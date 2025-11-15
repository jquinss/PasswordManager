package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.UserProfile;
import com.jquinss.passwordmanager.exceptions.LoadKeyPairException;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.managers.SettingsManager;
import com.jquinss.passwordmanager.security.Authenticator;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import com.jquinss.passwordmanager.util.misc.MessageDisplayUtil;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.net.URL;
import java.security.KeyPair;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainMenuPaneController implements Initializable {

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
    private ComboBox<String> loginProfileComboBox;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private Label message;

    private ObservableList<String> userProfiles;

    private PasswordManagerController passwordManagerController;

    private final Authenticator authenticator = new Authenticator();
    
    @FXML
    private void showLoginTab() {
        setActiveTab(loginPane, loginTabButton);
        loadUserProfiles();
    }
    
    @FXML
    private void showProfilesTab() {
        setActiveTab(profilesPane, profilesTabButton);
    }
    
    @FXML
    private void showBackupsTab() {
        setActiveTab(backupsPane, backupsTabButton);
    }
    
    private void setActiveTab(Pane contentToShow, Button activeButton) {
        // Hide all content panes
        hidePane(loginPane);
        hidePane(profilesPane);
        hidePane(backupsPane);
        
        // Show selected content
        showPane(contentToShow);

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

    private void showPane(Pane pane) {
        pane.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), pane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void hidePane(Pane pane) {
        pane.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String userProfileName = loginProfileComboBox.getSelectionModel().getSelectedItem();
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
        loginPasswordField.clear();
    }

    protected void loadUserProfiles() {
        try {
            List<String> profiles = DatabaseManager.getInstance().getAllUserProfileNames();
            userProfiles.setAll(profiles);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        loginProfileComboBox.setItems(userProfiles);
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userProfiles = FXCollections.observableArrayList();
        // Show login tab by default
        showLoginTab();
    }
}