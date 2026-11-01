package com.jquinss.passwordmanager.backup;

public record BackupOperationFailure(BackupOperationFailureStatus backupOperationFailureStatus) implements BackupOperationResult {
}
