package com.jquinss.passwordmanager.data;

import com.jquinss.passwordmanager.util.password.PasswordSpecs;

public class PasswordGeneratorPolicy {
    private int id;
    private String name;
    private boolean defaultPolicy = false;
    private PasswordSpecs passwordSpecs;
    public PasswordGeneratorPolicy(String name, PasswordSpecs passwordSpecs) {
        this.name = name;
        this.passwordSpecs = passwordSpecs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefaultPolicy() {
        return defaultPolicy;
    }

    public void setDefaultPolicy(boolean defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
    }

    public PasswordSpecs getPasswordSpecs() {
        return passwordSpecs;
    }

    public void setPasswordSpecs(PasswordSpecs passwordSpecs) {
        this.passwordSpecs = passwordSpecs;
    }
}
