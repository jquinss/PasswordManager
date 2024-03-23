package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PasswordEntityDao {
    Optional<PasswordEntity> getById(int id) throws SQLException;

    List<PasswordEntity> getAllByUserId(int id) throws SQLException;

    List<PasswordEntity> getAllByFolderId(int id) throws SQLException;

    void add(PasswordEntity pwdEntity) throws SQLException;

    void update(PasswordEntity pwdEntity) throws SQLException;

    void delete(PasswordEntity pwdEntity) throws SQLException;
}
