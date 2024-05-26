package com.jquinss.passwordmanager.data;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class PasswordEntity extends DataEntity implements Cloneable {
    private int userId;
    private int folderId;
    private int passwordEnforcementPolicyId;
    private String username;
    private String emailAddress;
    private String password;
    private String url;
    private boolean passwordExpires = false;
    private LocalDate expirationDate = LocalDate.now().plusMonths(6);

    public PasswordEntity(int id, int folderId, String name, String password) {
        this(folderId, name, password);
        setId(id);
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

    public int getPasswordEnforcementPolicyId() {
        return passwordEnforcementPolicyId;
    }

    public void setPasswordEnforcementPolicyId(int passwordEnforcementPolicyId) {
        this.passwordEnforcementPolicyId = passwordEnforcementPolicyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null || username.isEmpty() ? null : username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress == null || emailAddress.isEmpty() ? null : emailAddress;
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
        this.url = url == null || url.isEmpty() ? null : url;
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

    @Override
    public Object clone() {
        PasswordEntity pwdEntity = (PasswordEntity) super.clone();
        pwdEntity.setUserId(this.userId);
        pwdEntity.setFolderId(this.folderId);
        pwdEntity.setPasswordEnforcementPolicyId(this.passwordEnforcementPolicyId);
        pwdEntity.setUsername(this.username);
        pwdEntity.setEmailAddress(this.emailAddress);
        pwdEntity.setPassword(this.password);
        pwdEntity.setUrl(this.url);
        pwdEntity.setPasswordExpires(this.passwordExpires);
        pwdEntity.setExpirationDate(this.expirationDate);

        return pwdEntity;
    }
}
