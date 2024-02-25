package com.jquinss.passwordmanager.exceptions;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String error) {
        super(error);
    }
}
