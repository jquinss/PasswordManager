package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.PasswordPolicy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.function.ToDoubleBiFunction;

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
    private ComboBox<PasswordPolicy> passwordPolicyComboBox;
    @FXML
    private CheckBox passwordExpiresCheckBox;
    @FXML
    private DatePicker passwordExpirationDatePicker;
    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private void save() {
        // TODO
    }

    @FXML
    private void cancel() {
        // TODO
    }

    @FXML
    public void generatePassword() {
        // TODO
    }

    @FXML
    private void copyToClipboard(ActionEvent event) {
        // TODO
    }
}
