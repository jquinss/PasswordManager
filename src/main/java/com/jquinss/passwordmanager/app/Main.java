package com.jquinss.passwordmanager.app;

import com.jquinss.passwordmanager.controllers.AppController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws IOException {
            new AppController(primaryStage).initialize();
    }

    public static void main(String[] args) {
        launch(args);
    }
}