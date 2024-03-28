package com.jquinss.passwordmanager.managers;

import com.jquinss.passwordmanager.control.DataEntityTreeItem;
import com.jquinss.passwordmanager.data.DataEntity;
import com.jquinss.passwordmanager.data.Folder;
import com.jquinss.passwordmanager.data.PasswordEntity;
import com.jquinss.passwordmanager.data.RootFolder;
import com.jquinss.passwordmanager.security.UserSession;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.List;
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
        Dialog<Pair<String, String>> dialog = DialogBuilder.buildTwoTextFieldInputDialog("Create folder",
                "Create a new folder:", "Folder name", "Description", true);
        Optional<Pair<String, String>> optional = dialog.showAndWait();
        optional.ifPresent(pair -> {
            try {
                createFolderTreeItem(treeView.getSelectionModel().getSelectedItem(), pair.getKey(), pair.getValue());
            }
            catch (SQLException e) {
                DialogBuilder.buildAlertDialog("Error", "Error creating folder",
                        "An error has occurred with the database during the operation", Alert.AlertType.ERROR);
            }
        });
    }

    private void createFolderTreeItem(TreeItem<DataEntity> parentTreeItem, String name, String description) throws SQLException {
        Folder parentFolder = (Folder) parentTreeItem.getValue();
        Folder folder = createFolder(parentFolder.getId(), name, description);
        parentTreeItem.getChildren().add(buildTreeItem(folder));
    }

    private Folder createFolder(int parentFolderId, String name, String description) throws SQLException {
        Folder folder = new Folder(name);
        folder.setParentFolderId(parentFolderId);
        folder.setDescription(description);
        DatabaseManager.getInstance().addFolder(folder);
        return folder;
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
        loadTreeItems();
    }

    private void initializeRootTreeItem() {
        try {
            Optional<RootFolder> optional = DatabaseManager.getInstance().getRootFolderByUserId(userSession.getCurrentUserId());
            if (optional.isPresent()) {
                treeView.setRoot(buildTreeItem(optional.get()));
            }
            else {
                createRootTreeItem();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTreeItems() {
        try {
            TreeItem<DataEntity> rootTreeItem = treeView.getRoot();
            List<Folder> folders = DatabaseManager.getInstance().getAllFoldersByParentFolderId(rootTreeItem.getValue().getId());
            for (Folder folder : folders) {
                TreeItem<DataEntity> treeItem = buildTreeItem(folder);
                rootTreeItem.getChildren().add(treeItem);
                loadPasswordEntities(treeItem);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadPasswordEntities(TreeItem<DataEntity> folderTreeItem) throws SQLException {
        List<PasswordEntity> pwdEntities = DatabaseManager.getInstance().getAllPasswordEntitiesByFolderId(folderTreeItem.getValue().getId());
        for (PasswordEntity pwdEntity : pwdEntities) {
            folderTreeItem.getChildren().add(buildTreeItem(pwdEntity));
        }
    }

    private void createRootTreeItem() throws SQLException {
        RootFolder rootFolder = createRootFolder();
        treeView.setRoot(buildTreeItem(rootFolder));
    }

    private RootFolder createRootFolder() throws SQLException {
        RootFolder rootFolder = new RootFolder("root", userSession.getCurrentUserId());
        DatabaseManager.getInstance().addRootFolder(rootFolder);
        return rootFolder;
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

                return new TreeCell<DataEntity>() {
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
            }
        });
    }
}
