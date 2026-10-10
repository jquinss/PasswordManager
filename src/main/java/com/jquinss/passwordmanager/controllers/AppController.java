package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.app.AppContext;
import com.jquinss.passwordmanager.config.AppConfig;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import com.jquinss.passwordmanager.vault.repository.VaultRepository;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class AppController {
    private final Stage stage;
    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    public AppController(Stage stage) {
        this.stage = stage;
    }

    void loadMainMenu() throws IOException {
        logger.info("Loading Main Menu");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/MainMenuPane.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == MainMenuPaneController.class) {
                return new MainMenuPaneController(this);
            }
            if (controllerClass == RegistrationPaneController.class) {
                return new RegistrationPaneController();
            }
            if (controllerClass == BackupsPaneController.class) {
                return new BackupsPaneController();
            }

            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 800, 600);
        setWindowLogo(stage, this, "/com/jquinss/passwordmanager/images/logo.png");
        stage.setTitle("Password Manager");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    void loadProfileSelectionDialog(VaultRepository vaultRepository) throws IOException {
        logger.info("Loading Profile Selection Dialog");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/ProfileSelectionPane.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == ProfileSelectionPaneController.class) {
                return new ProfileSelectionPaneController(this, vaultRepository);
            }

            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 460, 320);
        setWindowLogo(stage, this, "/com/jquinss/passwordmanager/images/logo.png");
        stage.setTitle("Select a profile");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    void loadProfileManager(VaultRepository vaultRepository) throws IOException {
        logger.info("Loading Profile Manager");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/ProfileManagerPane.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == ProfileManagerPaneController.class) {
                return new ProfileManagerPaneController(this, vaultRepository);
            }

            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 540, 500);
        setWindowLogo(stage, this, "/com/jquinss/passwordmanager/images/logo.png");
        stage.setTitle("Manage profiles");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    void loadPasswordManager(AppContext appContext) throws IOException {
        logger.info("Loading Password Manager");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/PasswordManagerPane.fxml"));
        PasswordManagerPaneController passwordManagerPaneController = new PasswordManagerPaneController(this, appContext);

        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == PasswordManagerPaneController.class) {
                return passwordManagerPaneController;
            }

            if (controllerClass == PasswordItemEditorPaneController.class) {
                return new PasswordItemEditorPaneController(passwordManagerPaneController, appContext);
            }

            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 950, 640);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
        stage.setTitle("Password Manager");
        stage.setScene(scene);
        stage.show();


    }

    Stage getStage() {
        return stage;
    }

    void exitApplication() {
        stage.close();
    }

    private void setWindowLogo(Stage stage, Object context, String imageFile) {
        stage.getIcons().add(new Image(Objects.requireNonNull(context.getClass().getResource(imageFile)).toString()));
    }

    private void initializeApplicationDirectories() throws IOException {
        Files.createDirectories(Path.of(AppConfig.get("vault_db.path")).getParent());
        Files.createDirectories(Path.of(AppConfig.get("backups_db.path")).getParent());
    }

    public void initialize() throws IOException {
        logger.info("Initializing application");

        try {
            initializeApplicationDirectories();
            logger.info("Application directories initialized");
        }
        catch (IOException e) {
            logger.error("Failed to initialize application directories");
            DialogBuilder.buildAlertDialog("Error", "Error starting application",
                    "Failed to initialize application directories. The application will now exit.",
                    Alert.AlertType.ERROR).showAndWait();
            Platform.exit();
        }
        logger.info("Application initialized successfully");
        loadMainMenu();
    }
}
