package com.jquinss.passwordmanager.dao;

import java.sql.SQLException;
import com.jquinss.passwordmanager.vault.metadata.VaultMetadataInfo;

public interface VaultMetadataInfoDao {
    void upsert(VaultMetadataInfo vaultMetadataInfo) throws SQLException;

    VaultMetadataInfo get() throws SQLException;
}
