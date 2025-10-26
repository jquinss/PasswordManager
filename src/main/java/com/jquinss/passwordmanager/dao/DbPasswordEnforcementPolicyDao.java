package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordEnforcementPolicy;
import com.jquinss.passwordmanager.util.password.PasswordStrengthCriteria;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbPasswordEnforcementPolicyDao implements PasswordEnforcementPolicyDao {

    private final DataSource dataSource;

    public DbPasswordEnforcementPolicyDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<PasswordEnforcementPolicy> getById(int id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetPasswordEnforcementPolicyByIdPreparedStatement(conn, id);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(createPasswordEnforcementPolicy(rs));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<PasswordEnforcementPolicy> getAllByUserProfileId(int userProfileId) throws SQLException {
        List<PasswordEnforcementPolicy> pwdEntities = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetAllPasswordEnforcementPoliciesByUserProfileIdPreparedStatement(conn, userProfileId);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pwdEntities.add(createPasswordEnforcementPolicy(rs));
            }
        }

        return pwdEntities;
    }

    @Override
    public void add(PasswordEnforcementPolicy pwdPolicy) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildAddPasswordEnforcementPolicyPreparedStatement(conn, pwdPolicy);) {

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
    public void update(PasswordEnforcementPolicy pwdPolicy) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildUpdatePasswordEnforcementPolicyPreparedStatement(conn, pwdPolicy)) {
            int i = ps.executeUpdate();
        }
    }

    @Override
    public void delete(PasswordEnforcementPolicy pwdPolicy) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildDeletePasswordEnforcementPolicyPreparedStatement(conn, pwdPolicy.getId())) {
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(List<PasswordEnforcementPolicy> pwdPolicies) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildDeleteAllPasswordEnforcementPoliciesPreparedStatement(conn, pwdPolicies)) {
            conn.setAutoCommit(false);
            ps.executeBatch();
            conn.setAutoCommit(true);
        }
    }

    private PreparedStatement buildGetAllPasswordEnforcementPoliciesByUserProfileIdPreparedStatement(Connection conn, int userProfileId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM password_enf_policy WHERE user_profile_id = ?");
        ps.setInt(1, userProfileId);

        return ps;
    }

    private PreparedStatement buildGetPasswordEnforcementPolicyByIdPreparedStatement(Connection conn, int id) throws SQLException {
        String statement = """
                SELECT * FROM password_enf_policy WHERE password_enf_policy_id = ?""";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, id);

        return ps;
    }

    private PasswordEnforcementPolicy createPasswordEnforcementPolicy(ResultSet rs) throws SQLException {
        PasswordStrengthCriteria pwdStrengthCriteria = new PasswordStrengthCriteria.Builder()
                .minLength(rs.getInt(3))
                .minLowerCaseChars(rs.getInt(4))
                .minUppercaseChars(rs.getInt(5))
                .minDigits(rs.getInt(6))
                .minSymbols(rs.getInt(7))
                .maxConsecutiveChars(rs.getInt(8)).build();

        PasswordEnforcementPolicy pwdPolicy = new PasswordEnforcementPolicy(rs.getInt(1), rs.getString(2), pwdStrengthCriteria);
        pwdPolicy.setDefaultPolicy(rs.getBoolean(9));
        pwdPolicy.setUserProfileId(rs.getInt(10));

        return pwdPolicy;
    }

    private PreparedStatement buildDeletePasswordEnforcementPolicyPreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM password_enf_policy WHERE password_enf_policy_id = ?");
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildAddPasswordEnforcementPolicyPreparedStatement(Connection conn, PasswordEnforcementPolicy pwdPolicy) throws SQLException {
        String statement = """
        INSERT INTO password_enf_policy (password_enf_policy_name, min_length, min_lower_case_chars, min_upper_case_chars,
        min_digits, min_symbols, max_consec_equal_chars, default_policy, user_profile_id) VALUES (?,?,?,?,?,?,?,?,?)""";

        return buildSetOperationPreparedStatement(conn, pwdPolicy, statement);
    }

    private PreparedStatement buildDeleteAllPasswordEnforcementPoliciesPreparedStatement(Connection conn, List<PasswordEnforcementPolicy> pwdPolicies) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM password_enf_policy WHERE password_enf_policy_id = ?");
        for (PasswordEnforcementPolicy pwdPolicy : pwdPolicies) {
            ps.setInt(1, pwdPolicy.getId());
            ps.addBatch();
        }
        return ps;
    }

    private PreparedStatement buildUpdatePasswordEnforcementPolicyPreparedStatement(Connection conn, PasswordEnforcementPolicy pwdPolicy) throws SQLException {
        String statement = """
                UPDATE password_enf_policy SET password_enf_policy_name=?, min_length=?, min_lower_case_chars=?, 
                 min_upper_case_chars=?, min_digits=?, min_symbols=?, max_consec_equal_chars=?, default_policy=? WHERE password_enf_policy_id=?""";

        PreparedStatement ps = buildSetOperationPreparedStatement(conn, pwdPolicy, statement);
        ps.setInt(9, pwdPolicy.getId());

        return ps;
    }

    private PreparedStatement buildSetOperationPreparedStatement(Connection conn, PasswordEnforcementPolicy pwdPolicy, String sqlStatement) throws SQLException {
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
        ps.setInt(9, pwdPolicy.getUserProfileId());

        return ps;
    }
}
