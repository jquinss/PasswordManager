package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> getByName(String name) throws SQLException;
    void add(User user) throws SQLException;
}
