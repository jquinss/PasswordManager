package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordGeneratorPolicy;
import com.jquinss.passwordmanager.util.password.PasswordSpecs;

import javax.sql.DataSource;
import java.sql.*;
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildAddPasswordGeneratorPolicyPreparedStatement(conn, pwdGenPolicy);) {

            conn.setAutoCommit(false);
            ps.executeUpdate();

            Statement statement = conn.createStatement();

            try (ResultSet resultSet = statement.executeQuery("SELECT last_insert_rowid()")) {
                if (resultSet.next()) {
                    pwdGenPolicy.setId(resultSet.getInt(1));
                }
                conn.commit();
            }
        }
    }

    @Override
    public void update(PasswordGeneratorPolicy pwdGenPolicy) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildUpdatePasswordGenPolicyPreparedStatement(conn, pwdGenPolicy)) {
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(PasswordGeneratorPolicy pwdGenPolicy) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildDeletePasswordGenPolicyPreparedStatement(conn, pwdGenPolicy.getId())) {
            ps.executeUpdate();
        }
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
        pwdGenPolicy.setDefaultPolicy(rs.getBoolean(7));

        return pwdGenPolicy;
    }

    private PreparedStatement buildGetPasswordGeneratorPolicyByIdPreparedStatement(Connection conn, int id) throws SQLException {
        String statement = """
                SELECT * FROM password_gen_policy WHERE password_gen_policy_id = ?""";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildAddPasswordGeneratorPolicyPreparedStatement(Connection conn, PasswordGeneratorPolicy pwdGenPolicy) throws SQLException {
        String statement = """
        INSERT INTO password_gen_policy (password_gen_policy_name, lower_case_chars, upper_case_chars, digits, 
        symbols, default_policy) VALUES (?,?,?,?,?,?)""";

        return buildSetOperationPreparedStatement(conn, pwdGenPolicy, statement);
    }

    private PreparedStatement buildSetOperationPreparedStatement(Connection conn, PasswordGeneratorPolicy pwdGenPolicy,
                                                                 String sqlStatement) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sqlStatement);
        PasswordSpecs pwdSpecs = pwdGenPolicy.getPasswordSpecs();
        ps.setString(1, pwdGenPolicy.getName());
        ps.setInt(2, pwdSpecs.getNumLowerCaseChars());
        ps.setInt(3, pwdSpecs.getNumUpperCaseChars());
        ps.setInt(4, pwdSpecs.getNumDigits());
        ps.setInt(5, pwdSpecs.getNumSymbols());
        ps.setBoolean(6, pwdGenPolicy.isDefaultPolicy());

        return ps;
    }

    private PreparedStatement buildUpdatePasswordGenPolicyPreparedStatement(Connection conn, PasswordGeneratorPolicy pwdGenPolicy) throws SQLException {
        String statement = """
                UPDATE password_gen_policy SET password_gen_policy_name=?, lower_case_chars=?, upper_case_chars=?, 
                digits=?, symbols=?, default_policy=? WHERE password_policy_id=?""";

        PreparedStatement ps = buildSetOperationPreparedStatement(conn, pwdGenPolicy, statement);
        ps.setInt(7, pwdGenPolicy.getId());

        return ps;
    }

    private PreparedStatement buildDeletePasswordGenPolicyPreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM password_gen_policy WHERE password_gen_policy_id = ?");
        ps.setInt(1, id);

        return ps;
    }
}
