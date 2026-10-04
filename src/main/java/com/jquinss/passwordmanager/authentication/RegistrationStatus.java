package com.jquinss.passwordmanager.authentication;

public enum RegistrationStatus {
    VAULT_ALREADY_EXISTS,
    VAULT_PARTIALLY_EXISTS,
    METADATA_CREATION_FAILED,
    DB_CREATION_FAILED,
    VAULT_DELETION_FAILED;

    public String getMessage() {
        return switch (this) {
            case VAULT_ALREADY_EXISTS -> "A vault already exists";
            case VAULT_PARTIALLY_EXISTS -> "A partially created vault already exists";
            case METADATA_CREATION_FAILED -> "Failed to create vault metadata";
            case DB_CREATION_FAILED -> "Failed to create vault database";
            case VAULT_DELETION_FAILED -> "Failed to delete existing vault";
        };
    }
}
