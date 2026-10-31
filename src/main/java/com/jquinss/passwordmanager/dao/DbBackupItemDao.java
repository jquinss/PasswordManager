package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.backup.BackupItem;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DbBackupItemDao implements BackupItemDao {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private final DataSource dataSource;

    public DbBackupItemDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public void add(BackupItem backupItem) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildAddPreparedStatement(conn, backupItem)) {
            conn.setAutoCommit(false);
            ps.executeUpdate();

            Statement statement = conn.createStatement();
            try (ResultSet resultSet = statement.executeQuery("SELECT last_insert_rowid()")) {
                if (resultSet.next()) {
                    backupItem.setId(resultSet.getInt(1));
                }
                conn.commit();
            }
        }
    }

    @Override
    public void delete(BackupItem backupItem) throws SQLException {
        try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = buildDeletePreparedStatement(conn, backupItem.getId())) {
            ResultSet rs = ps.executeQuery();
        }
    }

    @Override
    public List<BackupItem> getAll() throws SQLException {
        List<BackupItem> backupItems = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM backups");
        ResultSet rs = ps.executeQuery()) {
           while (rs.next()) {
               backupItems.add(createBackupItem(rs));
           }
        }

        return backupItems;
    }

    private PreparedStatement buildAddPreparedStatement(Connection conn, BackupItem backupItem) throws SQLException {
        String statement = "INSERT INTO backups (file_path, creation_date, file_hash) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setString(1, backupItem.getFilePath());
        ps.setString(2, backupItem.getCreationDateTime().format(dateTimeFormatter));
        ps.setString(3, backupItem.getFileHash());

        return ps;
    }

    private PreparedStatement buildDeletePreparedStatement(Connection conn, int backupItemId) throws SQLException {
        String statement = "DELETE FROM backups WHERE backup_id = ?";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, backupItemId);

        return ps;
    }

    private BackupItem createBackupItem(ResultSet rs) throws SQLException {
        return new BackupItem(rs.getInt(1), rs.getString(2),
                LocalDateTime.parse(rs.getString(3), dateTimeFormatter),
                rs.getString(4));
    }
}
