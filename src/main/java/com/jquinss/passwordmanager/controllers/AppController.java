package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.app.AppContext;
import com.jquinss.passwordmanager.config.AppConfig;
import com.jquinss.passwordmanager.dao.BackupsRepository;
import com.jquinss.passwordmanager.dao.VaultRepository;
import com.jquinss.passwordmanager.data.UserProfile;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
    }

    public void loadMainMenuPane() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/MainMenuPane.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == MainMenuPaneController.class) {
                return new MainMenuPaneController(this);
            }
            if (controllerClass == UserProfilesPaneController.class) {
                return new UserProfilesPaneController();
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

    void loadPasswordManagerPane(UserProfile userProfile, KeyPair keyPair) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/PasswordManagerPane.fxml"));

        CryptoUtils.AsymmetricCrypto asymmetricCrypto = new CryptoUtils.AsymmetricCrypto("RSA", keyPair);

        PasswordManagerPaneController passwordManagerPaneController = new PasswordManagerPaneController(this, vaultRepository,
                                                                                                userProfile, asymmetricCrypto);

        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == PasswordManagerPaneController.class) {
                return passwordManagerPaneController;
            }

            if (controllerClass == PasswordEntityEditorPaneController.class) {
                return new PasswordEntityEditorPaneController(passwordManagerPaneController, vaultRepository);
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

    void exitApplication() {
        stage.close();
    }

    private void setWindowLogo(Stage stage, Object context, String imageFile) {
        stage.getIcons().add(new Image(Objects.requireNonNull(context.getClass().getResource(imageFile)).toString()));
    }
}
