package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.managers.SettingsManager;
import javafx.fxml.FXML;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class ProfilesPaneController {
    @FXML
    private void addProfile() {
        // ToDo
    }

    @FXML
    private void loadProfile() {
        // ToDo
    }

    @FXML
    private void removeProfile() {
        // ToDo
    }

    @FXML
    public void initialize() {
        try {
            Files.createDirectories(Path.of(SettingsManager.getInstance().getDatabaseDir()));
            DatabaseManager.getInstance().initializeDatabase();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
