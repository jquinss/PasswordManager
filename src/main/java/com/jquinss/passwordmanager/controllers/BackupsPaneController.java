package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class BackupsPaneController implements Initializable {

    @FXML
    private ListView<String> backupsListView;

    private ObservableList<String> backups;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize data collections

        backups = FXCollections.observableArrayList();
        backupsListView.setItems(backups);

        // TODO
        // Initialize backups by loading backups from filesystem
    }

    @FXML
    private void createBackup() {
        // Generate backup name with timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String backupName = "Backup_" + LocalDateTime.now().format(formatter);

        Alert confirmDialog = DialogBuilder.buildAlertDialog("Create Backup", "Create New Backup",
                "Create backup: " + backupName + "?", Alert.AlertType.CONFIRMATION);
        confirmDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
        setBackupsLogo(confirmDialog.getDialogPane());

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO
            // Create actual backup and show message at the bottom
        }
    }

    @FXML
    private void deleteBackup() {
        String selectedBackup = backupsListView.getSelectionModel().getSelectedItem();

        if (selectedBackup == null) {
            Alert alertDialog = DialogBuilder.buildAlertDialog("No Selection", "",
                    "Please select a backup to delete.", Alert.AlertType.WARNING);
            alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
            setBackupsLogo(alertDialog.getDialogPane());
            alertDialog.showAndWait();
            return;
        }

        Alert confirmDialog = DialogBuilder.buildAlertDialog("Confirm Deletion", "Delete Backup",
                "Are you sure you want to delete backup '" + selectedBackup + "'?", Alert.AlertType.CONFIRMATION);
        confirmDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
        setBackupsLogo(confirmDialog.getDialogPane());

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO
            // Delete actual backup and show message at the bottom
        }
    }

    private void setBackupsLogo(Stage stage) {
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResource("/com/jquinss/passwordmanager/images/profile.png")).toString()));
    }

    private void setBackupsLogo(Pane pane) {
        setBackupsLogo(((Stage) pane.getScene().getWindow()));
    }
}
