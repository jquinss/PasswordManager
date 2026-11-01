package com.jquinss.passwordmanager.backup;

public sealed interface BackupOperationResult permits BackupOperationFailure, BackupOperationSuccess {
}
