package com.jquinss.passwordmanager.factories;

import javax.sql.DataSource;
import org.sqlite.SQLiteDataSource;

public class DataSourceFactory {
    public static DataSource getDataSource(String type) {
        return switch (type) {
            case "SQLITE" -> new SQLiteDataSource();
            default -> throw new IllegalArgumentException();
        };
    }
}
