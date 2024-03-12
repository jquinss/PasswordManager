package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordEntity;

import java.sql.SQLException;
import java.util.List;

public class DbPasswordEntityDao implements PasswordEntityDao {
    @Override
    public List<PasswordEntity> getByUserId(int id) throws SQLException {
        return null;
    }

    @Override
    public void add(PasswordEntity pwdEntity) throws SQLException {

    }

    @Override
    public void update(PasswordEntity pwdEntity) throws SQLException {

    }

    @Override
    public void delete(PasswordEntity pwdEntity) throws SQLException {

    }
}
