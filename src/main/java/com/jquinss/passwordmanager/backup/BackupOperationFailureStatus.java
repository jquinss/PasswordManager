package com.jquinss.passwordmanager.backup;

public enum BackupOperationFailureStatus {
    VAULT_NOT_FOUND,
    VAULT_PARTIALLY_EXISTS,
    VAULT_ALREADY_EXISTS,
    BACKUP_ALREADY_EXISTS,
    DB_CONNECTION_FAILED,
    BACKUP_INTEGRITY_FAILED
}
