package com.jquinss.passwordmanager.data;

import java.time.LocalDate;

public class PasswordItem extends VaultItem implements Cloneable {
    private int userProfileId;
    private int folderId;
    private boolean passwordEnforcementPolicyEnabled = false;
    private int passwordEnforcementPolicyId;
    private String username;
    private String emailAddress;
    private String password;
    private String url;
    private boolean passwordExpires = false;
    private LocalDate expirationDate = LocalDate.now().plusMonths(6);

    public PasswordItem(int id, int folderId, String name, String password) {
        this(folderId, name, password);
        setId(id);
        this.password = password;
    }

    public PasswordItem(int folderId, String name, String password) {
        super(name);
        this.folderId = folderId;
        this.password = password;
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

    public boolean isPasswordEnforcementPolicyEnabled() {
        return passwordEnforcementPolicyEnabled;
    }

    public void setPasswordEnforcementPolicyEnabled(boolean passwordEnforcementPolicyEnabled) {
        this.passwordEnforcementPolicyEnabled = passwordEnforcementPolicyEnabled;
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
        PasswordItem pwdItem = (PasswordItem) super.clone();
        pwdItem.setUserProfileId(this.userProfileId);
        pwdItem.setFolderId(this.folderId);
        pwdItem.setPasswordEnforcementPolicyEnabled(this.passwordEnforcementPolicyEnabled);
        pwdItem.setPasswordEnforcementPolicyId(this.passwordEnforcementPolicyId);
        pwdItem.setUsername(this.username);
        pwdItem.setEmailAddress(this.emailAddress);
        pwdItem.setPassword(this.password);
        pwdItem.setUrl(this.url);
        pwdItem.setPasswordExpires(this.passwordExpires);
        pwdItem.setExpirationDate(this.expirationDate);

        return pwdItem;
    }
}
