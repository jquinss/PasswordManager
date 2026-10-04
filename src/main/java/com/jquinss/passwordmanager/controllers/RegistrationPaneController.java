package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.authentication.*;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import com.jquinss.passwordmanager.util.misc.FixedLengthFilter;
import com.jquinss.passwordmanager.util.misc.MessageDisplayUtil;
import com.jquinss.passwordmanager.util.password.PasswordStrength;
import com.jquinss.passwordmanager.util.password.PasswordStrengthChecker;
import com.jquinss.passwordmanager.util.password.PasswordStrengthCriteria;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import net.synedra.validatorfx.Validator;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class RegistrationPaneController implements Initializable {
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button registerButton;
    @FXML
    private Label message;
    private final Validator validator = new Validator();
    private final PasswordStrengthChecker passwordStrengthChecker = new PasswordStrengthChecker();
    private final RegistrationService registrationService = new RegistrationService();

    @FXML
    void handleRegistration() {
        RegistrationResult registrationResult = registrationService.register(passwordField.getText());
        if (registrationResult instanceof RegistrationFailure registrationFailure &&
                (registrationFailure.registrationStatus() == RegistrationStatus.VAULT_ALREADY_EXISTS ||
            registrationFailure.registrationStatus() == RegistrationStatus.VAULT_PARTIALLY_EXISTS)) {
            handleExistingVault(registrationFailure.registrationStatus());
        }
        else {
            handleRegistrationResult(registrationResult);
        }
        clearPasswordFields();
    }

    private void handleExistingVault(RegistrationStatus registrationStatus) {
        Dialog<ButtonType> confirmationDialog = DialogBuilder.buildConfirmationDialog("Vault creation",
                registrationStatus.getMessage(), "Are you sure you want to override the existing vault?");
        confirmationDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
        confirmationDialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                RegistrationResult registrationResult = registrationService.register(passwordField.getText(), true);
                if (registrationResult instanceof RegistrationFailure registrationFailure &&
                        registrationFailure.registrationStatus() == RegistrationStatus.VAULT_DELETION_FAILED) {
                    showErrorDialog(registrationResult.toString());
                }
                else {
                    handleRegistrationResult(registrationResult);
                }
            }
        });
    }

    private void handleRegistrationResult(RegistrationResult registrationResult) {
        if (registrationResult instanceof RegistrationSuccess) {
            showSuccessMessage(registrationResult.toString());
        }
        else {
            showErrorDialog(registrationResult.toString());
        }
    }

    public void createPasswordStrengthRequirementsCheck() {
        validator.createCheck()
                .withMethod(c -> {
                    PasswordStrength pwdStrength = passwordStrengthChecker.checkPasswordStrength(passwordField.getText());
                    PasswordStrength minPwdStrength = PasswordStrength.EXCELLENT;
                    if (pwdStrength.getValue() < minPwdStrength.getValue()) {
                        PasswordStrengthCriteria pwdStrengthCriteria =
                                passwordStrengthChecker.getCriteria(minPwdStrength);
                        c.error("The password must meet the following requirements:\n" + pwdStrengthCriteria.toString());
                    }
                })
                .dependsOn("passwordField", passwordField.textProperty())
                .decorates(passwordField)
                .decorates(confirmPasswordField)
                .immediate();
    }

    private void createPasswordsMustMatchCheck() {
        validator.createCheck()
                .withMethod(c -> {
                    if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                        c.error("Both fields are required and must match");
                    }
                })
                .dependsOn("passwordField", passwordField.textProperty())
                .dependsOn("confirmPasswordField",confirmPasswordField.textProperty())
                .decorates(passwordField)
                .decorates(confirmPasswordField)
                .immediate();
    }
    private void initializeValidator() {
        createPasswordStrengthRequirementsCheck();
        createPasswordsMustMatchCheck();
        registerButton.disableProperty().bind(validator.containsErrorsProperty());
    }

    private void initializeTextFormatters() {
        passwordField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(50)));
        confirmPasswordField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(50)));
    }

    private void showTemporaryMessage(String text, String styleClass) {
        MessageDisplayUtil.showTemporaryMessage(this.message, text, styleClass, 3);
    }

    private void showSuccessMessage(String text) {
        showTemporaryMessage(text, "success-message");
    }

    private void showErrorDialog(String text) {
        Alert alert = DialogBuilder.buildAlertDialog("Error", "Error creating vault",
                text, Alert.AlertType.ERROR);

        setPaneStyles(alert.getDialogPane(), "/com/jquinss/passwordmanager/styles/styles.css");
        alert.showAndWait();
    }

    private void clearPasswordFields() {
        passwordField.clear();
        confirmPasswordField.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeValidator();
        initializeTextFormatters();
    }

    private void setPaneStyles(Pane pane, String cssFile) {
        pane.getStylesheets().add(Objects.requireNonNull(getClass().getResource(cssFile)).toString());
    }
}
