package com.jquinss.passwordmanager.managers;

import com.jquinss.passwordmanager.dao.DbUserDao;
import com.jquinss.passwordmanager.dao.DbUserProfileDao;
import com.jquinss.passwordmanager.data.User;
import com.jquinss.passwordmanager.data.UserProfile;
import com.jquinss.passwordmanager.factories.DataSourceFactory;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class DatabaseManager {
    private static final String CREATE_TABLES_STATEMENT = """
            CREATE TABLE IF NOT EXISTS user (user_id INTEGER PRIMARY KEY, user_name TEXT UNIQUE NOT NULL,
            password BLOB NOT NULL, password_salt BLOB NOT NULL, public_key BLOB NOT NULL, private_key BLOB NOT NULL, private_key_iv BLOB NOT NULL);
            CREATE TABLE IF NOT EXISTS folder (folder_id INTEGER PRIMARY KEY, parent_folder_id INTEGER folder_name TEXT NOT NULL);
            CREATE TABLE IF NOT EXISTS password_entity (password_entity_id INTEGER PRIMARY KEY, name TEXT NOT NULL, 
            username TEXT NOT NULL, password TEXT NOT NULL, URL TEXT, comment TEXT, expires INTEGER NOT NULL DEFAULT 0,
            expiration_date TEXT, user_id INTEGER NOT NULL, folder_id INTEGER NOT NULL,
            password_policy_id INTEGER NOT NULL, FOREIGN KEY(user_id) REFERENCES user(user_id),
            FOREIGN KEY(folder_id) REFERENCES folder(folder_id)), FOREIGN KEY(password_policy_id) REFERENCES password_policy(password_policy_id);
            CREATE TABLE IF NOT EXISTS password_policy (password_policy_id INTEGER PRIMARY KEY, password_policy_name TEXT NOT NULL,
            min_length INTEGER NOT NULL, min_lower_case_chars INT NOT NULL, min_uppper_case_chars INT NOT NULL,
            min_digits INT NOT NULL, min_symbols INT NOT NULL, max_consec_equal_chars INT NOT NULL);
            """;

    private static final String databaseURL = "jdbc:sqlite:" + SettingsManager.getInstance().getDatabasePath();
    private final DataSource dataSource = initializeDataSource();
    private final DbUserDao dbUserDao = new DbUserDao(dataSource);
    private static final DatabaseManager databaseManager = new DatabaseManager();

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        return databaseManager;
    }

    public Optional<User> getUserByName(String name) throws SQLException {
        return dbUserDao.getByName(name);
    }

    public void addUser(User user) throws SQLException {
        dbUserDao.add(user);
    }

    public void initializeDatabase() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                stmt.execute(CREATE_TABLES_STATEMENT);
                System.out.println("Database has been created");
            }
        }
    }

    private DataSource initializeDataSource() {
        SQLiteDataSource dataSource = (SQLiteDataSource) DataSourceFactory.getDataSource("SQLITE");
        dataSource.setUrl(databaseURL);
        return dataSource;
    }
}
