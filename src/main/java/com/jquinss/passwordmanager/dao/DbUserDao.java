package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class DbUserDao implements UserDao {
    private final DataSource dataSource;

    public DbUserDao(DataSource dataSource) { this.dataSource = dataSource; }

    @Override
    public Optional<User> getByName(String name) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetUserByNamePreparedStatement(conn, name);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(createUser(rs));
            }
        }

        return Optional.empty();
    }

    @Override
    public void add(User user) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildAddUserPreparedStatement(conn, user);) {
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Creating user failed");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
        }
    }

    private User createUser(ResultSet resultSet) throws SQLException {
        User user = new User(resultSet.getInt("user_profile_id"),
                resultSet.getString("user_name"),
                resultSet.getString("password"));
        user.setPasswordSalt(resultSet.getBytes("password_salt"));
        user.setPublicKey(resultSet.getBytes("public_key"));
        user.setPrivateKey(resultSet.getBytes("private_key"));
        user.setPrivateKeyIV(resultSet.getBytes("private_key_iv"));

        return user;
    }


    private PreparedStatement buildGetUserByNamePreparedStatement(Connection conn, String name) throws SQLException {
        String statement = "SELECT * FROM user WHERE user_name = ?";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setString(1, name);
        return ps;
    }

    private PreparedStatement buildAddUserPreparedStatement(Connection conn, User user) throws SQLException {
        String statement = "INSERT INTO user_profile (user_name, password, password_salt, public_key, private_key, " +
                "private_key_iv) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, user.getName());
        ps.setString(2, user.getPasswordHash());
        ps.setBytes(3, user.getPasswordSalt());
        ps.setBytes(4, user.getPublicKey());
        ps.setBytes(5, user.getPrivateKey());
        ps.setBytes(6, user.getPrivateKeyIV());
        return ps;
    }
}
