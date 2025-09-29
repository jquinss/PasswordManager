package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.UserProfile;
import com.jquinss.passwordmanager.exceptions.LoadKeyPairException;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.managers.SettingsManager;
import com.jquinss.passwordmanager.security.Authenticator;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Optional;

public class LoginPaneController {
    public Label message;
    @FXML
    private TextField userProfileNameTextField;
    @FXML
    private PasswordField passwordField;

    private PasswordManagerController passwordManagerController;

    private final Authenticator authenticator = new Authenticator();

    @FXML
    public void authenticateUserProfile() {
        String userProfileName = userProfileNameTextField.getText();
        String password = passwordField.getText();

        try {
            Optional<UserProfile> optional = DatabaseManager.getInstance().getUserProfileByName(userProfileName);
            if (optional.isPresent()) {
                UserProfile userProfile = optional.get();
                boolean validCredentials = authenticator.authenticate(userProfile, password);

                if (validCredentials) {
                    hideMessage();
                    KeyPair keyPair = loadKeyPair(userProfile, password);
                    passwordManagerController.loadPasswordManagerPane(userProfile, keyPair);
                }
                else {
                    showErrorMessage("Error: Invalid user profile name or password");
                    clearFields();
                }
            }
            else {
                showErrorMessage("Error: Invalid user profile name or password");
                clearFields();
            }
        }
        catch (SQLException e) {
            showErrorMessage("Error: Cannot connect to the database");
        }
        catch (LoadKeyPairException e) {
            showErrorMessage("Error: Error loading key-pair from database");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @FXML
    public void setUpUserProfile() throws IOException {
        passwordManagerController.loadUserProfileSetUpPane();
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
        userProfileNameTextField.clear();
        passwordField.clear();
    }

    private KeyPair loadKeyPair(UserProfile userProfile, String password) throws LoadKeyPairException {
        try {
            byte[] publicKey = userProfile.getPublicKey();
            byte[] encryptedPrivateKey = userProfile.getPrivateKey();

            byte[] salt = userProfile.getPasswordSalt();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(userProfile.getPrivateKeyIV());
            SecretKey key = CryptoUtils.getSecretKeyFromPassword(password, salt);
            byte[] privateKey = CryptoUtils.decrypt(encryptedPrivateKey, SettingsManager.getInstance().getSymmetricEncryptionAlgorithm(),
                    key, ivParameterSpec);
            return CryptoUtils.loadKeyPair(publicKey, privateKey, SettingsManager.getInstance().getKeyPairAlgorithm());
        }
        catch (Exception e) {
            throw new LoadKeyPairException();
        }
    }
}
