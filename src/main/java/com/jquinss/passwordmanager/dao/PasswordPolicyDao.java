package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordPolicy;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PasswordPolicyDao {
    Optional<PasswordPolicy> getById(int id) throws SQLException;

    List<PasswordPolicy> getAll() throws SQLException;

    void add(PasswordPolicy pwdPolicy) throws SQLException;

    void update(PasswordPolicy pwdPolicy) throws SQLException;

    void delete(PasswordPolicy pwdPolicy) throws SQLException;
}
