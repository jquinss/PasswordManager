package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.DataEntity;
import com.jquinss.passwordmanager.data.Folder;
import com.jquinss.passwordmanager.data.PasswordEntity;
import com.jquinss.passwordmanager.data.User;
import com.jquinss.passwordmanager.security.UserSession;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PasswordManagerPaneController implements Initializable {
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

    private void terminateUserSession() {
        userSession.terminate();
    }

    private void initializetreeViewController() {
        treeViewController = new TreeViewController(treeView, userSession, asymmetricCrypto);
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
    }

    void editPasswordEntityInEditor(PasswordEntity passwordEntity) {
        passwordEntityEditorPaneController.openPasswordEntityEditorInEditMode(passwordEntity);
    }

    void viewPasswordEntityInEditor(PasswordEntity passwordEntity) {
        passwordEntityEditorPaneController.openPasswordEntityEditorInViewMode(passwordEntity);
    }

    void savePasswordEntity(PasswordEntity passwordEntity) {
        treeViewController.savePasswordEntity(passwordEntity);
    }

    void cancelEditMode() {
        treeViewController.setViewMode();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializetreeViewController();
        initializePasswordEntityEditorPaneController();
    }
}
