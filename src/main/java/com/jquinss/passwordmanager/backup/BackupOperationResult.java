package com.jquinss.passwordmanager.backup;

public sealed interface BackupOperationResult permits BackupOperationFailure, BackupCreationSuccess,
                                                    BackupDeletionSuccess, BackupsLoadSuccess {
}
