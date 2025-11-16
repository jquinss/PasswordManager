package com.jquinss.passwordmanager.data;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private int id;
    private String name;
    private byte[] passwordHash;
    private byte[] passwordSalt;
    private byte[] publicKey;
    private byte[] privateKey;
    private byte[] privateKeyIV;
    private boolean defaultProfile = false;

    private List<PasswordEntity> passwordEntities = new ArrayList<>();

    public UserProfile(int id, String name, byte[] passwordHash) {
        this(name, passwordHash);
        this.id = id;
    }

    public UserProfile(String name, byte[] password) {
        this.name = name;
        this.passwordHash = password;
    }

    public int getId() {
        return id;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefaultProfile() {
        return defaultProfile;
    }

    public void setDefaultProfile(boolean defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(byte[] passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public byte[] getPrivateKeyIV() {
        return privateKeyIV;
    }

    public void setPrivateKeyIV(byte[] privateKeyIV) {
        this.privateKeyIV = privateKeyIV;
    }

    public List<PasswordEntity> getPasswordEntities() {
        return passwordEntities;
    }

    public void setPasswordEntities(List<PasswordEntity> passwordEntities) {
        this.passwordEntities = passwordEntities;
    }
}
