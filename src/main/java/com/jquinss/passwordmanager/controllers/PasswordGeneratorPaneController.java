package com.jquinss.passwordmanager.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class PasswordGeneratorPaneController implements Initializable {
    @FXML
    private TextField numLowercaseCharsTextField;

    @FXML
    private TextField numUppercaseCharsTextField;

    @FXML
    private TextField numDigitsTextField;

    @FXML
    private TextField numSymbolsTetField;

    @FXML
    private TextField totalNumChars;

    @FXML
    private TextField password;

    @FXML
    private void generatePassword() {
        // TODO
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO
    }
}
