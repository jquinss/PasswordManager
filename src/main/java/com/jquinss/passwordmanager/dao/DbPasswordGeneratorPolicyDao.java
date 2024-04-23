package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordGeneratorPolicy;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DbPasswordGeneratorPolicyDao implements PasswordGeneratorPolicyDao {
    @Override
    public Optional<PasswordGeneratorPolicy> getById(int id) throws SQLException {
        // TDDO
    }

    @Override
    public List<PasswordGeneratorPolicy> getAll() throws SQLException {
        // TDDO
    }

    @Override
    public void add(PasswordGeneratorPolicy pwdGenPolicy) throws SQLException {
        // TDDO
    }

    @Override
    public void update(PasswordGeneratorPolicy pwdGenPolicy) throws SQLException {
        // TDDO
    }

    @Override
    public void delete(PasswordGeneratorPolicy pwdGenPolicy) throws SQLException {
        // TDDO
    }
}
