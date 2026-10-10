package com.jquinss.passwordmanager.factories;

import javax.sql.DataSource;

import com.jquinss.passwordmanager.enums.DataSourceType;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.util.Properties;

public class DataSourceFactory {
    private DataSourceFactory() {}
    public static DataSource getDataSource(DataSourceType type, String URL, Properties properties) {
        if (type == DataSourceType.SQLITE) {
            SQLiteDataSource sqLiteDataSource = new SQLiteDataSource(new SQLiteConfig(properties));
            sqLiteDataSource.setUrl(URL);
            return sqLiteDataSource;
        }
        else {
            throw new IllegalArgumentException();
        }
    }
}
