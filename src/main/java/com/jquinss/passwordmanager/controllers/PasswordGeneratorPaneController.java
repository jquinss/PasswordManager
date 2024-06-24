package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.util.password.PasswordGenerator;
import com.jquinss.passwordmanager.util.password.PasswordSpecs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.net.URL;
import java.util.ResourceBundle;

public class PasswordGeneratorPaneController implements Initializable {
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
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 20, 2));
    }

    private void initializeSpinners() {
        initializeSpinner(numLowerCaseCharsSpinner);
        initializeSpinner(numUpperCaseCharsSpinner);
        initializeSpinner(numDigitsSpinner);
        initializeSpinner(numSymbolsSpinner);
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
        setTotalNumberOfCharsTextField();
        initializePasswordGenerator();
    }
}
