package com.jquinss.passwordmanager.data;

import java.time.LocalDateTime;

public class PasswordEntity extends DataEntity {
    // TODO
    private int userId;
    private int folderId;
    private int passwordPolicyId;
    private String username;
    private String password;
    private String url;
    private boolean passwordExpires = false;
    private LocalDateTime expirationDate = LocalDateTime.now().plusMonths(6);

    public PasswordEntity(int id, int folderId, String name, String username, String password) {
        super(id, name);
        this.folderId = folderId;
        this.username = username;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getPasswordPolicyId() {
        return passwordPolicyId;
    }

    public void setPasswordPolicyId(int passwordPolicyId) {
        this.passwordPolicyId = passwordPolicyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPasswordExpires() {
        return passwordExpires;
    }

    public void setPasswordExpires(boolean passwordExpires) {
        this.passwordExpires = passwordExpires;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }
}
