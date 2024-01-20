package com.jquinss.passwordmanager.data;

import java.time.LocalDateTime;

public class PasswordEntity {
    // TODO
    private int id;
    private int userProfileId;
    private int folderId;
    private int passwordPolicyId;
    private String name;
    private String username;
    private String password;
    private String comment = "";
    private boolean passwordExpires = false;
    private LocalDateTime expirationDate = LocalDateTime.now().plusMonths(6);

    public PasswordEntity(int id, String name, String username, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(int userProfileId) {
        this.userProfileId = userProfileId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
