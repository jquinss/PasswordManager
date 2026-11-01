package com.jquinss.passwordmanager.backup;

public record BackupOperationFailure(BackupOperationStatus backupOperationStatus) implements BackupOperationResult {
}
