package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordEnforcementPolicy;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PasswordEnforcementPolicyDao {
    Optional<PasswordEnforcementPolicy> getById(int id) throws SQLException;

    List<PasswordEnforcementPolicy> getAll() throws SQLException;

    void add(PasswordEnforcementPolicy pwdPolicy) throws SQLException;

    void update(PasswordEnforcementPolicy pwdPolicy) throws SQLException;

    void delete(PasswordEnforcementPolicy pwdPolicy) throws SQLException;
}
