package com.jquinss.passwordmanager.authentication;

public record RegistrationFailure(RegistrationStatus registrationStatus) implements RegistrationResult {
    public String toString() {
        return "Registration failed: " + this.registrationStatus.getMessage();
    }
}
