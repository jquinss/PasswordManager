package com.jquinss.passwordmanager.authentication;

import com.jquinss.passwordmanager.vault.repository.VaultRepository;

public record AuthenticationSuccess(VaultRepository vaultRepository) implements AuthenticationResult {
}
