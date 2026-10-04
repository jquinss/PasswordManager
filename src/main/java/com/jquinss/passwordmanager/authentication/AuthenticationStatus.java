package com.jquinss.passwordmanager.authentication;

public enum AuthenticationStatus {
    VAULT_NOT_FOUND,
    VAULT_PARTIALLY_EXISTS,
    DB_CONNECTION_FAILED,
    METADATA_INTEGRITY_FAILED,
    METADATA_READ_FAILED,
    DB_METADATA_VERIFICATION_FAILED;

    public String getMessage() {
        return switch (this) {
            case VAULT_NOT_FOUND -> "Vault does not exist";
            case VAULT_PARTIALLY_EXISTS -> "A partially created vault already exists";
            case DB_CONNECTION_FAILED -> "Connection to the database failed";
            case METADATA_READ_FAILED -> "Failed to read vault metadata";
            case METADATA_INTEGRITY_FAILED -> "Vault metadata integrity verification failed";
            case DB_METADATA_VERIFICATION_FAILED -> "Database vault metadata verification failed";
        };
    }
}
