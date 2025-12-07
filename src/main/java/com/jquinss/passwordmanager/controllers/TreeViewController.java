package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.control.DataEntityTreeItem;
import com.jquinss.passwordmanager.data.*;
import com.jquinss.passwordmanager.enums.TreeViewMode;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import com.jquinss.passwordmanager.util.misc.FixedLengthFilter;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.Stage;
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
    private final CryptoUtils.AsymmetricCrypto asymmetricCrypto;
    private final ContextMenuBuilder contextMenuBuilder = new ContextMenuBuilder();

    private DataFormat dataFormat = DataFormat.lookupMimeType("fileItemDataFormat");
    private TreeViewMode treeViewMode;
    private PasswordManagerPaneController passwordManagerPaneController;

    public TreeViewController(TreeView<DataEntity> treeView, CryptoUtils.AsymmetricCrypto asymmetricCrypto) {
        this.treeView = treeView;
        this.asymmetricCrypto = asymmetricCrypto;
        if (dataFormat == null) {
            dataFormat = new DataFormat("fileItemDataFormat");
        }
    }

    void createFolder() {
        TextField folderNameTextField = new TextField();
        folderNameTextField.setPromptText("Enter folder name");
        folderNameTextField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(50)));

        TextField folderDescriptionTextField = new TextField();
        folderDescriptionTextField.setPromptText("Enter description");
        folderDescriptionTextField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(100)));

        Dialog<BiValue<String, String>> dialog = DialogBuilder.buildTwoTextFieldInputDialog("Create folder",
                    "Create a new folder:", "Folder name:", folderNameTextField, "Description:",
                folderDescriptionTextField, true);

        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
        setWindowLogo((Stage) dialog.getDialogPane().getScene().getWindow(), this, "/com/jquinss/passwordmanager/images/create_folder.png");
        Optional<BiValue<String, String>> optional = dialog.showAndWait();
        optional.ifPresent(biValue -> {
            try {
                createFolderTreeItem(treeView.getRoot(), biValue.first(), biValue.second());
            } catch (SQLException e) {
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error creating folder",
                            "A database error has occurred during the operation", Alert.AlertType.ERROR);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                alertDialog.showAndWait();
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

    void deleteFolder() {
        TreeItem<DataEntity> treeItem = treeView.getSelectionModel().getSelectedItem();
        if ((treeItem != null) && (treeItem.getValue() instanceof Folder) &&
                !(treeItem.getValue() instanceof RootFolder)){
            if (treeItem.getChildren().isEmpty()) {
                deleteFolder(treeItem);
            }
            else {
                Alert alertDialog = DialogBuilder.buildAlertDialog("Confirmation", "The folder is not empty", "Are you sure you want to delete all the files?", Alert.AlertType.CONFIRMATION);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                setWindowLogo((Stage) alertDialog.getDialogPane().getScene().getWindow(), this, "/com/jquinss/passwordmanager/images/delete_folder.png");


                alertDialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        deleteAllPasswordEntitiesInFolder(treeItem);
                        deleteFolder(treeItem);
                    }
                });;
            }
        }
    }

    private void deleteFolder(TreeItem<DataEntity> folderTreeItem) {
        try {
            Folder folder = (Folder) folderTreeItem.getValue();
            DatabaseManager.getInstance().deleteFolder(folder);
            folderTreeItem.getParent().getChildren().remove(folderTreeItem);
        }
        catch (SQLException e) {
            Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error deleting folder",
                    "A database error has occurred during the operation", Alert.AlertType.ERROR);
            alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
            alertDialog.showAndWait();
        }
    }

    private void deleteAllPasswordEntitiesInFolder(TreeItem<DataEntity> folderTreeItem) {
        List<PasswordEntity> passwordEntities = folderTreeItem.getChildren().stream().map(item -> (PasswordEntity) item.getValue()).toList();
        try {
            DatabaseManager.getInstance().deletePasswordEntities(passwordEntities);
        }
        catch (SQLException e) {
            Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error deleting password entities",
                    "A database error has occurred during the operation", Alert.AlertType.ERROR);
            alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
            alertDialog.showAndWait();
        }
    }

    private void editFolder() {
        TreeItem<DataEntity> folderTreeItem = treeView.getSelectionModel().getSelectedItem();
        if ((folderTreeItem != null) && (folderTreeItem.getValue() instanceof Folder folder)) {

            TextField folderNameTextField = new TextField(folder.getName());
            folderNameTextField.setPromptText("Enter folder name");
            folderNameTextField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(50)));

            TextField folderDescriptionTextField = new TextField();
            folderDescriptionTextField.setPromptText("Enter description");
            folderDescriptionTextField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(100)));
            if (folder.getDescription() != null) folderDescriptionTextField.setText(folder.getDescription());

            Dialog<BiValue<String, String>> dialog = DialogBuilder.buildTwoTextFieldInputDialog("Edit folder",
                    "Edit folder:", "Folder name:", folderNameTextField, "Description:",
                    folderDescriptionTextField, true);

            dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
            setWindowLogo((Stage) dialog.getDialogPane().getScene().getWindow(), this, "/com/jquinss/passwordmanager/images/edit_folder.png");

            Optional<BiValue<String, String>> optional = dialog.showAndWait();
            optional.ifPresent(biValue -> {
                try {
                    editFolderTreeItem(folderTreeItem, biValue.first(), biValue.second());
                }
                catch (SQLException e) {
                    Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error editing folder",
                            "A database error has occurred during the operation", Alert.AlertType.ERROR);
                    alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                    alertDialog.showAndWait();
                }
            });
        }

    }

    private void editFolderTreeItem(TreeItem<DataEntity> treeItem, String name, String description) throws SQLException {
        Folder folder = (Folder) treeItem.getValue();
        Folder folderCopy = folder.clone();
        folderCopy.setName(name);
        folderCopy.setDescription(description);
        DatabaseManager.getInstance().updateFolder(folderCopy);
        treeItem.setValue(folderCopy);
        // refresh quick view
        hideDataEntityInQuickViewPane();
        viewDataEntityInQuickViewPane(folderCopy);
    }

    void createPasswordEntity() {
        TreeItem<DataEntity> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();
        if ((selectedTreeItem != null ) && (selectedTreeItem.getValue() instanceof Folder)) {
            setEditMode(TreeViewMode.CREATE, selectedTreeItem);
            passwordManagerPaneController.createPasswordEntityInEditor((Folder) selectedTreeItem.getValue());
        }
    }

    void deletePasswordEntity() {
        TreeItem<DataEntity> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();
        if ((selectedTreeItem != null ) && (selectedTreeItem.getValue() instanceof PasswordEntity)) {
            try {
                DatabaseManager.getInstance().deletePasswordEntity((PasswordEntity) selectedTreeItem.getValue());
                selectedTreeItem.getParent().getChildren().remove(selectedTreeItem);
            } catch (SQLException e) {
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error deleting password entity",
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                alertDialog.showAndWait();
            }
        }
    }

    void editPasswordEntity() {
        TreeItem<DataEntity> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();
        if ((selectedTreeItem != null) && selectedTreeItem.getValue() instanceof PasswordEntity) {
            setEditMode(TreeViewMode.EDIT, selectedTreeItem);
            // creates a copy of the PasswordEntity instance in case any exception occurs inserting the the db
            PasswordEntity pwdEntityCopy = (PasswordEntity) ((PasswordEntity) selectedTreeItem.getValue()).clone();
            passwordManagerPaneController.editPasswordEntityInEditor(pwdEntityCopy);
        }
    }

    void viewPasswordEntity() {
        TreeItem<DataEntity> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();
        if ((selectedTreeItem != null) && selectedTreeItem.getValue() instanceof PasswordEntity) {
            setViewMode();
            passwordManagerPaneController.viewPasswordEntityInEditor((PasswordEntity) selectedTreeItem.getValue());
        }
    }

    private void viewDataEntityInQuickViewPane(DataEntity dataEntity) {
        passwordManagerPaneController.viewDataEntityInQuickViewPane(dataEntity);
    }

    private void hideDataEntityInQuickViewPane() {
        passwordManagerPaneController.hideQuickViewPane();
    }

    void duplicatePasswordEntity() {
        TreeItem<DataEntity> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();

        if ((selectedTreeItem != null) && selectedTreeItem.getValue() instanceof PasswordEntity) {
            PasswordEntity pwdEntity = (PasswordEntity) selectedTreeItem.getValue();

            try {
                PasswordEntity pwdEntityCopy = (PasswordEntity) pwdEntity.clone();
                pwdEntityCopy.setName("Copy of " + pwdEntity.getName());
                savePasswordEntityToDatabase(pwdEntityCopy);
                savePasswordEntityToTreeView(pwdEntityCopy, selectedTreeItem.getParent());
            }
            catch (SQLException e) {
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error creating new password entity",
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                alertDialog.showAndWait();
            }
        }
    }

    private void copyToClipboard(ActionEvent event) {
        TreeItem<DataEntity> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();

        if ((selectedTreeItem != null) && (selectedTreeItem.getValue() instanceof PasswordEntity)) {
            String menuItemId = ((MenuItem) event.getSource()).getId();
            PasswordEntity pwdEntity = (PasswordEntity) selectedTreeItem.getValue();
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
            Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error creating password entity",
                    "A database error has occurred during the operation", Alert.AlertType.ERROR);
            alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
            alertDialog.showAndWait();
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
                // refresh quick view pane
                hideDataEntityInQuickViewPane();
                viewDataEntityInQuickViewPane(passwordEntityCopy);
            }
            catch (SQLException e) {
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error modifying password entity",
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                alertDialog.showAndWait();
            }
            finally {
                setViewMode();
            }
        });
    }

    private void movePasswordEntity(TreeItem<DataEntity> passwordEntityTreeItem, TreeItem<DataEntity> destFolderTreeItem) throws SQLException {
        PasswordEntity passwordEntity = (PasswordEntity) passwordEntityTreeItem.getValue();
        PasswordEntity passwordEntityCopy = (PasswordEntity) passwordEntity.clone();
        passwordEntityCopy.setFolderId(destFolderTreeItem.getValue().getId());
        encryptFields(passwordEntityCopy);
        DatabaseManager.getInstance().updatePasswordEntity(passwordEntityCopy);
        decryptFields(passwordEntityCopy);
        passwordEntityTreeItem.setValue(passwordEntityCopy);
        passwordEntityTreeItem.getParent().getChildren().remove(passwordEntityTreeItem);
        destFolderTreeItem.getChildren().add(passwordEntityTreeItem);
    }

    void initializeTreeView() {
        setTreeViewCellFactory();
        setSelectedTreeItemListener();
        initializeRootTreeItem();
        loadTreeItems();
        setViewMode();
    }

    private void initializeRootTreeItem() {
        try {
            Optional<RootFolder> optional = DatabaseManager.getInstance().getRootFolderByUserProfileId(passwordManagerPaneController.getUserProfileSession().getCurrentUserProfileId());
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
        Optional<RootFolder> optional = DatabaseManager.getInstance().
                getRootFolderByUserProfileId(passwordManagerPaneController.getUserProfileSession().getCurrentUserProfileId());
        optional.ifPresent(rootFolder -> treeView.setRoot(buildTreeItem(rootFolder)));
    }

    private TreeItem<DataEntity> buildTreeItem(DataEntity dataEntity) {
        DataEntityTreeItem treeItem = new DataEntityTreeItem(dataEntity);
        setContextMenu(treeItem);
        if (dataEntity instanceof Folder) {
            treeItem.setExpanded(true);
        }
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
            addFolder.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+F"));
            getItems().addAll(addFolder);
        }
    }

    private class FolderContextMenu extends ContextMenu {
        final MenuItem createPasswordEntityMenuItem = new MenuItem("Create New Password...");
        final MenuItem editFolderMenuItem = new MenuItem("Edit");
        final MenuItem deleteFolderMenuItem = new MenuItem("Delete");

        FolderContextMenu() {
            createPasswordEntityMenuItem.setOnAction(e -> createPasswordEntity());
            createPasswordEntityMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+N"));
            editFolderMenuItem.setOnAction(e -> editFolder());
            editFolderMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+I"));
            deleteFolderMenuItem.setOnAction(e -> deleteFolder());
            deleteFolderMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+D"));

            getItems().addAll(createPasswordEntityMenuItem,editFolderMenuItem, deleteFolderMenuItem);
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
            copyPasswordToClipboardItem.setAccelerator(KeyCombination.keyCombination("Shortcut+P"));
            copyUsernameToClipboardItem.setId("copyUsernameToClipboardItem");
            copyUsernameToClipboardItem.setOnAction(TreeViewController.this::copyToClipboard);
            copyUsernameToClipboardItem.setAccelerator(KeyCombination.keyCombination("Shortcut+U"));
            copyURLToClipboardItem.setId("copyURLToClipboardItem");
            copyURLToClipboardItem.setOnAction(TreeViewController.this::copyToClipboard);
            copyURLToClipboardItem.setAccelerator(KeyCombination.keyCombination("Shortcut+R"));
            copyEmailAddressToClipboardItem.setId("copyEmailAddressToClipboardItem");
            copyEmailAddressToClipboardItem.setOnAction(TreeViewController.this::copyToClipboard);
            copyEmailAddressToClipboardItem.setAccelerator(KeyCombination.keyCombination("Shortcut+E"));
            duplicatePasswordEntityItem.setOnAction(e -> duplicatePasswordEntity());
            duplicatePasswordEntityItem.setAccelerator(KeyCombination.keyCombination("Shortcut+L"));
            viewPasswordEntityItem.setOnAction(e -> viewPasswordEntity());
            viewPasswordEntityItem.setAccelerator(KeyCombination.keyCombination("Shortcut+V"));
            editPasswordEntityItem.setOnAction(e -> editPasswordEntity());
            editPasswordEntityItem.setAccelerator(KeyCombination.keyCombination("Shortcut+I"));
            deletePasswordEntityItem.setOnAction(e -> deletePasswordEntity());
            deletePasswordEntityItem.setAccelerator(KeyCombination.keyCombination("Shortcut+D"));

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
            private TreeItem<DataEntity> passwordEntityTreeItem;
            private TreeItem<DataEntity> destFolderTreeItem;
            private TreeItem<DataEntity> resultPasswordEntityTreeItem;

            @Override
            public TreeCell<DataEntity> call(TreeView<DataEntity> p) {
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

                cell.setOnDragDetected(e -> {
                    if (cell.getItem() instanceof PasswordEntity) {
                        Dragboard dragBoard = cell.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent content = new ClipboardContent();
                        content.put(dataFormat, cell.getItem());
                        dragBoard.setContent(content);
                        dragBoard.setDragView(cell.snapshot(null, null));
                        e.consume();
                    }
                });


                cell.setOnDragOver(e ->{
                    Dragboard dragboard = e.getDragboard();
                    destFolderTreeItem = cell.getTreeItem();
                    passwordEntityTreeItem = p.getSelectionModel().getSelectedItem();

                    if ((dragboard.hasContent(dataFormat)) &&
                            (destFolderTreeItem.getValue() instanceof Folder) &&
                            (destFolderTreeItem != passwordEntityTreeItem.getParent())) {
                        e.acceptTransferModes(TransferMode.MOVE);
                    }
                });

                cell.setOnDragDropped(e -> {
                    try {
                        movePasswordEntity(passwordEntityTreeItem, destFolderTreeItem);
                        resultPasswordEntityTreeItem = passwordEntityTreeItem;
                        e.setDropCompleted(true);
                    } catch (SQLException e1) {
                        System.out.println("An error has occurred which moving the password entity");
                    }
                });

                cell.setOnDragDone(e -> {
                    if (resultPasswordEntityTreeItem != null) {
                        p.getSelectionModel().select(resultPasswordEntityTreeItem);
                    }
                });

                return cell;
            }
        });
    }

    private void setSelectedTreeItemListener() {
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                DataEntity dataEntity = newValue.getValue();
                passwordManagerPaneController.viewDataEntityInQuickViewPane(dataEntity);

                if (dataEntity instanceof  RootFolder) {
                    passwordManagerPaneController.enableRootRelatedToolbarButtons();
                } else if (dataEntity instanceof Folder) {
                    passwordManagerPaneController.enableFolderRelatedToolbarButtons();
                } else {
                    passwordManagerPaneController.enablePasswordEntityRelatedToolbarButtons();
                }
            }
            else {
                passwordManagerPaneController.hideQuickViewPane();
                passwordManagerPaneController.disableAllToolbarButtons();
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

    private void setWindowLogo(Stage stage, Object context, String imageFile) {
        stage.getIcons().add(new Image(Objects.requireNonNull(context.getClass().getResource(imageFile)).toString()));
    }
}
