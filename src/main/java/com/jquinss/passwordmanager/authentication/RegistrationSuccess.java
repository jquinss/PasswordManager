package com.jquinss.passwordmanager.authentication;

public record RegistrationSuccess() implements RegistrationResult {
    @Override
    public String toString() {
        return "Successfully registered";
    }
}
