package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.security.Authenticator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LoginPaneController {
    public Label message;
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
                hideMessage();
                showSuccessMessage("Authentication successful");
                passwordManagerController.loadPasswordManagerPane(); }
            else {
                showErrorMessage("Invalid username or password");
                clearFields();
            }
        }
        catch (SQLException e) {
            showErrorMessage("Error: Cannot connect to the database");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @FXML
    public void signUpUser() throws IOException {
        passwordManagerController.loadSignUpPane();
    }

    void setPasswordManagerController(PasswordManagerController passwordManagerController) {
        this.passwordManagerController = passwordManagerController;
    }

    private void showMessage(String text, String styleClass) {
        message.getStyleClass().remove(message.getStyleClass().toString());
        message.getStyleClass().add(styleClass);
        message.setText(text);
        message.setVisible(true);
    }

    private void showErrorMessage(String text) {
        showMessage(text, "error-message");
    }

    private void showSuccessMessage(String text) {
        showMessage(text, "success-message");
    }

    private void hideMessage() {
        message.setText("");
        message.setVisible(false);
    }

    private void clearFields() {
        usernameTextField.clear();
        passwordField.clear();
    }
}
