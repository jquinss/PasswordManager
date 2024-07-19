package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.util.password.PasswordGenerator;
import com.jquinss.passwordmanager.util.password.PasswordSpecs;
import com.jquinss.passwordmanager.util.password.PasswordStrength;
import com.jquinss.passwordmanager.util.password.PasswordStrengthChecker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.net.URL;
import java.util.ResourceBundle;

public class PasswordGeneratorPaneController implements Initializable {
    @FXML
    private Label passwordStrengthLabel;
    @FXML
    private Label passwordStrengthText;
    @FXML
    private Button copyToClipboardButton;
    @FXML
    private Spinner<Integer> numLowerCaseCharsSpinner;

    @FXML
    private Spinner<Integer> numUpperCaseCharsSpinner;

    @FXML
    private Spinner<Integer> numDigitsSpinner;

    @FXML
    private Spinner<Integer> numSymbolsSpinner;

    @FXML
    private TextField totalNumCharsTextField;

    @FXML
    private TextField passwordTextField;

    private PasswordGenerator passwordGenerator;
    private final PasswordStrengthChecker passwordStrengthChecker = new PasswordStrengthChecker();

    @FXML
    private void generatePassword() {
        passwordGenerator.setPasswordSpecs(getPasswordSpecs());
        passwordTextField.setText(passwordGenerator.generatePassword());
    }

    @FXML
    private void copyToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(passwordTextField.getText());
        clipboard.setContent(content);
    }

    private void setSpinnersListeners() {
        setSpinnerListener(numLowerCaseCharsSpinner);
        setSpinnerListener(numUpperCaseCharsSpinner);
        setSpinnerListener(numDigitsSpinner);
        setSpinnerListener(numSymbolsSpinner);
    }

    private void setSpinnerListener(Spinner<Integer> spinner) {
        spinner.valueProperty().addListener((obs, oldValue, newValue) -> setTotalNumberOfCharsTextField());
    }

    private void setTotalNumberOfCharsTextField() {
        int total = numLowerCaseCharsSpinner.getValue() + numUpperCaseCharsSpinner.getValue() +
                numSymbolsSpinner.getValue() + numDigitsSpinner.getValue();
        totalNumCharsTextField.setText(Integer.toString(total));
    }

    private void initializeSpinner(Spinner<Integer> spinner) {
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2));
    }

    private void initializeSpinners() {
        initializeSpinner(numLowerCaseCharsSpinner);
        initializeSpinner(numUpperCaseCharsSpinner);
        initializeSpinner(numDigitsSpinner);
        initializeSpinner(numSymbolsSpinner);
    }

    private void initializePasswordTextFieldListener() {
        passwordTextField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                passwordStrengthLabel.setVisible(true);
                PasswordStrength passwordStrength = passwordStrengthChecker.checkPasswordStrength(newValue);
                passwordStrengthText.setText(passwordStrength.toString().toLowerCase());
                passwordStrengthText.getStyleClass().clear();
                passwordStrengthText.getStyleClass().add(getPasswordStrengthTextStyle(passwordStrength));
            }
        });
    }

    private String getPasswordStrengthTextStyle(PasswordStrength passwordStrength) {
        return switch (passwordStrength) {
            case NONE -> "low-strength-pwd";
            case LOW -> "low-strength-pwd";
            case FAIR -> "fair-strength-pwd";
            case GOOD -> "good-strength-pwd";
            case EXCELLENT -> "excellent-strength-pwd";
        };
    }

    private void initializeTooltips() {
        Tooltip.install(copyToClipboardButton, new Tooltip("Copy to clipboard"));
    }

    private void initializePasswordGenerator() {
        passwordGenerator = new PasswordGenerator(getPasswordSpecs());
    }

    private PasswordSpecs getPasswordSpecs() {
        return new PasswordSpecs.Builder()
                .numLowerCaseChars(numLowerCaseCharsSpinner.getValue())
                .numUppercaseChars(numUpperCaseCharsSpinner.getValue())
                .numSymbols(numSymbolsSpinner.getValue())
                .numDigits(numDigitsSpinner.getValue())
                .build();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSpinners();
        setSpinnersListeners();
        initializePasswordTextFieldListener();
        initializeTooltips();
        setTotalNumberOfCharsTextField();
        initializePasswordGenerator();
    }
}
