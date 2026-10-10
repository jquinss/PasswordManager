package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordItem;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbPasswordItemDao implements PasswordItemDao {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd";
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private final DataSource dataSource;

    public DbPasswordItemDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<PasswordItem> getById(int id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetPasswordItemByIdPreparedStatement(conn, id);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(createPasswordItem(rs));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<PasswordItem> getAllByUserProfileId(int userProfileId) throws SQLException {
        List<PasswordItem> pwdItems = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = buildGetAllPasswordItemsByUserProfileIdPreparedStatement(conn, userProfileId);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pwdItems.add(createPasswordItem(rs));
            }
        }

        return pwdItems;
    }

    @Override
    public List<PasswordItem> getAllByPasswordEnforcementPolicyId(int passwordEnforcementPolicyId) throws SQLException {
        List<PasswordItem> pwdItems = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetAllPasswordItemsByEnforcementPolicyIdPreparedStatement(conn, passwordEnforcementPolicyId);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pwdItems.add(createPasswordItem(rs));
            }
        }

        return pwdItems;
    }

    @Override
    public List<PasswordItem> getAllByFolderId(int folderId) throws SQLException {
        List<PasswordItem> pwdItems = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetAllPasswordItemsByFolderIdPreparedStatement(conn, folderId);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pwdItems.add(createPasswordItem(rs));
            }
        }

        return pwdItems;
    }

    @Override
    public void add(PasswordItem pwdItem) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildAddPasswordItemPreparedStatement(conn, pwdItem);) {

            conn.setAutoCommit(false);
            ps.executeUpdate();

            Statement statement = conn.createStatement();

            try (ResultSet resultSet = statement.executeQuery("SELECT last_insert_rowid()")) {
                if (resultSet.next()) {
                    pwdItem.setId(resultSet.getInt(1));
                }
                conn.commit();
            }
        }
    }

    @Override
    public void update(PasswordItem pwdItem) throws SQLException {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = buildUpdatePasswordItemPreparedStatement(conn, pwdItem)) {
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(PasswordItem pwdItem) throws SQLException {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = buildDeletePasswordItemPreparedStatement(conn, pwdItem.getId())) {
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(List<PasswordItem> pwdItems) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildDeletePasswordItemsPreparedStatement(conn, pwdItems)) {
            conn.setAutoCommit(false);
            ps.executeBatch();
            conn.setAutoCommit(true);
        }
    }

    private PreparedStatement buildAddPasswordItemPreparedStatement(Connection conn, PasswordItem pwdItem) throws SQLException {
        String statement = """
        INSERT INTO password_Item (name, user_name, password, email_address,
        URL, description, expires, expiration_date, user_profile_id, folder_id, password_enf_policy_enabled, password_enf_policy_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)""";

        return buildSetOperationPreparedStatement(conn, pwdItem, statement);
    }

    private PreparedStatement buildUpdatePasswordItemPreparedStatement(Connection conn, PasswordItem pwdItem) throws SQLException {
        String statement = """
                 UPDATE password_Item SET name=?, user_name=?, password=?, email_address=?, URL=?, description=?, 
                 expires=?, expiration_date=?, user_profile_id=?, folder_id=?, password_enf_policy_enabled=?, password_enf_policy_id=? WHERE password_Item_id=?""";

        PreparedStatement ps = buildSetOperationPreparedStatement(conn, pwdItem, statement);
        ps.setInt(13, pwdItem.getId());

        return ps;
    }

    private PreparedStatement buildSetOperationPreparedStatement(Connection conn, PasswordItem pwdItem, String sqlStatement) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sqlStatement);

        ps.setString( 1, pwdItem.getName());
        ps.setString(2, pwdItem.getUsername());
        ps.setString(3, pwdItem.getPassword());
        ps.setString(4, pwdItem.getEmailAddress());
        ps.setString(5, pwdItem.getUrl());
        ps.setString(6, pwdItem.getDescription());
        ps.setBoolean(7, pwdItem.isPasswordExpires());
        ps.setString(8, pwdItem.getExpirationDate().format(dateTimeFormatter));
        ps.setInt(9, pwdItem.getUserProfileId());
        ps.setInt(10, pwdItem.getFolderId());
        ps.setBoolean(11, pwdItem.isPasswordEnforcementPolicyEnabled());
        ps.setInt(12, pwdItem.getPasswordEnforcementPolicyId());

        return ps;
    }

    private PreparedStatement buildGetPasswordItemByIdPreparedStatement(Connection conn, int id) throws SQLException {
        String statement = """
                SELECT * FROM password_Item WHERE password_Item_id = ?""";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildGetAllPasswordItemsByUserProfileIdPreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM password_Item WHERE user_profile_id = ?");
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildGetAllPasswordItemsByFolderIdPreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM password_Item WHERE folder_id = ?");
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildGetAllPasswordItemsByEnforcementPolicyIdPreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM password_Item WHERE password_enf_policy_id = ? AND password_enf_policy_enabled = 1");
        ps.setInt(1, id);

        return ps;
    }

    private PasswordItem createPasswordItem(ResultSet rs) throws SQLException {
        PasswordItem pwdItem = new PasswordItem(rs.getInt(1), rs.getInt(11),
                rs.getString(2), rs.getString(4));
        pwdItem.setUsername(rs.getString(3));
        pwdItem.setEmailAddress(rs.getString(5));
        pwdItem.setUrl(rs.getString(6));
        pwdItem.setDescription(rs.getString(7));
        pwdItem.setPasswordExpires(rs.getBoolean(8));
        pwdItem.setExpirationDate(LocalDate.parse(rs.getString(9), dateTimeFormatter));
        pwdItem.setUserProfileId(rs.getInt(10));
        pwdItem.setPasswordEnforcementPolicyEnabled(rs.getBoolean(12));
        pwdItem.setPasswordEnforcementPolicyId(rs.getInt(13));

        return pwdItem;
    }

    private PreparedStatement buildDeletePasswordItemPreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM password_Item WHERE password_Item_id = ?");
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildDeletePasswordItemsPreparedStatement(Connection conn, List<PasswordItem> pwdItems) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM password_Item WHERE password_Item_id = ?");
        for (PasswordItem pwdItem : pwdItems) {
            ps.setInt(1, pwdItem.getId());
            ps.addBatch();
        }
        return ps;
    }
}
