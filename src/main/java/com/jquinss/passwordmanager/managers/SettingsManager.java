package com.jquinss.passwordmanager.managers;

import java.nio.file.Path;
import com.jquinss.passwordmanager.util.OSChecker;

public class SettingsManager {
    private static final String APP_FOLDER_NAME = "PasswordManager";
    private static final String DATA_FOLDER_NAME = "data";
    private static final String DB_NAME = "database.db";
    private static final SettingsManager settingsManager = new SettingsManager();

    private SettingsManager() {}

    public static SettingsManager getInstance() {
        return settingsManager;
    }

    public String getDatabasePath() {
        return Path.of(OSChecker.getOSDataDirectory(), APP_FOLDER_NAME, DATA_FOLDER_NAME, DB_NAME).toString();
    }

    public String getDatabaseDir() {
        return Path.of(OSChecker.getOSDataDirectory(), APP_FOLDER_NAME, DATA_FOLDER_NAME).toString();
    }
}
