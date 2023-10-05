package com.jquinss.passwordmanager.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private final String databaseURL;
    private static final DatabaseManager databaseManager = new DatabaseManager();

    private DatabaseManager() {
        databaseURL = "jdbc:sqlite:" + SettingsManager.getInstance().getDatabasePath();
    }

    public static DatabaseManager getInstance() {
        return databaseManager;
    }

    public void initializeDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(databaseURL)) {
            if (conn != null) {
                System.out.println("Database has been created");
            }
        }
    }
}
