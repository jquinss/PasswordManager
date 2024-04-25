package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordGeneratorPolicy;
import com.jquinss.passwordmanager.util.password.PasswordSpecs;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbPasswordGeneratorPolicyDao implements PasswordGeneratorPolicyDao {
    public final DataSource dataSource;
    public DbPasswordGeneratorPolicyDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public Optional<PasswordGeneratorPolicy> getById(int id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetPasswordGeneratorPolicyByIdPreparedStatement(conn, id);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(createPasswordGeneratorPolicy(rs));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<PasswordGeneratorPolicy> getAll() throws SQLException {
        List<PasswordGeneratorPolicy> pwdGeneratorPolicies = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM password_gen_policy");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pwdGeneratorPolicies.add(createPasswordGeneratorPolicy(rs));
            }
        }

        return pwdGeneratorPolicies;
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

    private PasswordGeneratorPolicy createPasswordGeneratorPolicy(ResultSet rs) throws SQLException {
        PasswordSpecs passwordSpecs = new PasswordSpecs.Builder()
                .numLowerCaseChars(rs.getInt(3))
                .numUppercaseChars(rs.getInt(4))
                .numDigits(rs.getInt(5))
                .numSymbols(rs.getInt(6))
                .build();

        PasswordGeneratorPolicy pwdGenPolicy = new PasswordGeneratorPolicy(rs.getString(2), passwordSpecs);
        pwdGenPolicy.setId(rs.getInt(1));

        return pwdGenPolicy;
    }

    private PreparedStatement buildGetPasswordGeneratorPolicyByIdPreparedStatement(Connection conn, int id) throws SQLException {
        String statement = """
                SELECT * FROM password_gen_policy WHERE password_gen_policy_id = ?""";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, id);

        return ps;
    }
}
