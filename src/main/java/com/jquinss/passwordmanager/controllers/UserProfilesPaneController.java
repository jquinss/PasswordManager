package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
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

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserProfilesPaneController implements Initializable {
    @FXML
    private ListView<String> profilesListView;
    @FXML
    private Label message;
    private ObservableList<String> userProfiles;

    @FXML
    private void addUserProfile() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/UserProfileSetUpPane.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 580, 420);
        Stage stage = new Stage();

        final UserProfileSetUpPaneController controller = fxmlLoader.getController();
        //controller.setPasswordManagerPaneController(this);
        controller.setStage(stage);

        stage.setResizable(false);
        stage.setTitle("Create Profile");
        setProfileLogo(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    private void removeUserProfile() {
        String selectedProfile = profilesListView.getSelectionModel().getSelectedItem();

        if (selectedProfile == null) {
            Alert alertDialog = DialogBuilder.buildAlertDialog("No Selection", "",
                    "Please select a profile to remove.", Alert.AlertType.WARNING);
            alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
            setProfileLogo(alertDialog.getDialogPane());
            alertDialog.showAndWait();

            return;
        }

        Alert confirmationDialog = DialogBuilder.buildAlertDialog("Confirm Deletion", "Remove Profile",
                "Are you sure you want to remove the profile '" + selectedProfile + "'?", Alert.AlertType.CONFIRMATION);
        confirmationDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
        setProfileLogo(confirmationDialog.getDialogPane());

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO remove profile from the database
            userProfiles.remove(selectedProfile);
            // TODO Show message that the profile has been removed
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userProfiles = FXCollections.observableArrayList();
        profilesListView.setItems(userProfiles);
        loadUserProfiles();
    }

    private void loadUserProfiles() {
        try {
            List<String> profiles = DatabaseManager.getInstance().getAllUserProfileNames();
            userProfiles.clear();
            userProfiles.addAll(profiles);
        }
        catch (SQLException e) {
            message.setText("An error has occurred while trying to retrieve the profiles from the database.");
            message.setVisible(true);
        }
    }

    private void setProfileLogo(Stage stage) {
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResource("/com/jquinss/passwordmanager/images/profile.png")).toString()));
    }

    private void setProfileLogo(Pane pane) {
        setProfileLogo((Stage) pane.getScene().getWindow());
    }
}
