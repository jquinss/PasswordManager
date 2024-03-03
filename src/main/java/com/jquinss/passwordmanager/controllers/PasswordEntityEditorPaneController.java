package com.jquinss.passwordmanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PasswordEntityEditorPaneController {
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField urlTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox passwordPolicyComboBox;
    @FXML
    private CheckBox passwordExpiresCheckBox;
    @FXML
    private DatePicker passwordExpirationDatePicker;
    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private void save() {
    }

    @FXML
    private void cancel() {
    }
}
