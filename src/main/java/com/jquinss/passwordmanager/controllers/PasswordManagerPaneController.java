package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.DataEntity;
import com.jquinss.passwordmanager.data.Folder;
import com.jquinss.passwordmanager.data.PasswordEntity;
import com.jquinss.passwordmanager.data.User;
import com.jquinss.passwordmanager.security.UserSession;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PasswordManagerPaneController implements Initializable {
    @FXML
    public MenuBar menuBar;
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
    public void openPasswordPoliciesPane() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/PasswordPoliciesPane.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 400, 380);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css")).toString());

        Stage stage = new Stage();

        final PasswordPoliciesPaneController controller = fxmlLoader.getController();
        controller.setStage(stage);

        stage.setResizable(false);
        stage.setTitle("Password Policies");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    public void showAboutDialog() {
        Alert aboutDialog = DialogBuilder.buildAlertDialog("About", "", "Password Manager v1.0\n\nCreated by Joaquin Sampedro", Alert.AlertType.INFORMATION);
        aboutDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css")).toString());
        aboutDialog.showAndWait();
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
        // TODO

    }

    void createPasswordEntityInEditor(Folder folder) {
        passwordEntityEditorPaneController.openPasswordEntityEditorInCreateMode(folder);
        menuBar.setDisable(true);
    }

    void editPasswordEntityInEditor(PasswordEntity passwordEntity) {
        passwordEntityEditorPaneController.openPasswordEntityEditorInEditMode(passwordEntity);
        menuBar.setDisable(true);
    }

    void viewPasswordEntityInEditor(PasswordEntity passwordEntity) {
        passwordEntityEditorPaneController.openPasswordEntityEditorInViewMode(passwordEntity);
    }

    void savePasswordEntity(PasswordEntity passwordEntity) {
        treeViewController.savePasswordEntity(passwordEntity);
    }

    void cancelEditMode() {
        treeViewController.setViewMode();
        menuBar.setDisable(false);
    }

    UserSession getUserSession() {
        return userSession;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializetreeViewController();
        initializePasswordEntityEditorPaneController();
    }
}
