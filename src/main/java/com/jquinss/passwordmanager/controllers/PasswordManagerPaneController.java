package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.DataEntity;
import com.jquinss.passwordmanager.security.UserSession;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;

import java.io.IOException;

public class PasswordManagerPaneController {
    @FXML
    public TreeView<DataEntity> treeView;
    @FXML
    private PasswordEntityEditorPaneController passwordEntityEditorPaneController;
    private final UserSession userSession = new UserSession();
    private final CryptoUtils.AsymmetricCrypto asymmetricCrypto;
    private PasswordManagerController passwordManagerController;

    public PasswordManagerPaneController(String username, CryptoUtils.AsymmetricCrypto asymmetricCrypto) {
        userSession.initiate(username);
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
}
