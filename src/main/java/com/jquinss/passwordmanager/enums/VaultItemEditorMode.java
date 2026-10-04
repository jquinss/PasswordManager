package com.jquinss.passwordmanager.enums;

import com.jquinss.passwordmanager.data.VaultItem;

import java.util.Optional;

public enum DataEntityEditorMode {
    CREATE, EDIT, HIDE, VIEW;

    private VaultItem vaultItem;

    public void setDataEntity(VaultItem vaultItem) {
        this.vaultItem = vaultItem;
    }
    public Optional<VaultItem> getDataEntity() {
        return Optional.ofNullable(vaultItem);
    }
}
