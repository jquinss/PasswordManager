package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.UserProfile;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.managers.SettingsManager;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;

public class PasswordManagerController {

    private final Stage stage;

    public PasswordManagerController(Stage stage) {
        this.stage = stage;
        initializeDatabase();
    }

    public void loadMainMenuPane() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/MainMenuPane.fxml"));
        stage.setTitle("Password Manager");
        BorderPane root = (BorderPane) fxmlLoader.load();
        final MainMenuPaneController controller = fxmlLoader.getController();
        controller.setPasswordManagerController(this);
        Scene scene = new Scene(root, 800, 600);
        setWindowLogo(stage, this, "/com/jquinss/passwordmanager/images/logo.png");
        stage.setScene(scene);
        stage.show();
    }

    void loadPasswordManagerPane(UserProfile userProfile, KeyPair keyPair) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/PasswordManagerPane.fxml"));
        stage.setTitle("Password Manager");
        CryptoUtils.AsymmetricCrypto asymmetricCrypto = new CryptoUtils.AsymmetricCrypto(SettingsManager.getInstance().getKeyPairAlgorithm(), keyPair);
        PasswordManagerPaneController controller = new PasswordManagerPaneController(userProfile, asymmetricCrypto);
        controller.setPasswordManagerController(this);
        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == PasswordManagerPaneController.class) {
                return controller;
            }

            if (controllerClass == PasswordEntityEditorPaneController.class) {
                return new PasswordEntityEditorPaneController();
            }

            return null;
        });
        VBox root = (VBox) fxmlLoader.load();
        Scene scene = new Scene(root, 950, 640);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
        stage.setScene(scene);
        stage.show();


    }

    Stage getStage() {
        return stage;
    }

    void exitApplication() {
        stage.close();
    }

    private void initializeDatabase() {
        try {
            Files.createDirectories(Path.of(SettingsManager.getInstance().getDatabaseDir()));
            DatabaseManager.getInstance().initializeDatabase();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setWindowLogo(Stage stage, Object context, String imageFile) {
        stage.getIcons().add(new Image(Objects.requireNonNull(context.getClass().getResource(imageFile)).toString()));
    }
}
