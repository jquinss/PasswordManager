package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordPolicy;
import com.jquinss.passwordmanager.util.password.PasswordStrengthCriteria;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbPasswordPolicyDao implements PasswordPolicyDao {

    private final DataSource dataSource;

    public DbPasswordPolicyDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<PasswordPolicy> getById(int id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetPasswordPolicyByIdPreparedStatement(conn, id);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(createPasswordPolicy(rs));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<PasswordPolicy> getAll() throws SQLException {
        List<PasswordPolicy> pwdEntities = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM password_policy");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pwdEntities.add(createPasswordPolicy(rs));
            }
        }

        return pwdEntities;
    }

    @Override
    public void add(PasswordPolicy pwdPolicy) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildAddPasswordPolicyPreparedStatement(conn, pwdPolicy);) {

            conn.setAutoCommit(false);
            ps.executeUpdate();

            Statement statement = conn.createStatement();

            try (ResultSet resultSet = statement.executeQuery("SELECT last_insert_rowid()")) {
                if (resultSet.next()) {
                    pwdPolicy.setId(resultSet.getInt(1));
                }
                conn.commit();
            }
        }
    }

    @Override
    public void update(PasswordPolicy pwdPolicy) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildUpdatePasswordPolicyPreparedStatement(conn, pwdPolicy)) {
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(PasswordPolicy pwdPolicy) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildDeletePasswordPolicyPreparedStatement(conn, pwdPolicy.getId())) {
            ps.executeUpdate();
        }
    }

    private PreparedStatement buildGetPasswordPolicyByIdPreparedStatement(Connection conn, int id) throws SQLException {
        String statement = """
                SELECT * FROM password_policy WHERE password_policy_id = ?""";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, id);

        return ps;
    }

    private PasswordPolicy createPasswordPolicy(ResultSet rs) throws SQLException {
        PasswordStrengthCriteria pwdStrengthCriteria = new PasswordStrengthCriteria.Builder()
                .minLength(rs.getInt(3))
                .minLowerCaseChars(rs.getInt(4))
                .minUppercaseChars(rs.getInt(5))
                .minDigits(rs.getInt(6))
                .minSymbols(rs.getInt(7))
                .maxConsecutiveChars(rs.getInt(8)).build();

        PasswordPolicy pwdPolicy = new PasswordPolicy(rs.getInt(1), rs.getString(2), pwdStrengthCriteria);
        pwdPolicy.setDefaultPolicy(rs.getBoolean(9));

        return pwdPolicy;
    }

    private PreparedStatement buildDeletePasswordPolicyPreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM password_policy WHERE password_policy_id = ?");
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildAddPasswordPolicyPreparedStatement(Connection conn, PasswordPolicy pwdPolicy) throws SQLException {
        String statement = """
        INSERT INTO password_policy (password_policy_name, min_length, min_lower_case_chars, min_upper_case_chars,
        min_digits, min_symbols, max_consec_equal_chars, default_policy) VALUES (?,?,?,?,?,?,?,?)""";

        return buildSetOperationPreparedStatement(conn, pwdPolicy, statement);
    }

    private PreparedStatement buildUpdatePasswordPolicyPreparedStatement(Connection conn, PasswordPolicy pwdPolicy) throws SQLException {
        String statement = """
                UPDATE password_policy SET password_policy_name=?, min_length=?, min_lower_case_chars=?, 
                 min_upper_case_chars=?, min_digits=?, min_symbols=?, max_consec_equal_chars=?, default_policy=? 
                 WHERE password_policy_id = ?""";

        PreparedStatement ps = buildSetOperationPreparedStatement(conn, pwdPolicy, statement);
        ps.setInt(9, pwdPolicy.getId());

        return buildSetOperationPreparedStatement(conn, pwdPolicy, statement);
    }

    private PreparedStatement buildSetOperationPreparedStatement(Connection conn, PasswordPolicy pwdPolicy, String sqlStatement) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sqlStatement);
        PasswordStrengthCriteria pwdStrengthCriteria = pwdPolicy.getPasswordStrengthCriteria();
        ps.setString(1, pwdPolicy.getName());
        ps.setInt(2, pwdStrengthCriteria.getMinLength());
        ps.setInt(3, pwdStrengthCriteria.getMinLowerCaseChars());
        ps.setInt(4, pwdStrengthCriteria.getMinUpperCaseChars());
        ps.setInt(5, pwdStrengthCriteria.getMinDigits());
        ps.setInt(6, pwdStrengthCriteria.getMinSymbols());
        ps.setInt(7, pwdStrengthCriteria.getMaxConsecutiveEqualChars());
        ps.setBoolean(8, pwdPolicy.isDefaultPolicy());

        return ps;
    }
}
