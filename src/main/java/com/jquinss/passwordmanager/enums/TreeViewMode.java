package com.jquinss.passwordmanager.enums;

import com.jquinss.passwordmanager.data.DataEntity;
import javafx.scene.control.TreeItem;

import java.util.Optional;

public enum TreeViewMode {
    CREATE, EDIT, VIEW;

    private TreeItem<DataEntity> treeItem;

    public void setTreeItem(TreeItem<DataEntity> treeItem) {
        this.treeItem = treeItem;
    }
    public Optional<TreeItem<DataEntity>> getTreeItem() {
        return Optional.ofNullable(treeItem);
    }
}
