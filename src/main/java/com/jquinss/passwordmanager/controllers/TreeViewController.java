package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.control.DataEntityTreeItem;
import com.jquinss.passwordmanager.data.DataEntity;
import com.jquinss.passwordmanager.data.Folder;
import com.jquinss.passwordmanager.data.PasswordEntity;
import com.jquinss.passwordmanager.data.RootFolder;
import com.jquinss.passwordmanager.enums.TreeViewMode;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.security.UserSession;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Callback;
import javafx.util.Pair;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TreeViewController {
    private final TreeView<DataEntity> treeView;
    private final UserSession userSession;
    private final CryptoUtils.AsymmetricCrypto asymmetricCrypto;
    private final ContextMenuBuilder contextMenuBuilder = new ContextMenuBuilder();
    private TreeViewMode treeViewMode;
    private PasswordManagerPaneController passwordManagerPaneController;

    public TreeViewController(TreeView<DataEntity> treeView, UserSession userSession, CryptoUtils.AsymmetricCrypto asymmetricCrypto) {
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
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
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
        TreeItem<DataEntity> treeItem = treeView.getSelectionModel().getSelectedItem();
        if (treeItem.getChildren().isEmpty()) {
            deleteFolder(treeItem);
        }
        else {
            Alert alertDialog = DialogBuilder.buildAlertDialog("Confirmation", "The folder is not empty", "Are you sure you want to delete all the files?", Alert.AlertType.CONFIRMATION);
            alertDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteAllPasswordEntitiesInFolder(treeItem);
                    deleteFolder(treeItem);
                }
            });;
        }
    }

    private void deleteFolder(TreeItem<DataEntity> folderTreeItem) {
        try {
            Folder folder = (Folder) folderTreeItem.getValue();
            DatabaseManager.getInstance().deleteFolder(folder);
            folderTreeItem.getParent().getChildren().remove(folderTreeItem);
        }
        catch (SQLException e) {
            DialogBuilder.buildAlertDialog("Error", "Error deleting folder",
                    "A database error has occurred during the operation", Alert.AlertType.ERROR);
        }
    }

    private void deleteAllPasswordEntitiesInFolder(TreeItem<DataEntity> folderTreeItem) {
        List<PasswordEntity> passwordEntities = folderTreeItem.getChildren().stream().map(item -> (PasswordEntity) item.getValue()).toList();
        try {
            DatabaseManager.getInstance().deletePasswordEntities(passwordEntities);
        }
        catch (SQLException e) {
            DialogBuilder.buildAlertDialog("Error", "Error deleting password entities",
                    "A database error has occurred during the operation", Alert.AlertType.ERROR);
        }
    }

    private void editFolder() {
        // TODO
    }

    private void createPasswordEntity() {
        TreeItem<DataEntity> treeItem = treeView.getSelectionModel().getSelectedItem();
        setEditMode(TreeViewMode.CREATE, treeItem);
        passwordManagerPaneController.createPasswordEntityInEditor((Folder) treeItem.getValue());
    }

    private void deletePasswordEntity() {
        TreeItem<DataEntity> treeItem = treeView.getSelectionModel().getSelectedItem();
        try {
            DatabaseManager.getInstance().deletePasswordEntity((PasswordEntity) treeItem.getValue());
            treeItem.getParent().getChildren().remove(treeItem);
        }
        catch (SQLException e) {
            DialogBuilder.buildAlertDialog("Error", "Error deleting password entity",
                    "A database error has occurred during the operation", Alert.AlertType.ERROR);
        }
    }

    private void editPasswordEntity() {
        TreeItem<DataEntity> treeItem = treeView.getSelectionModel().getSelectedItem();
        setEditMode(TreeViewMode.EDIT, treeItem);
        // creates a copy of the PasswordEntity instance in case any exception occurs inserting the the db
        PasswordEntity pwdEntityCopy = (PasswordEntity) ((PasswordEntity) treeItem.getValue()).clone();
        passwordManagerPaneController.editPasswordEntityInEditor(pwdEntityCopy);
    }

    private void viewPasswordEntity() {
        setViewMode();
        passwordManagerPaneController.viewPasswordEntityInEditor((PasswordEntity) treeView.
                getSelectionModel().getSelectedItem().getValue());
    }

    private void duplicatePasswordEntity() {
        TreeItem<DataEntity> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();
        PasswordEntity pwdEntity = (PasswordEntity) selectedTreeItem.getValue();
        try {
            PasswordEntity pwdEntityCopy = (PasswordEntity) pwdEntity.clone();
            pwdEntityCopy.setName("Copy of " + pwdEntity.getName());
            savePasswordEntityToDatabase(pwdEntityCopy);
            savePasswordEntityToTreeView(pwdEntityCopy, selectedTreeItem.getParent());
        }
        catch (SQLException e) {
            DialogBuilder.buildAlertDialog("Error", "Error creating new password entity",
                    "A database error has occurred during the operation", Alert.AlertType.ERROR);
        }
    }

    private void copyToClipboard(ActionEvent event) {
        String menuItemId = ((MenuItem) event.getSource()).getId();
        PasswordEntity pwdEntity = (PasswordEntity) treeView.getSelectionModel().getSelectedItem().getValue();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        switch (menuItemId) {
            case "copyPasswordToClipboardItem" -> content.putString(pwdEntity.getPassword());
            case "copyUsernameToClipboardItem" -> content.putString(pwdEntity.getUsername());
            case "copyEmailAddressToClipboardItem" -> content.putString(pwdEntity.getEmailAddress());
            case "copyURLToClipboardItem" -> content.putString(pwdEntity.getUrl());
        }

        clipboard.setContent(content);
    }

    void savePasswordEntity(PasswordEntity passwordEntity) {
        treeViewMode.getTreeItem().ifPresent(treeItem -> {
            switch (treeViewMode) {
                case CREATE -> addPasswordEntity(passwordEntity, treeItem);
                case EDIT -> modifyPasswordEntity(passwordEntity);
            }
        });

    }

    private void addPasswordEntity(PasswordEntity passwordEntity, TreeItem<DataEntity> folderTreeItem) {
        try {
            savePasswordEntityToDatabase(passwordEntity);
            savePasswordEntityToTreeView(passwordEntity, folderTreeItem);
        }
        catch (SQLException e) {
            DialogBuilder.buildAlertDialog("Error", "Error creating password entity",
                    "A database error has occurred during the operation", Alert.AlertType.ERROR);
        }
        finally {
            setViewMode();
        }
    }

    private void savePasswordEntityToDatabase(PasswordEntity passwordEntity) throws SQLException {
        encryptFields(passwordEntity); // encrypt fields to save to the database
        DatabaseManager.getInstance().addPasswordEntity(passwordEntity);
        decryptFields(passwordEntity);
    }

    private void savePasswordEntityToTreeView(PasswordEntity passwordEntity, TreeItem<DataEntity> folderTreeItem) {
        TreeItem<DataEntity> treeItem = buildTreeItem(passwordEntity);
        folderTreeItem.getChildren().add(treeItem);
    }

    private void modifyPasswordEntity(PasswordEntity passwordEntityCopy) {
        treeViewMode.getTreeItem().ifPresent(treeItem -> {
            try {
                encryptFields(passwordEntityCopy); // encrypt fields to save to the database
                DatabaseManager.getInstance().updatePasswordEntity(passwordEntityCopy);
                decryptFields(passwordEntityCopy);
                treeItem.setValue(passwordEntityCopy);
            }
            catch (SQLException e) {
                DialogBuilder.buildAlertDialog("Error", "Error modifying password entity",
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
            }
            finally {
                setViewMode();
            }
        });
    }

    void initializeTreeView() {
        setTreeViewCellFactory();
        initializeRootTreeItem();
        loadTreeItems();
        setViewMode();
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
            decryptFields(pwdEntity);
            folderTreeItem.getChildren().add(buildTreeItem(pwdEntity));
        }
    }

    private void encryptFields(PasswordEntity pwdEntity) {
        pwdEntity.setUsername(encryptText(pwdEntity.getUsername()));
        pwdEntity.setEmailAddress(encryptText(pwdEntity.getEmailAddress()));
        pwdEntity.setPassword(encryptText(pwdEntity.getPassword()));
    }

    private void decryptFields(PasswordEntity pwdEntity) {
        pwdEntity.setUsername(decryptText(pwdEntity.getUsername()));
        pwdEntity.setEmailAddress(decryptText((pwdEntity.getEmailAddress())));
        pwdEntity.setPassword(decryptText(pwdEntity.getPassword()));
    }

    private String encryptText(String text)  {
        try {
            return (text != null) ? asymmetricCrypto.encrypt(text) : null;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private String decryptText(String text) {
        try {
            return (text != null) ? asymmetricCrypto.decrypt(text) : null;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
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
        final MenuItem addFolder = new MenuItem("Add Folder");
        final MenuItem removeFolders = new MenuItem("Delete All Folders");

        RootFolderContextMenu() {
            addFolder.setOnAction(e -> createFolder());
            removeFolders.setOnAction(e -> deleteAllFolders());
            getItems().addAll(addFolder, removeFolders);
        }
    }

    private class FolderContextMenu extends ContextMenu {
        final MenuItem createPasswordEntityMenuItem = new MenuItem("Create New Password...");
        final MenuItem renameFolderMenuItem = new MenuItem("Edit");
        final MenuItem deleteFolderMenuItem = new MenuItem("Delete");

        FolderContextMenu() {
            createPasswordEntityMenuItem.setOnAction(e -> createPasswordEntity());
            renameFolderMenuItem.setOnAction(e -> renameFolder());
            deleteFolderMenuItem.setOnAction(e -> deleteFolder());

            getItems().addAll(createPasswordEntityMenuItem, renameFolderMenuItem, deleteFolderMenuItem);
        }
    }

    private class PasswordEntityContextMenu extends ContextMenu {
        final Menu copyToClipboardMenu = new Menu("Copy to Clipboard");
        final MenuItem copyPasswordToClipboardItem = new MenuItem("Password");
        final MenuItem copyUsernameToClipboardItem = new MenuItem("Username");
        final MenuItem copyEmailAddressToClipboardItem = new MenuItem("Email address");
        final MenuItem copyURLToClipboardItem = new MenuItem("URL");
        final MenuItem duplicatePasswordEntityItem = new MenuItem("Duplicate");
        final MenuItem viewPasswordEntityItem = new MenuItem("View");
        final MenuItem editPasswordEntityItem = new MenuItem("Edit");
        final MenuItem deletePasswordEntityItem = new MenuItem("Delete");

        PasswordEntityContextMenu() {
            copyPasswordToClipboardItem.setId("copyPasswordToClipboardItem");
            copyPasswordToClipboardItem .setOnAction(TreeViewController.this::copyToClipboard);
            copyUsernameToClipboardItem.setId("copyUsernameToClipboardItem");
            copyUsernameToClipboardItem.setOnAction(TreeViewController.this::copyToClipboard);
            copyURLToClipboardItem.setId("copyURLToClipboardItem");
            copyURLToClipboardItem.setOnAction(TreeViewController.this::copyToClipboard);
            copyEmailAddressToClipboardItem.setId("copyEmailAddressToClipboardItem");
            copyEmailAddressToClipboardItem.setOnAction(TreeViewController.this::copyToClipboard);
            duplicatePasswordEntityItem.setOnAction(e -> duplicatePasswordEntity());
            viewPasswordEntityItem.setOnAction(e -> viewPasswordEntity());
            editPasswordEntityItem.setOnAction(e -> editPasswordEntity());
            deletePasswordEntityItem.setOnAction(e -> deletePasswordEntity());

            copyToClipboardMenu.getItems().addAll(copyPasswordToClipboardItem, copyUsernameToClipboardItem ,
                    copyURLToClipboardItem, copyEmailAddressToClipboardItem);

            getItems().addAll(copyToClipboardMenu, viewPasswordEntityItem, duplicatePasswordEntityItem,
                    editPasswordEntityItem, deletePasswordEntityItem);
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

    void setPasswordManagerPaneController(PasswordManagerPaneController passwordManagerPaneController) {
        this.passwordManagerPaneController = passwordManagerPaneController;
    }

    void setViewMode() {
        this.treeViewMode = TreeViewMode.VIEW;
        treeViewMode.setTreeItem(null);
        treeView.setDisable(false);
    }

    private void setEditMode(TreeViewMode treeViewMode, TreeItem<DataEntity> treeItem) {
        this.treeViewMode = treeViewMode;
        treeViewMode.setTreeItem(treeItem);
        treeView.setDisable(true);
    }
}
