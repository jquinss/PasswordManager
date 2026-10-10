package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.app.AppContext;
import com.jquinss.passwordmanager.data.*;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PasswordManagerPaneController implements Initializable {
    @FXML
    private Label statusLabel;
    @FXML
    private ToolBar toolBar;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Button createPasswordItemToolbarButton;
    @FXML
    private Button deletePasswordItemToolbarButton;
    @FXML
    private Button createFolderToolbarButton;
    @FXML
    private Button deleteFolderToolbarButton;
    @FXML
    private Button duplicatePasswordItemToolbarButton;
    @FXML
    private Button viewPasswordItemToolbarButton;
    @FXML
    private Button editPasswordItemToolbarButton;
    @FXML
    private Button openPasswordPoliciesPaneToolbarButton;
    @FXML
    private Button openPasswordGeneratorPaneToolbarButton;
    @FXML
    private VBox quickViewPane;
    @FXML
    private Label itemName;
    @FXML
    private VBox itemDescriptionVBox;
    @FXML
    private TextArea itemDescription;
    @FXML
    private TreeView<VaultItem> treeView;
    @FXML
    private PasswordItemEditorPaneController passwordItemEditorPaneController;
    private final AppController appController;
    private final AppContext appContext;
    private final Logger logger = LoggerFactory.getLogger(PasswordManagerPaneController.class);
    private TreeViewController treeViewController;


    public PasswordManagerPaneController(AppController appController, AppContext appContext) {
        this.appController = appController;
        this.appContext = appContext;
    }

    @FXML
    public void exitApplication() {
        appController.exitApplication();
    }

    @FXML
    private void logOut() throws IOException {
        logger.info("Logging out");
        appController.loadMainMenu();
    }

    @FXML
    private void openPasswordPoliciesPane() throws IOException {
        logger.info("Opening Password Policies");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/PasswordPoliciesPane.fxml"));

        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == PasswordPoliciesPaneController.class) {
                return new PasswordPoliciesPaneController(appContext);
            }

            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 400, 380);
        Stage stage = new Stage();

        stage.setResizable(false);
        stage.setTitle("Password Policies");
        setWindowLogo(stage, this, "/com/jquinss/passwordmanager/images/password_policies.png");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    private void openPasswordGeneratorPane() throws IOException {
        logger.info("Opening Password Generator");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/PasswordGeneratorPane.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Password Generator");
        setWindowLogo(stage, this, "/com/jquinss/passwordmanager/images/password_generator.png");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    private void showAboutDialog() {
        logger.info("Opening About Dialog");
        Alert aboutDialog = DialogBuilder.buildAlertDialog("About", "", "Password Manager v1.0\n\nCreated by Joaquin Sampedro", Alert.AlertType.INFORMATION);
        aboutDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
        setWindowLogo((Stage) aboutDialog.getDialogPane().getScene().getWindow(), this, "/com/jquinss/passwordmanager/images/logo.png");
        aboutDialog.showAndWait();
    }

    @FXML
    private void createPasswordItem() {
        logger.info("Creating Password Item");
        treeViewController.createPasswordItem();
    }

    @FXML
    private void deletePasswordItem() {
        logger.info("Deleting Password Item");
        treeViewController.deletePasswordItem();
    }

    @FXML
    private void createFolder() {
        logger.info("Creating Folder");
        treeViewController.createFolder();
    }

    @FXML
    private void deleteFolder() {
        logger.info("Deleting Folder");
        treeViewController.deleteFolder();
    }

    @FXML
    private void duplicatePasswordItem() {
        logger.info("Duplicating Password Item");
        treeViewController.duplicatePasswordItem();
    }

    @FXML
    private void viewPasswordItem() {
        logger.info("Viewing Password Item");
        treeViewController.viewPasswordItem();
    }

    @FXML
    private void editPasswordItem() {
        logger.info("Editing Password Item");
        treeViewController.editPasswordItem();
    }

    private void disableRootRelatedToolbarButtons(boolean disable) {
        createFolderToolbarButton.setDisable(disable);
    }

    private void disableFolderRelatedToolBarButtons(boolean disable) {
        createPasswordItemToolbarButton.setDisable(disable);
        deleteFolderToolbarButton.setDisable(disable);
    }

    private void disableTemplateRelatedToolbarButtons(boolean disable) {
        deletePasswordItemToolbarButton.setDisable(disable);
        duplicatePasswordItemToolbarButton.setDisable(disable);
        viewPasswordItemToolbarButton.setDisable(disable);
        editPasswordItemToolbarButton.setDisable(disable);
    }

    void disableAllToolbarButtons() {
        disableRootRelatedToolbarButtons(true);
        disableFolderRelatedToolBarButtons(true);
        disableTemplateRelatedToolbarButtons(true);
    }

    void enableRootRelatedToolbarButtons() {
        disableRootRelatedToolbarButtons(false);
        disableFolderRelatedToolBarButtons(true);
        disableTemplateRelatedToolbarButtons(true);
    }

    void enableFolderRelatedToolbarButtons() {
        disableRootRelatedToolbarButtons(true);
        disableFolderRelatedToolBarButtons(false);
        disableTemplateRelatedToolbarButtons(true);
    }

    void enablePasswordItemRelatedToolbarButtons() {
        disableRootRelatedToolbarButtons(true);
        disableFolderRelatedToolBarButtons(true);
        disableTemplateRelatedToolbarButtons(false);
    }

    private void initializeTreeViewController() {
        treeViewController = new TreeViewController(this, appContext,
                treeView);
    }

    void viewDataItemInQuickViewPane(VaultItem vaultItem) {
        //quickViewPane.setVisible(true);
        showPane(quickViewPane);
        itemName.setText(vaultItem.getName());
        if (vaultItem.getDescription() != null) {
            itemDescriptionVBox.setVisible(true);
            itemDescription.setText(vaultItem.getDescription());
        }
        else {
            itemDescriptionVBox.setVisible(false);
        }
    }

    void hideQuickViewPane() {
        hidePane(quickViewPane);
        //quickViewPane.setVisible(false);
    }

    private void showPane(Pane pane) {
        pane.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), pane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void hidePane(Pane pane) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), pane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> pane.setVisible(false));
        fadeOut.play();
    }

    void createPasswordItemInEditor(Folder folder) {
        passwordItemEditorPaneController.openPasswordItemEditorInCreateMode(folder);
        statusLabel.setText("Creating password item");
        disableMenuBarAndToolBar(true);
    }

    void editPasswordItemInEditor(PasswordItem passwordItem) {
        passwordItemEditorPaneController.openPasswordItemEditorInEditMode(passwordItem);
        statusLabel.setText("Editing password item");
        disableMenuBarAndToolBar(true);
    }

    void viewPasswordItemInEditor(PasswordItem passwordItem) {
        passwordItemEditorPaneController.openPasswordItemEditorInViewMode(passwordItem);
    }

    void savePasswordItem(PasswordItem passwordItem) {
        treeViewController.savePasswordItem(passwordItem);
        disableMenuBarAndToolBar(false);
        statusLabel.setText("");
    }

    void cancelEditMode() {
        treeViewController.setViewMode();
        disableMenuBarAndToolBar(false);
        statusLabel.setText("");
    }

    private void disableMenuBarAndToolBar(boolean disable) {
        menuBar.setDisable(disable);
        toolBar.setDisable(disable);
    }

    private void setWindowLogo(Stage stage, Object context, String imageFile) {
        stage.getIcons().add(new Image(Objects.requireNonNull(context.getClass().getResource(imageFile)).toString()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTreeViewController();
    }
}
