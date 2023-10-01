package com.jquinss.passwordmanager.app;

import com.jquinss.passwordmanager.controllers.ProfilesPaneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/passwordmanager/fxml/ProfilesPane.fxml"));
            primaryStage.setTitle("Password Manager");
            //primaryStage.getIcons().add(new Image(getClass().getResource("/com/jquinss/passwordmanager/images/logo.png").toString()));
            AnchorPane root = (AnchorPane) fxmlLoader.load();
            final ProfilesPaneController controller = fxmlLoader.getController();
            //controller.setStage(primaryStage);
            Scene scene = new Scene(root, 530, 370);
            //scene.getStylesheets().add(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css").toString());
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
