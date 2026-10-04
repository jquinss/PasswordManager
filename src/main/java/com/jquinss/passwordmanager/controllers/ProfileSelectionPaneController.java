package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.app.AppContext;
import com.jquinss.passwordmanager.data.UserProfile;
import com.jquinss.passwordmanager.util.misc.MessageDisplayUtil;
import com.jquinss.passwordmanager.vault.repository.VaultRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;

public class ProfileSelectionPaneController implements Initializable {
    @FXML
    private ComboBox<UserProfile> profilesComboBox;
    @FXML
    private Button loadProfileButton;
    @FXML
    private Label message;
    private final Logger logger = LoggerFactory.getLogger(ProfileSelectionPaneController.class);
    private final AppController appController;
    private final VaultRepository vaultRepository;
    private final ObservableList<UserProfile> profiles = FXCollections.observableArrayList();

    public ProfileSelectionPaneController(AppController appController, VaultRepository vaultRepository) {
        this.appController = appController;
        this.vaultRepository = vaultRepository;
    }

    @FXML
    private void loadProfile() throws IOException {
        // TODO
        UserProfile profile = profilesComboBox.getSelectionModel().getSelectedItem();
        if (profile != null) {
            Properties sessionVariables = new Properties();
            sessionVariables.setProperty("profileName", profile.getName());
            sessionVariables.setProperty("profileId", Integer.toString(profile.getId()));
            AppContext appContext = new AppContext(vaultRepository, sessionVariables);
            appController.loadPasswordManager(appContext);
        }
    }

    @FXML
    private void openProfileManager() throws IOException {
        appController.loadProfileManager(vaultRepository);
    }

    @FXML
    private void cancel() throws IOException {
        appController.loadMainMenu();
    }

    private void initializeProfiles() {
        try {
            logger.info("Loading user profiles");
            profiles.setAll(vaultRepository.getAllUserProfiles());
            if (profiles.isEmpty()) {
                showErrorMessage("There are no user profiles");
                logger.info("There are no user profiles");
            }
            else {
                logger.info("User profiles have been loaded");
                selectDefaultProfile();
            }
        } catch (SQLException e) {
            logger.error("Error loading user profiles", e);
            showErrorMessage("Error loading user profiles");
        }
    }

    private void selectDefaultProfile() {
        for (UserProfile profile : profiles) {
            if (profile.isDefaultProfile()) {
                profilesComboBox.getSelectionModel().select(profile);
                break;
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        profilesComboBox.setItems(profiles);
        initializeProfiles();
    }

    private void showMessage(String text, String styleClass) {
        MessageDisplayUtil.showMessage(message, text, styleClass);
    }

    private void showErrorMessage(String text) {
        showMessage(text, "error-message");
    }
}
