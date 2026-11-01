package com.jquinss.passwordmanager.backup;

import java.util.List;

public record BackupsLoadSuccess(List<BackupItem> backups) implements BackupOperationResult {
}
