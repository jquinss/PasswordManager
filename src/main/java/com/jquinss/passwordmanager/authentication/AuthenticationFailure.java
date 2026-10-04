package com.jquinss.passwordmanager.authentication;

public record AuthenticationFailure(AuthenticationStatus authenticationStatus) implements AuthenticationResult {
}
