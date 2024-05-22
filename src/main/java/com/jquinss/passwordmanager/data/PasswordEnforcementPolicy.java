package com.jquinss.passwordmanager.data;

import com.jquinss.passwordmanager.util.password.PasswordStrengthCriteria;

public class PasswordEnforcementPolicy extends PasswordPolicy {
    private PasswordStrengthCriteria passwordStrengthCriteria;

    public PasswordEnforcementPolicy(int id, String name, PasswordStrengthCriteria passwordStrengthCriteria) {
        this(name, passwordStrengthCriteria);
        setId(id);
    }

    public PasswordEnforcementPolicy(String name, PasswordStrengthCriteria passwordStrengthCriteria) {
        setName(name);
        this.passwordStrengthCriteria = passwordStrengthCriteria;
    }

    public PasswordStrengthCriteria getPasswordStrengthCriteria() {
        return passwordStrengthCriteria;
    }

    public void setPasswordStrengthCriteria(PasswordStrengthCriteria passwordStrengthCriteria) {
        this.passwordStrengthCriteria = passwordStrengthCriteria;
    }
}
