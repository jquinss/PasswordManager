package com.jquinss.passwordmanager.data;

import com.jquinss.passwordmanager.util.password.PasswordSpecs;

public class PasswordGeneratorPolicy extends PasswordPolicy {
    private PasswordSpecs passwordSpecs;
    public PasswordGeneratorPolicy(String name, PasswordSpecs passwordSpecs) {
        setName(name);
        this.passwordSpecs = passwordSpecs;
    }

    public PasswordSpecs getPasswordSpecs() {
        return passwordSpecs;
    }

    public void setPasswordSpecs(PasswordSpecs passwordSpecs) {
        this.passwordSpecs = passwordSpecs;
    }
}
