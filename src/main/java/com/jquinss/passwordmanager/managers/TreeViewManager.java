package com.jquinss.passwordmanager.managers;

import com.jquinss.passwordmanager.control.DataEntityTreeItem;
import com.jquinss.passwordmanager.data.DataEntity;
import com.jquinss.passwordmanager.data.Folder;
import com.jquinss.passwordmanager.data.PasswordEntity;
import com.jquinss.passwordmanager.data.RootFolder;
import com.jquinss.passwordmanager.security.UserSession;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class TreeViewManager {
    private final TreeView<DataEntity> treeView;
    private final UserSession userSession;
    private final CryptoUtils.AsymmetricCrypto asymmetricCrypto;
    private final ContextMenuBuilder contextMenuBuilder = new ContextMenuBuilder();

    public TreeViewManager(TreeView<DataEntity> treeView, UserSession userSession, CryptoUtils.AsymmetricCrypto asymmetricCrypto) {
        this.treeView = treeView;
        this.userSession = userSession;
        this.asymmetricCrypto = asymmetricCrypto;
    }

    private void deleteAllFolders() {
        // TODO
    }

    private void createFolder() {
        // TODO
    }

    private void deleteFolder() {
        // TODO
    }

    private void renameFolder() {
        // TODO
    }

    private void createPasswordEntity() {
        // TODO
    }

    private void deletePasswordEntity() {
        // TODO
    }

    private void renamePasswordEntity() {
        // TODO
    }

    private void editPasswordEntity() {
        // TODO
    }

    private void viewPasswordEntity() {
        // TODO
    }

    private void duplicatePasswordEntity() {
        // TODO
    }

    private void copyPasswordToClipboard() {
        // TODO
    }

    public void initializeTreeView() {
        // TODO
        setTreeViewCellFactory();
        initializeRootTreeItem();
    }

    private void initializeRootTreeItem() {
        try{
            Optional<Folder> optional = DatabaseManager.getInstance().getFolderById(0);
            optional.ifPresent(folder -> treeView.setRoot(buildTreeItem(folder)));
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private TreeItem<DataEntity> buildTreeItem(DataEntity dataEntity) {
        DataEntityTreeItem treeItem = new DataEntityTreeItem(dataEntity);
        setContextMenu(treeItem);
        return treeItem;
    }

    private void setContextMenu(DataEntityTreeItem treeItem) {
        ContextMenu contextMenu = contextMenuBuilder.buildContextMenu(treeItem.getValue());
        treeItem.setContextMenu(contextMenu);
    }

    // inner class that builds the context menus based on the type of element
    class ContextMenuBuilder {
        ContextMenu buildContextMenu(DataEntity dataEntity) {
            ContextMenu contextMenu = null;

            if (dataEntity instanceof RootFolder) {
                contextMenu = new RootFolderContextMenu();
            } else if (dataEntity instanceof Folder) {
                contextMenu = new FolderContextMenu();
            } else if (dataEntity instanceof PasswordEntity) {
                contextMenu = new PasswordEntityContextMenu();
            }

            return contextMenu;
        }
    }

    private class RootFolderContextMenu extends ContextMenu {
        MenuItem addFolder = new MenuItem("Add Folder");
        MenuItem removeFolders = new MenuItem("Delete All Folders");

        RootFolderContextMenu() {
            addFolder.setOnAction(e -> createFolder());
            removeFolders.setOnAction(e -> deleteAllFolders());
            getItems().addAll(addFolder, removeFolders);
        }
    }

    private class FolderContextMenu extends ContextMenu {
        MenuItem createPasswordEntityMenuItem = new MenuItem("Create New Password...");
        MenuItem renameFolderMenuItem = new MenuItem("Rename");
        MenuItem deleteFolderMenuItem = new MenuItem("Delete");

        FolderContextMenu() {
            createPasswordEntityMenuItem.setOnAction(e -> createPasswordEntity());
            renameFolderMenuItem.setOnAction(e -> renameFolder());
            deleteFolderMenuItem.setOnAction(e -> deleteFolder());

            getItems().addAll(createPasswordEntityMenuItem, renameFolderMenuItem, deleteFolderMenuItem);
        }
    }

    private class PasswordEntityContextMenu extends ContextMenu {
        MenuItem copyPasswordToClipboardItem = new MenuItem("Copy Password to Clipboard");
        MenuItem duplicatePasswordEntityItem = new MenuItem("Duplicate");
        MenuItem viewPasswordEntityItem = new MenuItem("View");
        MenuItem editPasswordEntityItem = new MenuItem("Edit");
        MenuItem renamePasswordEntityItem = new MenuItem("Rename");
        MenuItem deletePasswordEntityItem = new MenuItem("Delete");

        PasswordEntityContextMenu() {
            copyPasswordToClipboardItem .setOnAction(e -> copyPasswordToClipboard());
            duplicatePasswordEntityItem.setOnAction(e -> duplicatePasswordEntity());
            viewPasswordEntityItem.setOnAction(e -> viewPasswordEntity());
            editPasswordEntityItem.setOnAction(e -> editPasswordEntity());
            renamePasswordEntityItem.setOnAction(e -> renamePasswordEntity());
            deletePasswordEntityItem.setOnAction(e -> deletePasswordEntity());

            getItems().addAll(copyPasswordToClipboardItem, viewPasswordEntityItem, duplicatePasswordEntityItem,
                    editPasswordEntityItem, renamePasswordEntityItem, deletePasswordEntityItem);
        }
    }

    private void setTreeViewCellFactory() {
        // we set the cell factory for each different element. The context menu and graphic will be different
        // depending on the type of element.
        treeView.setCellFactory(new Callback<TreeView<DataEntity>, TreeCell<DataEntity>>() {

            @Override
            public TreeCell<DataEntity> call(TreeView<DataEntity> p){
                TreeCell<DataEntity> cell = new TreeCell<DataEntity>() {
                    @Override
                    protected void updateItem(DataEntity dataEntity, boolean empty) {
                        super.updateItem(dataEntity, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        }
                        else {
                            setText(dataEntity.getName());
                            setContextMenu(((DataEntityTreeItem) getTreeItem()).getContextMenu());
                            setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(((DataEntityTreeItem) getTreeItem()).getImgURL())))));
                        }
                    }
                };

                return cell;
            }
        });
    }
}
