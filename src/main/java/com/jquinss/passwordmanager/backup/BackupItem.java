package com.jquinss.passwordmanager.backup;

import java.time.LocalDateTime;

public class BackupItem {
    private int id;
    private final String filePath;
    private final LocalDateTime creationDateTime;
    private final String fileHash;

    public BackupItem(int id, String filePath, LocalDateTime creationDateTime, String fileHash) {
        this(filePath, creationDateTime, fileHash);
        this.id = id;
    }

    public BackupItem(String filePath, LocalDateTime creationDateTime, String fileHash) {
        this.filePath = filePath;
        this.creationDateTime = creationDateTime;
        this.fileHash = fileHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public String getFileHash() {
        return fileHash;
    }
}
