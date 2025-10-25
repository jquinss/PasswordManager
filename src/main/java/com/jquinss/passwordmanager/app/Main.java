package com.jquinss.passwordmanager.app;

import com.jquinss.passwordmanager.controllers.PasswordManagerController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) {
        try {
            PasswordManagerController controller = new PasswordManagerController(primaryStage);
            controller.loadMainMenuPane();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
