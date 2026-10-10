package com.jquinss.passwordmanager.data;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private int id;
    private String name;
    private boolean defaultProfile = false;

    private List<PasswordItem> passwordItems = new ArrayList<>();

    public UserProfile(String name, boolean defaultProfile) {
        this(name);
        this.defaultProfile = defaultProfile;
    }

    public UserProfile(int id, String name) {
        this(name);
        this.id = id;
    }

    public UserProfile(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
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

    public List<PasswordItem> getPasswordItems() {
        return passwordItems;
    }

    public void setPasswordItems(List<PasswordItem> passwordItems) {
        this.passwordItems = passwordItems;
    }

    @Override
    public String toString() {
        return name;
    }
}
