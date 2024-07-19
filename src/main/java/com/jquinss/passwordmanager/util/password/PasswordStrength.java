package com.jquinss.passwordmanager.util.password;

public enum PasswordStrength {
    NONE, LOW, FAIR, GOOD, EXCELLENT;

    public int getValue() {
        return switch (this) {
            case NONE -> 0;
            case LOW -> 1;
            case FAIR -> 2;
            case GOOD -> 3;
            case EXCELLENT -> 4;
        };
    }
}
