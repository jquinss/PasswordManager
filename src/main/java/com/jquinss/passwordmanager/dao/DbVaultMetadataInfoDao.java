package com.jquinss.passwordmanager.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.jquinss.passwordmanager.vault.metadata.VaultMetadataInfo;

public class DbVaultMetadataInfoDao implements VaultMetadataInfoDao {
    private final DataSource dataSource;
    public DbVaultMetadataInfoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void upsert(VaultMetadataInfo vaultMetadataInfo) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = buildUpsertVaultMetadataInfoPreparedStatement(conn, vaultMetadataInfo);
            ps.executeUpdate();
        }
    }

    @Override
    public VaultMetadataInfo get() throws SQLException {
        String stmt = " SELECT vault_id, version, created_at, updated_at FROM vault_metadata WHERE id = 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(stmt);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new VaultMetadataInfo(rs.getString("vault_id"), rs.getInt("version"),
                        rs.getInt("created_at"), rs.getInt("updated_at"));
            }
            throw new IllegalStateException("Vault metadata information does not exist");
        }
    }

    private PreparedStatement buildUpsertVaultMetadataInfoPreparedStatement(Connection conn, VaultMetadataInfo vaultMetadataInfo) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO vault_metadata (vault_id, version, created_at, updated_at) 
            VALUES (?,?,?,?) ON CONFLICT(id) DO UPDATE SET
            vault_id = excluded.vault_id,
            version = excluded.version,
            created_at = excluded.created_at,
            updated_at = excluded.updated_at""");

        ps.setString(1, vaultMetadataInfo.vaultId());
        ps.setInt(2, vaultMetadataInfo.version());
        ps.setLong(3, vaultMetadataInfo.createTimestamp());
        ps.setLong(4, vaultMetadataInfo.updateTimeStamp());

        return ps;
    }
}
