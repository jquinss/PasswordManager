package com.jquinss.passwordmanager.data;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String name;
    private String passwordHash;
    private byte[] passwordSalt;
    private byte[] publicKey;
    private byte[] privateKey;
    private byte[] privateKeyIV;

    private List<PasswordEntity> passwordEntities = new ArrayList<>();

    public User(int id, String name, String passwordHash) {
        this(name, passwordHash);
        this.id = id;
    }

    public User(String name, String password) {
        this.name = name;
        this.passwordHash = password;
    }

    public int getId() {
        return id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
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
