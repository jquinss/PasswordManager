package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.security.Authenticator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class LoginPaneController {
    public Label loginMessage;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private PasswordManagerController passwordManagerController;

    private final Authenticator authenticator = new Authenticator();

    @FXML
    public void authenticateUser() {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        try {
            boolean validCredentials = authenticator.authenticate(username, password);

            if (validCredentials) {
                setErrorMessage("", false);
                passwordManagerController.loadPasswordManagerPane(); }
            else {
                setErrorMessage("Invalid username or password", true);
            }
        }
        catch (SQLException e) {
            setErrorMessage("Error: Cannot connect to the database", true);
        }
    }
    
    @FXML
    public void signUpUser() {
        passwordManagerController.loadRegistrationPane();
    }

    void setPasswordManagerController(PasswordManagerController passwordManagerController) {
        this.passwordManagerController = passwordManagerController;
    }

    private void setErrorMessage(String text, boolean visible) {
        loginMessage.setVisible(visible);
        loginMessage.setText(text);
    }
}
