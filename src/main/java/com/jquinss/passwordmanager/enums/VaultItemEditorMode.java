package com.jquinss.passwordmanager.enums;

import com.jquinss.passwordmanager.data.VaultItem;

import java.util.Optional;

public enum VaultItemEditorMode {
    CREATE, EDIT, HIDE, VIEW;

    private VaultItem vaultItem;

    public void setVaultItem(VaultItem vaultItem) {
        this.vaultItem = vaultItem;
    }
    public Optional<VaultItem> getVaultItem() {
        return Optional.ofNullable(vaultItem);
    }
}
