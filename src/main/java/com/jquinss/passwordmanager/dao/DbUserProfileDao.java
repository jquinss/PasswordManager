package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.UserProfile;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbUserProfileDao implements UserProfileDao {
    private final DataSource dataSource;

    public DbUserProfileDao(DataSource dataSource) { this.dataSource = dataSource; }

    @Override
    public Optional<UserProfile> getByName(String name) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetUserProfileByNamePreparedStatement(conn, name);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(createUserProfile(rs));
            }
        }

        return Optional.empty();
    }

    @Override
    public void add(UserProfile userProfile) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildAddUserProfilePreparedStatement(conn, userProfile);) {

            conn.setAutoCommit(false);
            ps.executeUpdate();

            Statement statement = conn.createStatement();

            try (ResultSet resultSet = statement.executeQuery("SELECT last_insert_rowid()")) {
                //ResultSetMetaData metaData = resultSet.getMetaData();
                if (resultSet.next()) {
                    userProfile.setId(resultSet.getInt(1));
                }
                conn.commit();
            }
        }
    }

    @Override
    public void delete(UserProfile userProfile) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildDeleteUserProfilePreparedStatement(conn, userProfile.getId());) {
            ps.executeUpdate();
        }
    }

    @Override
    public List<String> getAllUserProfileNames() throws SQLException {
        List<String> profileNames = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetAllUserProfileNamesPreparedStatement(conn);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                profileNames.add(rs.getString(2));
            }
        }

        return profileNames;
    }

    private UserProfile createUserProfile(ResultSet resultSet) throws SQLException {
        UserProfile userProfile = new UserProfile(resultSet.getInt("user_profile_id"),
                resultSet.getString("user_profile_name"),
                resultSet.getBytes("password"));
        userProfile.setDefaultProfile(resultSet.getBoolean("default_profile"));
        userProfile.setPasswordSalt(resultSet.getBytes("password_salt"));
        userProfile.setPublicKey(resultSet.getBytes("public_key"));
        userProfile.setPrivateKey(resultSet.getBytes("private_key"));
        userProfile.setPrivateKeyIV(resultSet.getBytes("private_key_iv"));

        return userProfile;
    }


    private PreparedStatement buildGetUserProfileByNamePreparedStatement(Connection conn, String name) throws SQLException {
        String statement = "SELECT * FROM user_profile WHERE user_profile_name = ?";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setString(1, name);
        return ps;
    }

    private PreparedStatement buildAddUserProfilePreparedStatement(Connection conn, UserProfile userProfile) throws SQLException {
        String statement = "INSERT INTO user_profile (user_profile_name, default_profile, password, password_salt, public_key, private_key, " +
                "private_key_iv) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setString(1, userProfile.getName());
        ps.setBoolean(2, userProfile.isDefaultProfile());
        ps.setBytes(3, userProfile.getPasswordHash());
        ps.setBytes(4, userProfile.getPasswordSalt());
        ps.setBytes(5, userProfile.getPublicKey());
        ps.setBytes(6, userProfile.getPrivateKey());
        ps.setBytes(7, userProfile.getPrivateKeyIV());
        return ps;
    }

    private PreparedStatement buildDeleteUserProfilePreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM user_profile WHERE user_profile_id = ?");
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildGetAllUserProfileNamesPreparedStatement(Connection conn) throws SQLException {
        String statement = "SELECT * FROM user_profile";
        return conn.prepareStatement(statement);
    }
}
