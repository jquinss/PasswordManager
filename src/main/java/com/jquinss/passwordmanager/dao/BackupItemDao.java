package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.backup.BackupItem;

import java.sql.SQLException;
import java.util.List;

public interface BackupItemDao {
    void add(BackupItem backupItem) throws SQLException;

    void delete(BackupItem backupItem) throws SQLException;

    List<BackupItem> getAll() throws SQLException;
}
