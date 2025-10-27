package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.UserProfile;
import com.jquinss.passwordmanager.exceptions.LoadKeyPairException;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.managers.SettingsManager;
import com.jquinss.passwordmanager.security.Authenticator;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import com.jquinss.passwordmanager.util.misc.MessageDisplayUtil;
import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.KeyPair;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class MainMenuPaneController {

    // Tab buttons
    @FXML
    private Button loginTabButton;
    @FXML
    private Button profilesTabButton;
    @FXML
    private Button backupsTabButton;
    
    // Content panes
    @FXML
    private VBox loginPane;
    @FXML
    private BorderPane profilesPane;
    @FXML
    private BorderPane backupsPane;
    @FXML
    private TextField loginProfileField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private Label message;

    private PasswordManagerController passwordManagerController;

    private final Authenticator authenticator = new Authenticator();

    @FXML
    public void initialize() {
        // Show login tab by default
        showLoginTab();
    }
    
    @FXML
    private void showLoginTab() {
        setActiveTab(loginPane, loginTabButton);
    }
    
    @FXML
    private void showProfilesTab() {
        setActiveTab(profilesPane, profilesTabButton);
    }
    
    @FXML
    private void showBackupsTab() {
        setActiveTab(backupsPane, backupsTabButton);
    }
    
    private void setActiveTab(javafx.scene.Node contentToShow, Button activeButton) {
        // Hide all content panes
        loginPane.setVisible(false);
        profilesPane.setVisible(false);
        backupsPane.setVisible(false);
        
        // Show selected content
        contentToShow.setVisible(true);

        // Update button styles
        String inactiveStyleClass = "side_bar_btn_inactive";
        String activeStyleClass = "side_bar_btn_active";

        for (Button button : Arrays.asList(loginTabButton, profilesTabButton, backupsTabButton)) {
            ObservableList<String> buttonStyles = button.getStyleClass();

            if (button == activeButton) {
                if (!buttonStyles.contains(activeStyleClass)) {
                    buttonStyles.add(activeStyleClass);
                }
                buttonStyles.remove(inactiveStyleClass);

            } else {
                if (!buttonStyles.contains(inactiveStyleClass)) {
                    buttonStyles.add(inactiveStyleClass);
                }
                buttonStyles.remove(activeStyleClass);
            }
        }
    }

    @FXML
    private void handleLogin() {
        String userProfileName = loginProfileField.getText();
        String password = loginPasswordField.getText();

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

    void setPasswordManagerController(PasswordManagerController passwordManagerController) {
        this.passwordManagerController = passwordManagerController;
    }

    private void showTemporaryMessage(String text, String styleClass) {
        MessageDisplayUtil.showTemporaryMessage(this.message, text, styleClass, 3);
    }

    private void showErrorMessage(String text) {
        showTemporaryMessage(text, "error-message");
    }

    private void showSuccessMessage(String text) {
        showTemporaryMessage(text, "success-message");
    }

    private void hideMessage() {
        message.setText("");
        message.setVisible(false);
    }

    private void clearFields() {
        loginProfileField.clear();
        loginPasswordField.clear();
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