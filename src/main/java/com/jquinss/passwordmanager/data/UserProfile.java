package com.jquinss.passwordmanager.data;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private int id;
    private String name;
    private String password;
    private byte[] passwordSalt;
    private List<PasswordEntity> passwordEntities = new ArrayList<>();

    public UserProfile(int id, String name, String password) {
        this(name, password);
        this.id = id;
    }

    public UserProfile(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public List<PasswordEntity> getPasswordEntities() {
        return passwordEntities;
    }

    public void setPasswordEntities(List<PasswordEntity> passwordEntities) {
        this.passwordEntities = passwordEntities;
    }
}
