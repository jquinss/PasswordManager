package com.jquinss.passwordmanager.util.password;

public enum PasswordStrength {
    NONE, LOW, FAIR, GOOD, EXCELLENT;

    public int getValue() {
        int value = 0;
        switch (this) {
            case LOW -> value = 1;
            case FAIR -> value = 2;
            case GOOD -> value = 3;
            case EXCELLENT -> value = 4;
        }

        return value;
    }
}
