package com.jquinss.passwordmanager.vault.metadata;

public record VaultMetadataInfo(String vaultId, int version,
                                long createTimestamp, long updateTimeStamp) {
}
