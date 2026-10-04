package com.jquinss.passwordmanager.enums;

import com.jquinss.passwordmanager.data.VaultItem;
import javafx.scene.control.TreeItem;

import java.util.Optional;

public enum TreeViewMode {
    CREATE, EDIT, VIEW;

    private TreeItem<VaultItem> treeItem;

    public void setTreeItem(TreeItem<VaultItem> treeItem) {
        this.treeItem = treeItem;
    }
    public Optional<TreeItem<VaultItem>> getTreeItem() {
        return Optional.ofNullable(treeItem);
    }
}
