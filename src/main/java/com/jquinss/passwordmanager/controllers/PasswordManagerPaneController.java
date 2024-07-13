package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.DataEntity;
import com.jquinss.passwordmanager.data.Folder;
import com.jquinss.passwordmanager.data.PasswordEntity;
import com.jquinss.passwordmanager.data.User;
import com.jquinss.passwordmanager.security.UserSession;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PasswordManagerPaneController implements Initializable {
    @FXML
    private ToolBar toolBar;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Button createPasswordEntityToolbarButton;
    @FXML
    private Button deletePasswordEntityToolbarButton;
    @FXML
    private Button createFolderToolbarButton;
    @FXML
    private Button deleteFolderToolbarButton;
    @FXML
    private Button duplicatePasswordEntityToolbarButton;
    @FXML
    private Button viewPasswordEntityToolbarButton;
    @FXML
    private Button editPasswordEntityToolbarButton;
    @FXML
    private Button openPasswordPoliciesPaneToolbarButton;
    @FXML
    private Button openPasswordGeneratorPaneToolbarButton;
    @FXML
    private VBox quickViewPane;
    @FXML
    private VBox entityNameVBox;
    @FXML
    private Label entityName;
    @FXML
    private VBox entityDescriptionVBox;
    @FXML
    private Label entityDescription;
    @FXML
    private TreeView<DataEntity> treeView;
    @FXML
    private PasswordEntityEditorPaneController passwordEntityEditorPaneController;
    private final UserSession userSession = new UserSession();
    private final CryptoUtils.AsymmetricCrypto asymmetricCrypto;
    private TreeViewController treeViewController;
    private PasswordManagerController passwordManagerController;

    public PasswordManagerPaneController(User user, CryptoUtils.AsymmetricCrypto asymmetricCrypto) {
        userSession.initiate(user);
        this.asymmetricCrypto = asymmetricCrypto;
    }

    void setPasswordManagerController(PasswordManagerController passwordManagerController) {
        this.passwordManagerController = passwordManagerController;
    }

    @FXML
    public void exitApplication() {
        passwordManagerController.exitApplication();
    }

    @FXML
    private void logOut() throws IOException {
        terminateUserSession();
        passwordManagerController.loadLoginPane();
    }

    @FXML
    private void openPasswordPoliciesPane() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/PasswordPoliciesPane.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 400, 380);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css")).toString());

        Stage stage = new Stage();

        final PasswordPoliciesPaneController controller = fxmlLoader.getController();
        controller.setPasswordManagerPaneController(this);
        controller.setStage(stage);
        controller.initializePolicies();

        stage.setResizable(false);
        stage.setTitle("Password Policies");
        setWindowLogo(stage, this, "/com/jquinss/passwordmanager/images/password_policies.png");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    private void openPasswordGeneratorPane() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/PasswordGeneratorPane.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css")).toString());

        Stage stage = new Stage();

        final PasswordGeneratorPaneController controller = fxmlLoader.getController();

        stage.setResizable(false);
        stage.setTitle("Password Generator");
        setWindowLogo(stage, this, "/com/jquinss/passwordmanager/images/password_generator.png");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    private void showAboutDialog() {
        Alert aboutDialog = DialogBuilder.buildAlertDialog("About", "", "Password Manager v1.0\n\nCreated by Joaquin Sampedro", Alert.AlertType.INFORMATION);
        aboutDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css")).toString());
        setWindowLogo((Stage) aboutDialog.getDialogPane().getScene().getWindow(), this, "/com/jquinss/passwordmanager/images/logo.png");
        aboutDialog.showAndWait();
    }

    @FXML
    private void createPasswordEntity() {
        treeViewController.createPasswordEntity();
    }

    @FXML
    private void deletePasswordEntity() {
        treeViewController.deletePasswordEntity();
    }

    @FXML
    private void createFolder() {
        treeViewController.createFolder();
    }

    @FXML
    private void deleteFolder() {
        treeViewController.deleteFolder();
    }

    @FXML
    private void duplicatePasswordEntity() {
        treeViewController.duplicatePasswordEntity();
    }

    @FXML
    private void viewPasswordEntity() {
        treeViewController.viewPasswordEntity();
    }

    @FXML
    private void editPasswordEntity() {
        treeViewController.editPasswordEntity();
    }

    private void disableRootRelatedToolbarButtons(boolean disable) {
        createFolderToolbarButton.setDisable(disable);
    }

    private void disableFolderRelatedToolBarButtons(boolean disable) {
        createPasswordEntityToolbarButton.setDisable(disable);
        deleteFolderToolbarButton.setDisable(disable);
    }

    private void disableTemplateRelatedToolbarButtons(boolean disable) {
        deletePasswordEntityToolbarButton.setDisable(disable);
        duplicatePasswordEntityToolbarButton.setDisable(disable);
        viewPasswordEntityToolbarButton.setDisable(disable);
        editPasswordEntityToolbarButton.setDisable(disable);
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

    void enablePasswordEntityRelatedToolbarButtons() {
        disableRootRelatedToolbarButtons(true);
        disableFolderRelatedToolBarButtons(true);
        disableTemplateRelatedToolbarButtons(false);
    }

    private void terminateUserSession() {
        userSession.terminate();
    }

    private void initializetreeViewController() {
        treeViewController = new TreeViewController(treeView, asymmetricCrypto);
        treeViewController.setPasswordManagerPaneController(this);
        treeViewController.initializeTreeView();
    }

    private void initializePasswordEntityEditorPaneController() {
        passwordEntityEditorPaneController.setPasswordManagerPaneController(this);
    }

    void viewDataEntityInQuickViewPane(DataEntity dataEntity) {
        quickViewPane.setVisible(true);
        entityName.setText(dataEntity.getName());
        if (dataEntity.getDescription() != null) {
            entityDescriptionVBox.setVisible(true);
            entityDescription.setText(dataEntity.getDescription());
        }
        else {
            entityDescriptionVBox.setVisible(false);
        }
    }

    void hideQuickViewPane() {
        quickViewPane.setVisible(false);
    }

    void createPasswordEntityInEditor(Folder folder) {
        passwordEntityEditorPaneController.openPasswordEntityEditorInCreateMode(folder);
        disableMenuBarAndToolBar(true);
    }

    void editPasswordEntityInEditor(PasswordEntity passwordEntity) {
        passwordEntityEditorPaneController.openPasswordEntityEditorInEditMode(passwordEntity);
        disableMenuBarAndToolBar(true);
    }

    void viewPasswordEntityInEditor(PasswordEntity passwordEntity) {
        passwordEntityEditorPaneController.openPasswordEntityEditorInViewMode(passwordEntity);
    }

    void savePasswordEntity(PasswordEntity passwordEntity) {
        treeViewController.savePasswordEntity(passwordEntity);
        disableMenuBarAndToolBar(false);
    }

    void cancelEditMode() {
        treeViewController.setViewMode();
        disableMenuBarAndToolBar(false);
    }

    private void disableMenuBarAndToolBar(boolean disable) {
        menuBar.setDisable(disable);
        toolBar.setDisable(disable);
    }

    UserSession getUserSession() {
        return userSession;
    }

    private void setWindowLogo(Stage stage, Object context, String imageFile) {
        stage.getIcons().add(new Image(Objects.requireNonNull(context.getClass().getResource(imageFile)).toString()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializetreeViewController();
        initializePasswordEntityEditorPaneController();
    }
}
