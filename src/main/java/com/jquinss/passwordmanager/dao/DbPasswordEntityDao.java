package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordEntity;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbPasswordEntityDao implements PasswordEntityDao {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd";
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private final DataSource dataSource;

    public DbPasswordEntityDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<PasswordEntity> getById(int id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetPasswordEntityByIdPreparedStatement(conn, id);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(createPasswordEntity(rs));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<PasswordEntity> getAllByUserId(int id) throws SQLException {
        List<PasswordEntity> pwdEntities = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = buildGetAllPasswordEntitiesByUserIdPreparedStatement(conn, id);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pwdEntities.add(createPasswordEntity(rs));
            }
        }

        return pwdEntities;
    }

    @Override
    public List<PasswordEntity> getAllByEnforcementPolicyId(int id) throws SQLException {
        List<PasswordEntity> pwdEntities = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetAllPasswordEntitiesByEnforcementPolicyIdPreparedStatement(conn, id);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pwdEntities.add(createPasswordEntity(rs));
            }
        }

        return pwdEntities;
    }

    @Override
    public List<PasswordEntity> getAllByFolderId(int id) throws SQLException {
        List<PasswordEntity> pwdEntities = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetAllPasswordEntitiesByFolderIdPreparedStatement(conn, id);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pwdEntities.add(createPasswordEntity(rs));
            }
        }

        return pwdEntities;
    }

    @Override
    public void add(PasswordEntity pwdEntity) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildAddPasswordEntityPreparedStatement(conn, pwdEntity);) {

            conn.setAutoCommit(false);
            ps.executeUpdate();

            Statement statement = conn.createStatement();

            try (ResultSet resultSet = statement.executeQuery("SELECT last_insert_rowid()")) {
                if (resultSet.next()) {
                    pwdEntity.setId(resultSet.getInt(1));
                }
                conn.commit();
            }
        }
    }

    @Override
    public void update(PasswordEntity pwdEntity) throws SQLException {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = buildUpdatePasswordEntityPreparedStatement(conn, pwdEntity)) {
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(PasswordEntity pwdEntity) throws SQLException {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = buildDeletePasswordEntityPreparedStatement(conn, pwdEntity.getId())) {
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(List<PasswordEntity> pwdEntities) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildDeletePasswordEntitiesPreparedStatement(conn, pwdEntities)) {
            conn.setAutoCommit(false);
            ps.executeBatch();
            conn.setAutoCommit(true);
        }
    }

    private PreparedStatement buildAddPasswordEntityPreparedStatement(Connection conn, PasswordEntity pwdEntity) throws SQLException {
        String statement = """
        INSERT INTO password_entity (name, user_name, password, email_address,
        URL, description, expires, expiration_date, user_id, folder_id, password_enf_policy_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)""";

        return buildSetOperationPreparedStatement(conn, pwdEntity, statement);
    }

    private PreparedStatement buildUpdatePasswordEntityPreparedStatement(Connection conn, PasswordEntity pwdEntity) throws SQLException {
        String statement = """
                 UPDATE password_entity SET name=?, user_name=?, password=?, email_address=?, URL=?, description=?, 
                 expires=?, expiration_date=?, user_id=?, folder_id=?, password_enf_policy_id=? WHERE password_entity_id=?""";

        PreparedStatement ps = buildSetOperationPreparedStatement(conn, pwdEntity, statement);
        ps.setInt(12, pwdEntity.getId());

        return ps;
    }

    private PreparedStatement buildSetOperationPreparedStatement(Connection conn, PasswordEntity pwdEntity, String sqlStatement) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sqlStatement);
        ps.setString( 1, pwdEntity.getName());
        ps.setString(2, pwdEntity.getUsername());
        ps.setString(3, pwdEntity.getPassword());
        ps.setString(4, pwdEntity.getEmailAddress());
        ps.setString(5, pwdEntity.getUrl());
        ps.setString(6, pwdEntity.getDescription());
        ps.setBoolean(7, pwdEntity.isPasswordExpires());
        ps.setString(8, pwdEntity.getExpirationDate().format(dateTimeFormatter));
        ps.setInt(9, pwdEntity.getUserId());
        ps.setInt(10, pwdEntity.getFolderId());
        ps.setInt(11, pwdEntity.getPasswordPolicyId());

        return ps;
    }

    private PreparedStatement buildGetPasswordEntityByIdPreparedStatement(Connection conn, int id) throws SQLException {
        String statement = """
                SELECT * FROM password_entity WHERE password_entity_id = ?""";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildGetAllPasswordEntitiesByUserIdPreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM password_entity WHERE user_id = ?");
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildGetAllPasswordEntitiesByFolderIdPreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM password_entity WHERE folder_id = ?");
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildGetAllPasswordEntitiesByEnforcementPolicyIdPreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM password_entity WHERE password_enf_policy_id = ?");
        ps.setInt(1, id);

        return ps;
    }

    private PasswordEntity createPasswordEntity(ResultSet rs) throws SQLException {
        PasswordEntity pwdEntity = new PasswordEntity(rs.getInt(1), rs.getInt(11),
                rs.getString(2), rs.getString(4));
        pwdEntity.setUsername(rs.getString(3));
        pwdEntity.setEmailAddress(rs.getString(5));
        pwdEntity.setUrl(rs.getString(6));
        pwdEntity.setDescription(rs.getString(7));
        pwdEntity.setPasswordExpires(rs.getBoolean(8));
        pwdEntity.setExpirationDate(LocalDate.parse(rs.getString(9), dateTimeFormatter));
        pwdEntity.setUserId(rs.getInt(10));
        pwdEntity.setPasswordPolicyId(rs.getInt(12));

        return pwdEntity;
    }

    private PreparedStatement buildDeletePasswordEntityPreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM password_entity WHERE password_entity_id = ?");
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildDeletePasswordEntitiesPreparedStatement(Connection conn, List<PasswordEntity> pwdEntities) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM password_entity WHERE password_entity_id = ?");
        for (PasswordEntity pwdEntity : pwdEntities) {
            ps.setInt(1, pwdEntity.getId());
            ps.addBatch();
        }
        return ps;
    }
}
