package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.app.AppContext;
import com.jquinss.passwordmanager.control.VaultTreeItem;
import com.jquinss.passwordmanager.vault.repository.VaultRepository;
import com.jquinss.passwordmanager.data.*;
import com.jquinss.passwordmanager.enums.TreeViewMode;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import com.jquinss.passwordmanager.util.misc.FixedLengthFilter;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class TreeViewController {
    private final TreeView<VaultItem> treeView;
    private final ContextMenuBuilder contextMenuBuilder = new ContextMenuBuilder();
    private final PasswordManagerPaneController passwordManagerPaneController;
    private final VaultRepository vaultRepository;
    private final Properties sessionVariables;
    private final Logger logger = LoggerFactory.getLogger(TreeViewController.class);
    private DataFormat dataFormat = DataFormat.lookupMimeType("fileItemDataFormat");
    private TreeViewMode treeViewMode;


    public TreeViewController(PasswordManagerPaneController passwordManagerPaneController, AppContext appContext,
                              TreeView<VaultItem> treeView) {
        this.passwordManagerPaneController = passwordManagerPaneController;
        this.vaultRepository = appContext.vaultRepository();
        this.sessionVariables = appContext.sessionVariables();
        this.treeView = treeView;

        if (dataFormat == null) {
            dataFormat = new DataFormat("fileItemDataFormat");
        }
        initializeTreeView();
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

        setPaneStyles(dialog.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
        setWindowLogo((Stage) dialog.getDialogPane().getScene().getWindow(), this, "/com/jquinss/passwordmanager/images/create_folder.png");
        Optional<BiValue<String, String>> optional = dialog.showAndWait();
        optional.ifPresent(biValue -> {
            try {
                logger.info("Creating folder");
                createFolderTreeItem(treeView.getRoot(), biValue.first(), biValue.second());
                logger.info("Folder has been created");
            } catch (SQLException e) {
                logger.error("Failed to create folder", e);
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to create folder",
                            "A database error has occurred during the operation", Alert.AlertType.ERROR);
                setPaneStyles(alertDialog.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
                alertDialog.showAndWait();
            }
        });
    }

    private void createFolderTreeItem(TreeItem<VaultItem> parentTreeItem, String name, String description) throws SQLException {
        Folder parentFolder = (Folder) parentTreeItem.getValue();
        Folder folder = createFolder(parentFolder.getId(), name, description);
        parentTreeItem.getChildren().add(buildTreeItem(folder));
    }

    private Folder createFolder(int parentFolderId, String name, String description) throws SQLException {
        Folder folder = new Folder(name);
        folder.setParentFolderId(parentFolderId);
        folder.setDescription(description);
        vaultRepository.addFolder(folder);
        return folder;
    }

    void deleteFolder() {
        TreeItem<VaultItem> treeItem = treeView.getSelectionModel().getSelectedItem();
        if ((treeItem != null) && (treeItem.getValue() instanceof Folder) &&
                !(treeItem.getValue() instanceof RootFolder)){
            if (treeItem.getChildren().isEmpty()) {
                deleteFolder(treeItem);
            }
            else {
                Alert alertDialog = DialogBuilder.buildAlertDialog("Confirmation", "The folder is not empty", "Are you sure you want to delete all the files?", Alert.AlertType.CONFIRMATION);
                setPaneStyles(alertDialog.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
                setWindowLogo((Stage) alertDialog.getDialogPane().getScene().getWindow(), this, "/com/jquinss/passwordmanager/images/delete_folder.png");
                
                alertDialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        deleteAllPasswordItemsInFolder(treeItem);
                        deleteFolder(treeItem);
                    }
                });;
            }
        }
    }

    private void deleteFolder(TreeItem<VaultItem> folderTreeItem) {
        try {
            logger.info("Deleting folder");
            Folder folder = (Folder) folderTreeItem.getValue();
            vaultRepository.deleteFolder(folder);
            folderTreeItem.getParent().getChildren().remove(folderTreeItem);
            logger.info("Folder has been deleted");
        }
        catch (SQLException e) {
            logger.error("Failed to delete folder", e);
            Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error deleting folder",
                    "A database error has occurred during the operation", Alert.AlertType.ERROR);
            setPaneStyles(alertDialog.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
            alertDialog.showAndWait();
        }
    }

    private void deleteAllPasswordItemsInFolder(TreeItem<VaultItem> folderTreeItem) {
        List<PasswordItem> passwordItems = folderTreeItem.getChildren().stream().map(item -> (PasswordItem) item.getValue()).toList();
        try {
            logger.info("Deleting password items in folder");
            vaultRepository.deletePasswordItems(passwordItems);
            logger.info("Password items have been deleted");
        }
        catch (SQLException e) {
            logger.error("Failed to delete password items");
            Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to delete password items",
                    "A database error has occurred during the operation", Alert.AlertType.ERROR);
            setPaneStyles(alertDialog.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
            alertDialog.showAndWait();
        }
    }

    private void editFolder() {
        TreeItem<VaultItem> folderTreeItem = treeView.getSelectionModel().getSelectedItem();
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
                    logger.info("Editing folder");
                    editFolderTreeItem(folderTreeItem, biValue.first(), biValue.second());
                    logger.info("Folder has been edited");
                }
                catch (SQLException e) {
                    logger.error("Failed to edit folder");
                    Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to edit folder",
                            "A database error has occurred during the operation", Alert.AlertType.ERROR);
                    setPaneStyles(alertDialog.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
                    alertDialog.showAndWait();
                }
            });
        }

    }

    private void editFolderTreeItem(TreeItem<VaultItem> treeItem, String name, String description) throws SQLException {
        Folder folder = (Folder) treeItem.getValue();
        Folder folderCopy = folder.clone();
        folderCopy.setName(name);
        folderCopy.setDescription(description);
        vaultRepository.updateFolder(folderCopy);
        treeItem.setValue(folderCopy);
        // refresh quick view
        viewDataItemInQuickViewPane(folderCopy);
    }

    void createPasswordItem() {
        TreeItem<VaultItem> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();
        if ((selectedTreeItem != null ) && (selectedTreeItem.getValue() instanceof Folder)) {
            setEditMode(TreeViewMode.CREATE, selectedTreeItem);
            passwordManagerPaneController.createPasswordItemInEditor((Folder) selectedTreeItem.getValue());
        }
    }

    void deletePasswordItem() {
        TreeItem<VaultItem> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();
        if ((selectedTreeItem != null ) && (selectedTreeItem.getValue() instanceof PasswordItem)) {
            try {
                logger.info("Deleting password item");
                vaultRepository.deletePasswordItem((PasswordItem) selectedTreeItem.getValue());
                selectedTreeItem.getParent().getChildren().remove(selectedTreeItem);
                logger.info("Password item has been deleted");
            } catch (SQLException e) {
                logger.error("Failed to delete password item");
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to delete password item",
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
                setPaneStyles(alertDialog.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
                alertDialog.showAndWait();
            }
        }
    }

    void editPasswordItem() {
        TreeItem<VaultItem> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();
        if ((selectedTreeItem != null) && selectedTreeItem.getValue() instanceof PasswordItem) {
            setEditMode(TreeViewMode.EDIT, selectedTreeItem);
            // creates a copy of the PasswordItem instance in case any exception occurs inserting the the db
            PasswordItem pwdItemCopy = (PasswordItem) ((PasswordItem) selectedTreeItem.getValue()).clone();
            passwordManagerPaneController.editPasswordItemInEditor(pwdItemCopy);
        }
    }

    void viewPasswordItem() {
        TreeItem<VaultItem> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();
        if ((selectedTreeItem != null) && selectedTreeItem.getValue() instanceof PasswordItem) {
            setViewMode();
            passwordManagerPaneController.viewPasswordItemInEditor((PasswordItem) selectedTreeItem.getValue());
        }
    }

    private void viewDataItemInQuickViewPane(VaultItem vaultItem) {
        passwordManagerPaneController.viewDataItemInQuickViewPane(vaultItem);
    }

    private void hideDataItemInQuickViewPane() {
        passwordManagerPaneController.hideQuickViewPane();
    }

    void duplicatePasswordItem() {
        TreeItem<VaultItem> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();

        if ((selectedTreeItem != null) && selectedTreeItem.getValue() instanceof PasswordItem pwdItem) {

            try {
                logger.info("Duplicating password item");
                PasswordItem pwdItemCopy = (PasswordItem) pwdItem.clone();
                pwdItemCopy.setName("Copy of " + pwdItem.getName());
                savePasswordItemToDatabase(pwdItemCopy);
                savePasswordItemToTreeView(pwdItemCopy, selectedTreeItem.getParent());
                logger.info("Password item has been duplicated");
            }
            catch (SQLException e) {
                logger.error("Failed to duplicate password item");
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to duplicate password item",
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
                setPaneStyles(alertDialog.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
                alertDialog.showAndWait();
            }
        }
    }

    private void copyToClipboard(ActionEvent event) {
        TreeItem<VaultItem> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();

        if ((selectedTreeItem != null) && (selectedTreeItem.getValue() instanceof PasswordItem pwdItem)) {
            String menuItemId = ((MenuItem) event.getSource()).getId();
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();

            switch (menuItemId) {
                case "copyPasswordToClipboardItem" -> content.putString(pwdItem.getPassword());
                case "copyUsernameToClipboardItem" -> content.putString(pwdItem.getUsername());
                case "copyEmailAddressToClipboardItem" -> content.putString(pwdItem.getEmailAddress());
                case "copyURLToClipboardItem" -> content.putString(pwdItem.getUrl());
            }

            clipboard.setContent(content);
        }
    }

    void savePasswordItem(PasswordItem passwordItem) {
        treeViewMode.getTreeItem().ifPresent(treeItem -> {
            switch (treeViewMode) {
                case CREATE -> addPasswordItem(passwordItem, treeItem);
                case EDIT -> modifyPasswordItem(passwordItem);
            }
        });

    }

    private void addPasswordItem(PasswordItem passwordItem, TreeItem<VaultItem> folderTreeItem) {
        try {
            logger.info("Creating password item");
            savePasswordItemToDatabase(passwordItem);
            savePasswordItemToTreeView(passwordItem, folderTreeItem);
            logger.info("Password item has been created");
        }
        catch (SQLException e) {
            logger.error("Failed to create password item");
            Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to create password item",
                    "A database error has occurred during the operation", Alert.AlertType.ERROR);
            setPaneStyles(alertDialog.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
            alertDialog.showAndWait();
        }
        finally {
            setViewMode();
        }
    }

    private void savePasswordItemToDatabase(PasswordItem passwordItem) throws SQLException {
        vaultRepository.addPasswordItem(passwordItem);
    }

    private void savePasswordItemToTreeView(PasswordItem passwordItem, TreeItem<VaultItem> folderTreeItem) {
        TreeItem<VaultItem> treeItem = buildTreeItem(passwordItem);
        folderTreeItem.getChildren().add(treeItem);
    }

    private void modifyPasswordItem(PasswordItem passwordItemCopy) {
        treeViewMode.getTreeItem().ifPresent(treeItem -> {
            try {
                logger.info("Modifying password item");
                vaultRepository.updatePasswordItem(passwordItemCopy);
                treeItem.setValue(passwordItemCopy);
                // refresh quick view pane
                viewDataItemInQuickViewPane(passwordItemCopy);
                logger.info("Password item has been modified");
            }
            catch (SQLException e) {
                logger.error("Failed to modify password item");
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to modify password item",
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
                setPaneStyles(alertDialog.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
                alertDialog.showAndWait();
            }
            finally {
                setViewMode();
            }
        });
    }

    private void movePasswordItem(TreeItem<VaultItem> passwordItemTreeItem, TreeItem<VaultItem> destFolderTreeItem) throws SQLException {
        PasswordItem passwordItem = (PasswordItem) passwordItemTreeItem.getValue();
        PasswordItem passwordItemCopy = (PasswordItem) passwordItem.clone();
        passwordItemCopy.setFolderId(destFolderTreeItem.getValue().getId());
        vaultRepository.updatePasswordItem(passwordItemCopy);
        passwordItemTreeItem.setValue(passwordItemCopy);
        passwordItemTreeItem.getParent().getChildren().remove(passwordItemTreeItem);
        destFolderTreeItem.getChildren().add(passwordItemTreeItem);
    }

    void initializeTreeView() {
        logger.info("Initializing TreeView");
        setTreeViewCellFactory();
        setSelectedTreeItemListener();
        try {
            initializeRootTreeItem();
            loadTreeItems();
        }
        catch (SQLException e) {
            logger.error("Failed to load vault items from database", e);
            Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to load vault",
                    "The application could not access the vault database.\nThe application will now exit",
                    Alert.AlertType.ERROR);
            setPaneStyles(alertDialog.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
            alertDialog.showAndWait();
        }

        setViewMode();
    }

    private void initializeRootTreeItem() throws SQLException {
        Optional<RootFolder> optional = vaultRepository.getRootFolderByUserProfileId(Integer.parseInt(sessionVariables.getProperty("profileId")));
        if (optional.isPresent()) {
            treeView.setRoot(buildTreeItem(optional.get()));
        }
        else {
            createRootTreeItem();
        }
    }

    private void loadTreeItems() throws SQLException {
        logger.info("Loading vault items");
        TreeItem<VaultItem> rootTreeItem = treeView.getRoot();
        List<Folder> folders = vaultRepository.getAllFoldersByParentFolderId(rootTreeItem.getValue().getId());
        for (Folder folder : folders) {
            TreeItem<VaultItem> treeItem = buildTreeItem(folder);
            rootTreeItem.getChildren().add(treeItem);
            loadPasswordItems(treeItem);
        }
        logger.info("Vault items have been loaded");
    }

    private void loadPasswordItems(TreeItem<VaultItem> folderTreeItem) throws SQLException {
        List<PasswordItem> pwdItems = vaultRepository.getAllPasswordItemsByFolderId(folderTreeItem.getValue().getId());
        for (PasswordItem pwdItem : pwdItems) {
            folderTreeItem.getChildren().add(buildTreeItem(pwdItem));
        }
    }

    private void createRootTreeItem() throws SQLException {
        RootFolder rootFolder = new RootFolder("Root", Integer.parseInt(sessionVariables.getProperty("profileId")));
        vaultRepository.addRootFolder(rootFolder);
        treeView.setRoot(buildTreeItem(rootFolder));
    }

    private TreeItem<VaultItem> buildTreeItem(VaultItem vaultItem) {
        VaultTreeItem treeItem = new VaultTreeItem(vaultItem);
        setContextMenu(treeItem);
        if (vaultItem instanceof Folder) {
            treeItem.setExpanded(true);
        }
        return treeItem;
    }

    private void setContextMenu(VaultTreeItem treeItem) {
        ContextMenu contextMenu = contextMenuBuilder.buildContextMenu(treeItem.getValue());
        treeItem.setContextMenu(contextMenu);
    }

    // inner class that builds the context menus based on the type of element
    class ContextMenuBuilder {
        ContextMenu buildContextMenu(VaultItem vaultItem) {
            ContextMenu contextMenu = null;

            if (vaultItem instanceof RootFolder) {
                contextMenu = new RootFolderContextMenu();
            } else if (vaultItem instanceof Folder) {
                contextMenu = new FolderContextMenu();
            } else if (vaultItem instanceof PasswordItem) {
                contextMenu = new PasswordItemContextMenu();
            }

            return contextMenu;
        }
    }

    private class RootFolderContextMenu extends ContextMenu {
        final MenuItem addFolder = new MenuItem("Add Folder");
        //final MenuItem removeFolders = new MenuItem("Delete All Folders");

        RootFolderContextMenu() {
            addFolder.setOnAction(e -> createFolder());
            addFolder.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+F"));
            getItems().addAll(addFolder);
        }
    }

    private class FolderContextMenu extends ContextMenu {
        final MenuItem createPasswordItemMenuItem = new MenuItem("Create New Password...");
        final MenuItem editFolderMenuItem = new MenuItem("Edit");
        final MenuItem deleteFolderMenuItem = new MenuItem("Delete");

        FolderContextMenu() {
            createPasswordItemMenuItem.setOnAction(e -> createPasswordItem());
            createPasswordItemMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+N"));
            editFolderMenuItem.setOnAction(e -> editFolder());
            editFolderMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+I"));
            deleteFolderMenuItem.setOnAction(e -> deleteFolder());
            deleteFolderMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+D"));

            getItems().addAll(createPasswordItemMenuItem,editFolderMenuItem, deleteFolderMenuItem);
        }
    }

    private class PasswordItemContextMenu extends ContextMenu {
        final Menu copyToClipboardMenu = new Menu("Copy to Clipboard");
        final MenuItem copyPasswordToClipboardItem = new MenuItem("Password");
        final MenuItem copyUsernameToClipboardItem = new MenuItem("Username");
        final MenuItem copyEmailAddressToClipboardItem = new MenuItem("Email address");
        final MenuItem copyURLToClipboardItem = new MenuItem("URL");
        final MenuItem duplicatePasswordItemItem = new MenuItem("Duplicate");
        final MenuItem viewPasswordItemItem = new MenuItem("View");
        final MenuItem editPasswordItemItem = new MenuItem("Edit");
        final MenuItem deletePasswordItemItem = new MenuItem("Delete");

        PasswordItemContextMenu() {
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
            duplicatePasswordItemItem.setOnAction(e -> duplicatePasswordItem());
            duplicatePasswordItemItem.setAccelerator(KeyCombination.keyCombination("Shortcut+L"));
            viewPasswordItemItem.setOnAction(e -> viewPasswordItem());
            viewPasswordItemItem.setAccelerator(KeyCombination.keyCombination("Shortcut+V"));
            editPasswordItemItem.setOnAction(e -> editPasswordItem());
            editPasswordItemItem.setAccelerator(KeyCombination.keyCombination("Shortcut+I"));
            deletePasswordItemItem.setOnAction(e -> deletePasswordItem());
            deletePasswordItemItem.setAccelerator(KeyCombination.keyCombination("Shortcut+D"));

            copyToClipboardMenu.getItems().addAll(copyPasswordToClipboardItem, copyUsernameToClipboardItem ,
                    copyURLToClipboardItem, copyEmailAddressToClipboardItem);

            getItems().addAll(copyToClipboardMenu, viewPasswordItemItem, duplicatePasswordItemItem,
                    editPasswordItemItem, deletePasswordItemItem);
        }
    }

    private void setTreeViewCellFactory() {
        // we set the cell factory for each different element. The context menu and graphic will be different
        // depending on the type of element.
        treeView.setCellFactory(new Callback<TreeView<VaultItem>, TreeCell<VaultItem>>() {
            private TreeItem<VaultItem> passwordItemTreeItem;
            private TreeItem<VaultItem> destFolderTreeItem;
            private TreeItem<VaultItem> resultPasswordItemTreeItem;

            @Override
            public TreeCell<VaultItem> call(TreeView<VaultItem> p) {
                TreeCell<VaultItem> cell = new TreeCell<VaultItem>() {
                    @Override
                    protected void updateItem(VaultItem dataItem, boolean empty) {
                        super.updateItem(dataItem, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        }
                        else {
                            setText(dataItem.getName());
                            setContextMenu(((VaultTreeItem) getTreeItem()).getContextMenu());
                            setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(((VaultTreeItem) getTreeItem()).getImgURL())))));
                        }
                    }
                };

                cell.setOnDragDetected(e -> {
                    if (cell.getItem() instanceof PasswordItem) {
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
                    passwordItemTreeItem = p.getSelectionModel().getSelectedItem();

                    if ((dragboard.hasContent(dataFormat)) &&
                            (destFolderTreeItem.getValue() instanceof Folder) &&
                            (destFolderTreeItem != passwordItemTreeItem.getParent())) {
                        e.acceptTransferModes(TransferMode.MOVE);
                    }
                });

                cell.setOnDragDropped(e -> {
                    try {
                        logger.info("Moving password item");
                        movePasswordItem(passwordItemTreeItem, destFolderTreeItem);
                        resultPasswordItemTreeItem = passwordItemTreeItem;
                        e.setDropCompleted(true);
                        logger.info("Password item has been moved");
                    } catch (SQLException e1) {
                        logger.error("Failed to move password item");
                        Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to move password item",
                                "A database error has occurred during the operation", Alert.AlertType.ERROR);
                        setPaneStyles(alertDialog.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
                        alertDialog.showAndWait();
                    }
                });

                cell.setOnDragDone(e -> {
                    if (resultPasswordItemTreeItem != null) {
                        p.getSelectionModel().select(resultPasswordItemTreeItem);
                    }
                });

                return cell;
            }
        });
    }

    private void setSelectedTreeItemListener() {
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                VaultItem vaultItem = newValue.getValue();
                passwordManagerPaneController.viewDataItemInQuickViewPane(vaultItem);

                if (vaultItem instanceof  RootFolder) {
                    passwordManagerPaneController.enableRootRelatedToolbarButtons();
                } else if (vaultItem instanceof Folder) {
                    passwordManagerPaneController.enableFolderRelatedToolbarButtons();
                } else if (vaultItem instanceof PasswordItem) {
                    passwordManagerPaneController.enablePasswordItemRelatedToolbarButtons();
                }
            }
            else {
                passwordManagerPaneController.hideQuickViewPane();
                passwordManagerPaneController.disableAllToolbarButtons();
            }
        });
    }

    void setViewMode() {
        this.treeViewMode = TreeViewMode.VIEW;
        treeViewMode.setTreeItem(null);
        treeView.setDisable(false);
    }

    private void setEditMode(TreeViewMode treeViewMode, TreeItem<VaultItem> treeItem) {
        this.treeViewMode = treeViewMode;
        treeViewMode.setTreeItem(treeItem);
        treeView.setDisable(true);
    }

    private void setWindowLogo(Stage stage, Object context, String imageFile) {
        stage.getIcons().add(new Image(Objects.requireNonNull(context.getClass().getResource(imageFile)).toString()));
    }

    private void setPaneStyles(Pane pane, String cssFile) {
        pane.getStylesheets().add(Objects.requireNonNull(getClass().getResource(cssFile)).toString());
    }
}
