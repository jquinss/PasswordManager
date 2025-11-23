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
                if (resultSet.next()) {
                    userProfile.setId(resultSet.getInt(1));
                }
                conn.commit();
            }
        }
    }

    @Override
    public void update(UserProfile userProfile) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildUpdateUserProfilePreparedStatement(conn, userProfile);) {
            ps.executeUpdate();
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
    public List<UserProfile> getAllUserProfiles() throws SQLException {
        List<UserProfile> userProfiles = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetAllUserProfilesPreparedStatement(conn);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                userProfiles.add(createSimpleUserProfile(rs));
            }
        }

        return userProfiles;
    }

    @Override
    public Optional<UserProfile> getDefaultUserProfile() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetDefaultUserProfilePreparedStatement(conn);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(createSimpleUserProfile(rs));
            }
        }

        return Optional.empty();
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

    // method used to the situations where we do not need all the user profile attributes. In this case
    // we only load the username and the default attributes
    private UserProfile createSimpleUserProfile(ResultSet resultSet) throws SQLException {
        UserProfile userProfile = new UserProfile(resultSet.getInt("user_profile_id"),
                resultSet.getString("user_profile_name"));
        userProfile.setDefaultProfile(resultSet.getBoolean("default_profile"));

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
                "private_key_iv) VALUES (?,?,?,?,?,?,?)";
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

    private PreparedStatement buildUpdateUserProfilePreparedStatement(Connection conn, UserProfile userProfile) throws SQLException {
        String statement = "UPDATE user_profile SET default_profile=? WHERE user_profile_id=?";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setBoolean(1, userProfile.isDefaultProfile());
        ps.setInt(2, userProfile.getId());
        return ps;
    }

    private PreparedStatement buildDeleteUserProfilePreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM user_profile WHERE user_profile_id = ?");
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildGetAllUserProfilesPreparedStatement(Connection conn) throws SQLException {
        String statement = "SELECT user_profile_id, user_profile_name, default_profile FROM user_profile";
        return conn.prepareStatement(statement);
    }

    private PreparedStatement buildGetDefaultUserProfilePreparedStatement(Connection conn) throws SQLException {
        String statement = "SELECT user_profile_id, user_profile_name, default_profile FROM user_profile WHERE default_profile = TRUE";
        return conn.prepareStatement(statement);
    }
}
