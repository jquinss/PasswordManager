package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.managers.SettingsManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Objects;

public class PasswordManagerController {

    private final Stage stage;

    public PasswordManagerController(Stage stage) {
        this.stage = stage;
        initializeDatabase();
    }

    public void loadLoginPane() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/LoginPane.fxml"));
        stage.setTitle("Log on");
        AnchorPane root = (AnchorPane) fxmlLoader.load();
        final LoginPaneController controller = fxmlLoader.getController();
        controller.setPasswordManagerController(this);
        Scene scene = new Scene(root, 560, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css")).toString());
        stage.setScene(scene);
        stage.show();
    }

    void loadSignUpPane() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/SignUpPane.fxml"));
        stage.setTitle("Sign Up");
        AnchorPane root = (AnchorPane) fxmlLoader.load();
        final SignUpPaneController controller = fxmlLoader.getController();
        controller.setPasswordManagerController(this);
        Scene scene = new Scene(root, 560, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css")).toString());
        stage.setScene(scene);
        stage.show();
    }

    void loadPasswordManagerPane() {
        // TODO
        System.out.println("Loading Password Manager Pane");
    }

    Stage getStage() {
        return stage;
    }

    private void initializeDatabase() {
        try {
            Files.createDirectories(Path.of(SettingsManager.getInstance().getDatabaseDir()));
            DatabaseManager.getInstance().initializeDatabase();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
