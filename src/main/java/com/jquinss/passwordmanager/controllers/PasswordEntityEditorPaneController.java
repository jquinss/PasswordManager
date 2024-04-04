package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.PasswordPolicy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.Validator;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class PasswordEntityEditorPaneController implements Initializable {
    public Button saveButton;
    @FXML
    private ScrollPane passwordEntityEditorMainPane;
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

    private final Validator validator = new Validator();

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

    private void initializeValidator() {
        createRequiredTextFieldCheck(nameTextField);
        createRequiredTextFieldCheck(passwordField);
        createDateCheck();
        saveButton.disableProperty().bind(validator.containsErrorsProperty());
    }

    private void createRequiredTextFieldCheck(TextField textField) {
        validator.createCheck()
                .withMethod(this::required)
                .dependsOn("text", textField.textProperty())
                .decorates(textField)
                .immediate();
    }

    private void required(Check.Context context) {
        String text = context.get("text");
        if (text == null || text.isEmpty()) {
            context.error("This field is required");
        }
    }

    private void createDateCheck() {
        validator.createCheck().withMethod(c -> {
            boolean isPasswordExpiresCheckBoxSelected = c.get("passwordExpires");
            String passwordExpirationDate = c.get("passwordExpirationDate");
            if (isPasswordExpiresCheckBoxSelected &&
                    !validateDate(passwordExpirationDate, "M/d/yyyy")) {
                c.error("You must enter a valid future date");
            }
        }).dependsOn("passwordExpires", passwordExpiresCheckBox.selectedProperty())
                .dependsOn("passwordExpirationDate", passwordExpirationDatePicker.getEditor().textProperty())
                .decorates(passwordExpirationDatePicker.getEditor())
                .immediate();
    }

    private boolean validateDate(String date, String pattern) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern)).isAfter(LocalDate.now());
        }
        catch (DateTimeParseException e) {
            return false;
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //passwordEntityEditorMainPane.setVisible(false);
        initializeValidator();
    }
}
