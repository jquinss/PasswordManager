package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.User;
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
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    private PasswordManagerController passwordManagerController;

    private final Authenticator authenticator = new Authenticator();

    @FXML
    public void authenticateUser() {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        try {
            Optional<User> optional = DatabaseManager.getInstance().getUserByName(username);
            if (optional.isPresent()) {
                User user = optional.get();
                boolean validCredentials = authenticator.authenticate(user, password);

                if (validCredentials) {
                    hideMessage();
                    showSuccessMessage("Authentication successful");
                    KeyPair keyPair = loadKeyPair(user, password);
                    passwordManagerController.loadPasswordManagerPane(user, keyPair);
                }
                else {
                    throw new LoadKeyPairException();
                }
            }
            else {
                showErrorMessage("Error: Invalid username or password");
                clearFields();
            }
        }
        catch (SQLException e) {
            showErrorMessage("Error: Cannot connect to the database");
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidAlgorithmParameterException | NoSuchPaddingException |
                IllegalBlockSizeException | BadPaddingException | InvalidKeyException | LoadKeyPairException e) {
            showErrorMessage("Error: Error loading key-pair from database");
        }
        catch (Exception e) {
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

    private KeyPair loadKeyPair(User user, String password) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
            byte[] publicKey = user.getPublicKey();
            byte[] encryptedPrivateKey = user.getPrivateKey();

            byte[] salt = user.getPasswordSalt();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(user.getPrivateKeyIV());
            SecretKey key = CryptoUtils.getSecretKeyFromPassword(password, salt);
            byte[] privateKey = CryptoUtils.decrypt(encryptedPrivateKey, SettingsManager.getInstance().getSymmetricEncryptionAlgorithm(),
                    key, ivParameterSpec);

        return CryptoUtils.loadKeyPair(publicKey, privateKey, SettingsManager.getInstance().getKeyPairAlgorithm());
    }
}
