package com.jquinss.passwordmanager.data;

import com.jquinss.passwordmanager.util.password.PasswordStrengthCriteria;

public class PasswordPolicy {
    private int id;
    private String name;
    private PasswordStrengthCriteria passwordStrengthCriteria;

    public PasswordPolicy(int id, String name, PasswordStrengthCriteria passwordStrengthCriteria) {
        this.id = id;
        this.name = name;
        this.passwordStrengthCriteria = passwordStrengthCriteria;
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

    public void setName(String nam) {
        this.name = name;
    }

    public PasswordStrengthCriteria getPasswordStrengthCriteria() {
        return passwordStrengthCriteria;
    }

    public void setPasswordStrengthCriteria(PasswordStrengthCriteria passwordStrengthCriteria) {
        this.passwordStrengthCriteria = passwordStrengthCriteria;
    }
}
