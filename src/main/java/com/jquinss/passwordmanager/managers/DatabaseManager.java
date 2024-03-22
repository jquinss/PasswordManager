package com.jquinss.passwordmanager.managers;

import com.jquinss.passwordmanager.dao.DbUserDao;
import com.jquinss.passwordmanager.dao.UserDao;
import com.jquinss.passwordmanager.data.User;
import com.jquinss.passwordmanager.factories.DataSourceFactory;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class DatabaseManager {
    private static final String CREATE_USER_TABLE_STATEMENT = """
            CREATE TABLE IF NOT EXISTS user (user_id INTEGER PRIMARY KEY, user_name TEXT UNIQUE NOT NULL,
            password BLOB NOT NULL, password_salt BLOB NOT NULL, public_key BLOB NOT NULL, private_key BLOB NOT NULL, 
            private_key_iv BLOB NOT NULL);""";
    private static final String CREATE_FOLDER_TABLE_STATEMENT = """
            CREATE TABLE IF NOT EXISTS folder (folder_id INTEGER PRIMARY KEY, parent_folder_id INTEGER, 
            folder_name TEXT NOT NULL, description TEXT);""";
    private static final String CREATE_PASSWORD_ENTITY_TABLE_STATEMENT = """
            CREATE TABLE IF NOT EXISTS password_entity (password_entity_id INTEGER PRIMARY KEY, name TEXT NOT NULL, user_name TEXT,
            password TEXT NOT NULL, email_address TEXT, URL TEXT, description TEXT, expires INTEGER NOT NULL DEFAULT 0,
            expiration_date TEXT, user_id INTEGER NOT NULL, folder_id INTEGER NOT NULL,
            password_policy_id INTEGER NOT NULL, FOREIGN KEY(user_id) REFERENCES user(user_id),
            FOREIGN KEY(folder_id) REFERENCES folder(folder_id), FOREIGN KEY(password_policy_id) REFERENCES password_policy(password_policy_id));""";
    private static final String CREATE_PASSWORD_POLICY_STATEMENT = """
            CREATE TABLE IF NOT EXISTS password_policy (password_policy_id INTEGER PRIMARY KEY, password_policy_name TEXT NOT NULL,
            min_length INTEGER NOT NULL, min_lower_case_chars INT NOT NULL, min_upper_case_chars INT NOT NULL,
            min_digits INT NOT NULL, min_symbols INT NOT NULL, max_consec_equal_chars INT NOT NULL);""";

    private static final String INSERT_ROOT_FOLDER_STATEMENT = "INSERT OR IGNORE INTO folder VALUES (0, null, 'root', 'Root folder');";
    private static final String INITIALIZE_PWD_POLICY_STATEMENT = "INSERT OR IGNORE INTO password_policy VALUES (0, 'default', 10, 3, 3, 3, 3, 2)";

    private static final String databaseURL = "jdbc:sqlite:" + SettingsManager.getInstance().getDatabasePath();
    private final DataSource dataSource = initializeDataSource();
    private final UserDao userDao = new DbUserDao(dataSource);
    private static final DatabaseManager databaseManager = new DatabaseManager();

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        return databaseManager;
    }

    public Optional<User> getUserByName(String name) throws SQLException {
        return userDao.getByName(name);
    }

    public void addUser(User user) throws SQLException {
        userDao.add(user);
    }

    public void initializeDatabase() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null) {
                conn.setAutoCommit(false);
                Statement stmt = conn.createStatement();
                stmt.execute(CREATE_USER_TABLE_STATEMENT);
                stmt.execute(CREATE_FOLDER_TABLE_STATEMENT);
                stmt.execute(CREATE_FOLDER_TABLE_STATEMENT);
                stmt.execute(CREATE_PASSWORD_ENTITY_TABLE_STATEMENT);
                stmt.execute(CREATE_PASSWORD_POLICY_STATEMENT);
                stmt.execute(INSERT_ROOT_FOLDER_STATEMENT);
                stmt.execute(INITIALIZE_PWD_POLICY_STATEMENT);
                conn.commit();
                conn.setAutoCommit(true);
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
