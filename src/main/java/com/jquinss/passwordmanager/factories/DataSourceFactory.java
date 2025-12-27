package com.jquinss.passwordmanager.factories;

import javax.sql.DataSource;

import com.jquinss.passwordmanager.enums.DataSourceType;
import org.sqlite.SQLiteDataSource;

public class DataSourceFactory {
    public static DataSource getDataSource(DataSourceType type, String URL) {
        if (type == DataSourceType.SQLITE) {
            SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
            sqLiteDataSource.setUrl(URL);
            return sqLiteDataSource;
        }
        else {
            throw new IllegalArgumentException();
        }
    }
}
