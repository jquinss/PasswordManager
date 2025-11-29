package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.*;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import com.jquinss.passwordmanager.util.misc.MessageDisplayUtil;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserProfilesPaneController implements Initializable {
    @FXML
    private TableView<UserProfile> userProfilesTableView;
    @FXML
    private TableColumn<UserProfile, String> userProfileNameTableColumn;
    @FXML
    private TableColumn<UserProfile, String> isDefaultUserProfileTableColumn;
    @FXML
    private Label message;
    private ObservableList<UserProfile> userProfiles;

    @FXML
    private void addUserProfile() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/UserProfileSetUpPane.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 580, 450);
        Stage stage = new Stage();

        final UserProfileSetUpPaneController controller = fxmlLoader.getController();
        controller.setUserProfilesController(this);
        controller.setStage(stage);

        stage.setResizable(false);
        stage.setTitle("Create Profile");
        setProfileLogo(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    private void deleteUserProfile() {
        UserProfile selectedProfile = userProfilesTableView.getSelectionModel().getSelectedItem();

        if (selectedProfile == null) {
            Alert alertDialog = DialogBuilder.buildAlertDialog("No Selection", "",
                    "Please select a profile to delete.", Alert.AlertType.WARNING);
            alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
            setProfileLogo(alertDialog.getDialogPane());
            alertDialog.showAndWait();

            return;
        }

        Alert confirmationDialog = DialogBuilder.buildAlertDialog("Confirm Deletion", "Delete Profile",
                "Are you sure you want to delete the profile '" + selectedProfile.getName() + "'?", Alert.AlertType.CONFIRMATION);
        confirmationDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
        setProfileLogo(confirmationDialog.getDialogPane());

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Optional<UserProfile> optional = DatabaseManager.getInstance().getUserProfileByName(selectedProfile.getName());
                optional.ifPresent(userProfile -> {
                    try {
                        deleteUserProfile(userProfile);
                        userProfiles.remove(selectedProfile);

                        showSuccessMessage("The profile has been removed");
                    }
                    catch (SQLException | RuntimeException e) {
                        showErrorMessage("A problem has occurred while trying to remove the profile");
                    }
                });
            }
            catch (SQLException e) {
                showErrorMessage("An error has occurred while trying to retrieve the selected profile from the database.");
            }
        }
    }

    void addUserProfileToList(UserProfile userProfile) {
        this.userProfiles.add(userProfile);
    }

    private void deleteUserProfile(UserProfile userProfile) throws SQLException {
        Optional<RootFolder> optional = DatabaseManager.getInstance().getRootFolderByUserProfileId(userProfile.getId());
        optional.ifPresent(rootFolder -> {
            try {
                List<Folder> folders = DatabaseManager.getInstance().getAllFoldersByParentFolderId(rootFolder.getId());
                DatabaseManager.getInstance().deleteFolders(folders);
                DatabaseManager.getInstance().deleteRootFolder(rootFolder);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        deletePasswordEnforcementPolicies(userProfile.getId());
        deletePasswordGeneratorPolicies(userProfile.getId());
        deletePasswordEntities(userProfile.getId());
        DatabaseManager.getInstance().deleteUserProfile(userProfile);
    }

    private void deletePasswordEntities(int userProfileId) throws SQLException {
        List<PasswordEntity> passwordEntities = DatabaseManager.getInstance().getAllPasswordEntitiesByUserProfileId(userProfileId);
        DatabaseManager.getInstance().deletePasswordEntities(passwordEntities);
    }

    private void deletePasswordEnforcementPolicies(int userProfileId) throws SQLException {
        List<PasswordEnforcementPolicy> passwordEnforcementPolicies =
                DatabaseManager.getInstance().getAllPasswordEnforcementPoliciesByUserProfileId(userProfileId);
        DatabaseManager.getInstance().deletePasswordEnforcementPolicies(passwordEnforcementPolicies);
    }

    private void deletePasswordGeneratorPolicies(int userProfileId) throws SQLException {
        List<PasswordGeneratorPolicy> passwordGeneratorPolicies =
                DatabaseManager.getInstance().getAllPasswordGeneratorPoliciesByUserProfileId(userProfileId);
        DatabaseManager.getInstance().deletePasswordGeneratorPolicies(passwordGeneratorPolicies);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userProfiles = FXCollections.observableArrayList();
        userProfilesTableView.setItems(userProfiles);
        initializeTableViewCellValueFactory();
        loadUserProfiles();
    }

    private void initializeTableViewCellValueFactory() {
        userProfileNameTableColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getName());
        });

        isDefaultUserProfileTableColumn.setCellValueFactory(cellData -> {
            boolean state = cellData.getValue().isDefaultProfile();
            return state ? new SimpleStringProperty("Yes") : new SimpleStringProperty("No");
        });
    }

    private void loadUserProfiles() {
        try {
            List<UserProfile> profiles = DatabaseManager.getInstance().getAllUserProfiles();
            userProfiles.setAll(profiles);
        }
        catch (SQLException e) {
            showErrorMessage("An error has occurred while trying to retrieve the profiles from the database.");
        }
    }

    private void setProfileLogo(Stage stage) {
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResource("/com/jquinss/passwordmanager/images/profile.png")).toString()));
    }

    private void setProfileLogo(Pane pane) {
        setProfileLogo((Stage) pane.getScene().getWindow());
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
}
