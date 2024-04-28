package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.DataEntity;
import com.jquinss.passwordmanager.data.Folder;
import com.jquinss.passwordmanager.data.PasswordEntity;
import com.jquinss.passwordmanager.data.User;
import com.jquinss.passwordmanager.enums.EditorMode;
import com.jquinss.passwordmanager.managers.TreeViewManager;
import com.jquinss.passwordmanager.security.UserSession;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PasswordManagerPaneController implements Initializable {
    @FXML
    public TreeView<DataEntity> treeView;
    @FXML
    private PasswordEntityEditorPaneController passwordEntityEditorPaneController;
    private final UserSession userSession = new UserSession();
    private final CryptoUtils.AsymmetricCrypto asymmetricCrypto;
    private TreeViewManager treeViewManager;
    private PasswordManagerController passwordManagerController;

    public PasswordManagerPaneController(User user, CryptoUtils.AsymmetricCrypto asymmetricCrypto) {
        userSession.initiate(user);
        this.asymmetricCrypto = asymmetricCrypto;
    }

    void setPasswordManagerController(PasswordManagerController passwordManagerController) {
        this.passwordManagerController = passwordManagerController;
    }

    private void terminateUserSession() {
        userSession.terminate();
    }

    private void logOut() throws IOException {
        terminateUserSession();
        passwordManagerController.loadLoginPane();
    }

    private void initializeTreeViewManager() {
        treeViewManager = new TreeViewManager(treeView, userSession, asymmetricCrypto);
        treeViewManager.setPasswordManagerPaneController(this);
        treeViewManager.initializeTreeView();
    }

    private void initializePasswordEntityEditorPaneController() {
        passwordEntityEditorPaneController.setPasswordManagerPaneController(this);
    }



    public void createPasswordEntityInEditor(Folder folder) {
        passwordEntityEditorPaneController.openPasswordEntityEditorInCreateMode(folder);
    }

    public void editPasswordEntityInEditor(PasswordEntity passwordEntity) {
        passwordEntityEditorPaneController.openPasswordEntityEditorInEditMode(passwordEntity);
    }

    public void viewPasswordEntityInEditor(PasswordEntity passwordEntity) {
        passwordEntityEditorPaneController.openPasswordEntityEditorInViewMode(passwordEntity);
    }

    void savePasswordEntity(PasswordEntity passwordEntity) {
        treeViewManager.savePasswordEntity(passwordEntity);
    }

    void cancelEditMode() {
        treeViewManager.setViewMode();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTreeViewManager();
        initializePasswordEntityEditorPaneController();
    }
}
