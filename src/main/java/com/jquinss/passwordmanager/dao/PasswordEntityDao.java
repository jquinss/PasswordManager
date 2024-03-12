package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordEntity;

import java.sql.SQLException;
import java.util.List;

public interface PasswordEntityDao {
    List<PasswordEntity> getByUserId(int id) throws SQLException;

    void add(PasswordEntity pwdEntity) throws SQLException;

    void update(PasswordEntity pwdEntity) throws SQLException;

    void delete(PasswordEntity pwdEntity) throws SQLException;
}
