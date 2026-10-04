package com.jquinss.passwordmanager.authentication;

public sealed interface AuthenticationResult permits AuthenticationFailure, AuthenticationSuccess {
}
