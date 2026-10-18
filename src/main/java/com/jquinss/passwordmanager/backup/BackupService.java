package com.jquinss.passwordmanager.backup;

import com.jquinss.passwordmanager.vault.repository.BackupsRepository;

public class BackupService {
    private final BackupsRepository backupsRepository;
    public BackupService(BackupsRepository backupsRepository) {
        this.backupsRepository = backupsRepository;
    }

    public BackupItem createBackup(String vaultMetadataFilePath, String vaultDbFilePath, String backupFilePath) {
        // TODO
        return null;
    }

    public void deleteBackup(BackupItem backupItem) {
        // TODO
    }

    public BackupItem importBackup(String srcBackupFilePath, String dstBackupFilePath) {
        // TODO
        return null;
    }
}
