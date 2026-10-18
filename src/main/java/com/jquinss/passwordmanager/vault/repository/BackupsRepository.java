package com.jquinss.passwordmanager.vault.repository;
import com.jquinss.passwordmanager.dao.BackupItemDao;
import com.jquinss.passwordmanager.dao.DbBackupItemDao;
import com.jquinss.passwordmanager.backup.BackupItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class BackupsRepository {
    private static final String CREATE_BACKUP_TABLE_STATEMENT = """
            CREATE TABLE IF NOT EXITS backups (backup_id INTEGER PRIMARY KEY, file_path TEXT NOT NULL,
            creation_date TEXT NOT NULL, file_hash TEXT NOT NULL);
            """;
    private final DataSource dataSource;
    private final BackupItemDao backupItemDao;

    public BackupsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        backupItemDao = new DbBackupItemDao(dataSource);
    }

    public void addBackupItem(BackupItem backupItem) throws SQLException {
        backupItemDao.add(backupItem);
    }

    public void deleteBackupItem(BackupItem backupItem) throws SQLException {
        backupItemDao.delete(backupItem);
    }

    public List<BackupItem> getAllBackupItems() throws SQLException {
        return backupItemDao.getAll();
    }

    public void initialize() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null) {
                conn.setAutoCommit(false);
                Statement stmt = conn.createStatement();
                stmt.execute(CREATE_BACKUP_TABLE_STATEMENT);
                conn.commit();
                conn.setAutoCommit(true);
            }
        }
    }
}
