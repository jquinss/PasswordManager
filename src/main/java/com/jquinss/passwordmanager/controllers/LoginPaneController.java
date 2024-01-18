package com.jquinss.passwordmanager.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginPaneController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    @FXML
    public void authenticateUser() {
        System.out.println("Authenticating user");
    }
    
    @FXML
    public void signUpUser() {
        System.out.println("Loading registration pane");
    }
}
