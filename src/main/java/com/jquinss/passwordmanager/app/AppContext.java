package com.jquinss.passwordmanager.app;

import com.jquinss.passwordmanager.vault.repository.VaultRepository;

import java.util.Properties;

public record AppContext(VaultRepository vaultRepository, Properties sessionVariables) {}
