package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordGeneratorPolicy;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PasswordGeneratorPolicyDao {
    Optional<PasswordGeneratorPolicy> getById(int id) throws SQLException;

    List<PasswordGeneratorPolicy> getAllByUserProfileId(int userProfileId) throws SQLException;

    void add(PasswordGeneratorPolicy pwdGenPolicy) throws SQLException;

    void update(PasswordGeneratorPolicy pwdGenPolicy) throws SQLException;

    void delete(PasswordGeneratorPolicy pwdGenPolicy) throws SQLException;

    void delete(List<PasswordGeneratorPolicy> pwdGenPolicies) throws SQLException;
}
