package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.UserProfile;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbUserProfileDao implements UserProfileDao {
    private final DataSource dataSource;

    public DbUserProfileDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<UserProfile> getByName(String name) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetByNamePreparedStatement(conn, name);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(createUserProfile(rs));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<UserProfile> getAll() throws SQLException {
        List<UserProfile> userProfiles = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM user_profile");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                userProfiles.add(createUserProfile(rs));
            }
        }
        return userProfiles;
    }

    @Override
    public void add(UserProfile userProfile) throws SQLException {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = buildAddProfilePreparedStatement(conn, userProfile);) {
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Creating user failed");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    userProfile.setId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void delete(UserProfile userProfile) throws SQLException {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = buildRemoveProfilePreparedStatement(conn, userProfile)) {
            ps.executeUpdate();
        }
    }

    private UserProfile createUserProfile(ResultSet resultSet) throws SQLException {
        return new UserProfile(resultSet.getInt("user_profile_id"),
                resultSet.getString("user_profile_name"),
                resultSet.getString("master_password"));
    }


    private PreparedStatement buildGetByNamePreparedStatement(Connection conn, String name) throws SQLException {
        String statement = "SELECT * FROM user_profile WHERE user_profile_name = ?";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setString(1, name);
        return ps;
    }

    private PreparedStatement buildAddProfilePreparedStatement(Connection conn, UserProfile userProfile) throws SQLException {
        String statement = "INSERT INTO user_profile (user_profile_name, master_password) VALUES (?,?)";
        PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, userProfile.getName());
        ps.setString(2, userProfile.getPassword());
        return ps;
    }

    private PreparedStatement buildRemoveProfilePreparedStatement (Connection conn, UserProfile userProfile) throws SQLException {
        String statement = "DELETE FROM user_profile WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, userProfile.getId());
        return ps;
    }
}
