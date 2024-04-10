package com.jquinss.passwordmanager.data;

import java.time.LocalDate;
import java.util.Optional;

public class PasswordEntity extends DataEntity {
    private int userId;
    private int folderId;
    private int passwordPolicyId;
    private String username;
    private String emailAddress;
    private String password;
    private String url;
    private boolean passwordExpires = false;
    private LocalDate expirationDate = LocalDate.now().plusMonths(6);

    public PasswordEntity(int id, int folderId, String name, String password) {
        this(folderId, name, password);
        this.password = password;
    }

    public PasswordEntity(int folderId, String name, String password) {
        super(name);
        this.folderId = folderId;
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

    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    public void setUsername(String username) {
        this.username = username.isEmpty() ? null : username;
    }

    public Optional<String> getEmailAddress() {
        return Optional.ofNullable(emailAddress);
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress.isEmpty() ? null : emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Optional<String> getUrl() {
        return Optional.ofNullable(url);
    }

    public void setUrl(String url) {
        this.url = url.isEmpty() ? null : url;
    }

    public boolean isPasswordExpires() {
        return passwordExpires;
    }

    public void setPasswordExpires(boolean passwordExpires) {
        this.passwordExpires = passwordExpires;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
