package com.jquinss.passwordmanager.app;

import com.jquinss.passwordmanager.dao.BackupsRepository;
import com.jquinss.passwordmanager.dao.VaultRepository;

public record AppContext(
        VaultRepository vaultRepository,
        BackupsRepository backupsRepository
) {
}
