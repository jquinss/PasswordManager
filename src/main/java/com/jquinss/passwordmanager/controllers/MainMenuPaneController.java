package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.authentication.*;
import com.jquinss.passwordmanager.util.misc.FixedLengthFilter;
import com.jquinss.passwordmanager.util.misc.MessageDisplayUtil;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainMenuPaneController implements Initializable {
    @FXML
    private Button loginTabButton;
    @FXML
    private Button profilesTabButton;
    @FXML
    private Button backupsTabButton;
    @FXML
    private VBox loginPane;
    @FXML
    private VBox registrationPane;
    @FXML
    private BorderPane backupsPane;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private Label message;
    private static final Logger logger = LoggerFactory.getLogger(MainMenuPaneController.class);
    private final AppController appController;
    private final AuthenticationService authenticator = new AuthenticationService();

    public MainMenuPaneController(AppController appController) {
        this.appController = appController;
    }
    @FXML

    private void showLoginTab() {
        setActiveTab(loginPane, loginTabButton);
    }
    
    @FXML
    private void showRegistrationTab() {
        setActiveTab(registrationPane, profilesTabButton);
    }
    
    @FXML
    private void showBackupsTab() {
        setActiveTab(backupsPane, backupsTabButton);
    }
    
    private void setActiveTab(Pane contentToShow, Button activeButton) {
        // Hide all content panes
        hidePane(loginPane);
        hidePane(registrationPane);
        hidePane(backupsPane);
        
        // Show selected content
        showPane(contentToShow);

        // Update buttonstyles
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
    private void handleLogin() throws IOException {
        logger.info("Authenticating user");
        AuthenticationResult authenticationResult = authenticator.authenticate(loginPasswordField.getText());
        if (authenticationResult instanceof AuthenticationSuccess authenticationSuccess) {
            logger.info("Authentication successful");
            appController.loadProfileSelectionDialog(authenticationSuccess.vaultRepository());
        }
        else {
            AuthenticationStatus authenticationStatus = ((AuthenticationFailure) authenticationResult).authenticationStatus();
            logger.warn("Authentication failed: " + authenticationStatus.getMessage());
            showTemporaryErrorMessage("Authentication failed: " + authenticationStatus.getMessage());
            clearFields();
        }
    }

    private void showTemporaryMessage(String text, String styleClass) {
        MessageDisplayUtil.showTemporaryMessage(this.message, text, styleClass, 3);
    }

    private void showTemporaryErrorMessage(String text) {
        showTemporaryMessage(text, "error-message");
    }

    private void showTemporarySuccessMessage(String text) {
        showTemporaryMessage(text, "success-message");
    }

    private void clearFields() {
        loginPasswordField.clear();
    }

    private void initializeTextFormatters() {
        loginPasswordField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(50)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTextFormatters();
        // Show login tab by default
        showLoginTab();
    }
}