package com.jquinss.passwordmanager.managers;

import com.jquinss.passwordmanager.dao.*;
import com.jquinss.passwordmanager.data.*;
import com.jquinss.passwordmanager.factories.DataSourceFactory;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class DatabaseManager {
    private static final String CREATE_USER_PROFILE_TABLE_STATEMENT = """
            CREATE TABLE IF NOT EXISTS user_profile (user_profile_id INTEGER PRIMARY KEY, user_profile_name TEXT UNIQUE NOT NULL,
            default_profile INTEGER NOT NULL DEFAULT 0, password BLOB NOT NULL, password_salt BLOB NOT NULL, public_key BLOB NOT NULL, 
            private_key BLOB NOT NULL, private_key_iv BLOB NOT NULL);""";
    private static final String CREATE_ROOT_FOLDER_TABLE_STATEMENT = """
            CREATE TABLE IF NOT EXISTS root_folder (root_folder_id INTEGER PRIMARY KEY, root_folder_name TEXT NOT NULL, user_profile_id INTEGER NOT NULL, 
            FOREIGN KEY(user_profile_id) REFERENCES user_profile(user_profile_id))""";
    private static final String CREATE_FOLDER_TABLE_STATEMENT = """
            CREATE TABLE IF NOT EXISTS folder (folder_id INTEGER PRIMARY KEY, parent_folder_id INTEGER, 
            folder_name TEXT NOT NULL, description TEXT);""";
    private static final String CREATE_PWD_ENTITY_TABLE_STATEMENT = """
            CREATE TABLE IF NOT EXISTS password_entity (password_entity_id INTEGER PRIMARY KEY, name TEXT NOT NULL, user_name TEXT,
            password TEXT NOT NULL, email_address TEXT, URL TEXT, description TEXT, expires INTEGER NOT NULL DEFAULT 0,
            expiration_date TEXT, user_profile_id INTEGER NOT NULL, folder_id INTEGER NOT NULL, password_enf_policy_enabled INTEGER NOT NULL DEFAULT 0,
            password_enf_policy_id INTEGER NOT NULL, FOREIGN KEY(user_profile_id) REFERENCES user_profile(user_profile_id),
            FOREIGN KEY(folder_id) REFERENCES folder(folder_id), FOREIGN KEY(password_enf_policy_id) REFERENCES password_enf_policy(password_enf_policy_id));""";
    private static final String CREATE_PWD_ENFORCEMENT_POLICY_TABLE_STATEMENT = """
            CREATE TABLE IF NOT EXISTS password_enf_policy (password_enf_policy_id INTEGER PRIMARY KEY, password_enf_policy_name TEXT NOT NULL,
            min_length INTEGER NOT NULL, min_lower_case_chars INT NOT NULL, min_upper_case_chars INT NOT NULL,
            min_digits INT NOT NULL, min_symbols INT NOT NULL, max_consec_equal_chars INT NOT NULL, default_policy INTEGER NOT NULL DEFAULT 0, 
            user_profile_id INTEGER NOT NULL, FOREIGN KEY(user_profile_id) REFERENCES user_profile(user_profile_id));""";

    private static final String CREATE_PWD_GENERATOR_POLICY_TABLE_STATEMENT = """
            CREATE TABLE IF NOT EXISTS password_gen_policy (password_gen_policy_id INTEGER PRIMARY KEY, password_gen_policy_name TEXT NOT NULL,
            lower_case_chars INT NOT NULL, upper_case_chars INT NOT NULL, digits INT NOT NULL, symbols INT NOT NULL, 
            default_policy INTEGER NOT NULL DEFAULT 0, user_profile_id INTEGER NOT NULL, FOREIGN KEY(user_profile_id) REFERENCES user_profile(user_profile_id));
            """;
    //private static final String INIT_PWD_ENF_POLICY_TABLE_STATEMENT = "INSERT OR IGNORE INTO password_enf_policy VALUES (0, 'default', 10, 3, 3, 3, 3, 2, 1)";
    //private static final String INIT_PWD_GEN_POLICY_TABLE_STATEMENT = "INSERT OR IGNORE INTO password_gen_policy VALUES (0, 'default', 3, 3, 3, 3, 1)";

    private static final String databaseURL = "jdbc:sqlite:" + SettingsManager.getInstance().getDatabasePath();
    private final DataSource dataSource = initializeDataSource();
    private final UserProfileDao userProfileDao = new DbUserProfileDao(dataSource);
    private final PasswordEntityDao passwordEntityDao = new DbPasswordEntityDao(dataSource);
    private final FolderDao folderDao = new DbFolderDao(dataSource);
    private final PasswordEnforcementPolicyDao passwordEnforcementPolicyDao = new DbPasswordEnforcementPolicyDao(dataSource);
    private final PasswordGeneratorPolicyDao passwordGeneratorPolicyDao = new DbPasswordGeneratorPolicyDao(dataSource);
    private static final DatabaseManager databaseManager = new DatabaseManager();

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        return databaseManager;
    }

    public Optional<UserProfile> getUserProfileByName(String name) throws SQLException {
        return userProfileDao.getByName(name);
    }

    public void addUserProfile(UserProfile userProfile) throws SQLException {
        userProfileDao.add(userProfile);
    }

    public void updateUserProfile(UserProfile userProfile) throws SQLException {
        userProfileDao.update(userProfile);
    }

    public void deleteUserProfile(UserProfile userProfile) throws SQLException {
        userProfileDao.delete(userProfile);
    }

    public List<String> getAllUserProfileNames() throws SQLException {
        return userProfileDao.getAllUserProfileNames();
    }

    public Optional<Folder> getFolderById(int id) throws SQLException {
        return folderDao.getById(id);
    }

    public List<Folder> getAllFoldersByParentFolderId(int parentId) throws SQLException {
        return folderDao.getAllByParentId(parentId);
    }

    public void addRootFolder(RootFolder folder) throws SQLException {
        folderDao.addRoot(folder);
    }

    public void addFolder(Folder folder) throws SQLException {
        folderDao.add(folder);
    }

    public Optional<RootFolder> getRootFolderByUserProfileId(int userProfileId) throws SQLException {
        return folderDao.getRootByUserProfileId(userProfileId);
    }

    public void updateFolder(Folder folder) throws SQLException {
        folderDao.update(folder);
    }

    public void deleteFolder(Folder folder) throws SQLException {
        folderDao.delete(folder);
    }

    public void deleteFolders(List<Folder> folders) throws SQLException {
        folderDao.delete(folders);
    }

    public void deleteRootFolder(RootFolder folder) throws SQLException {
        folderDao.deleteRoot(folder);
    }

    public void addPasswordEntity(PasswordEntity passwordEntity) throws SQLException {
        passwordEntityDao.add(passwordEntity);
    }

    public void deletePasswordEntity(PasswordEntity passwordEntity) throws SQLException {
        passwordEntityDao.delete(passwordEntity);
    }

    public void updatePasswordEntity(PasswordEntity passwordEntity) throws SQLException {
        passwordEntityDao.update(passwordEntity);
    }

    public List<PasswordEntity> getAllPasswordEntitiesByFolderId(int folderId) throws SQLException {
        return passwordEntityDao.getAllByFolderId(folderId);
    }

    public List<PasswordEntity> getAllPasswordEntitiesByPasswordEnforcementPolicyId(int policyId) throws SQLException {
        return passwordEntityDao.getAllByPasswordEnforcementPolicyId(policyId);
    }

    public List<PasswordEntity> getAllPasswordEntitiesByUserProfileId(int userProfileId) throws SQLException {
        return passwordEntityDao.getAllByUserProfileId(userProfileId);
    }

    public void deletePasswordEntities(List<PasswordEntity> pwdEntities) throws SQLException {
        passwordEntityDao.delete(pwdEntities);
    }

    public void addPasswordEnforcementPolicy(PasswordEnforcementPolicy passwordEnforcementPolicy) throws SQLException {
        passwordEnforcementPolicyDao.add(passwordEnforcementPolicy);
    }

    public void updatePasswordEnforcementPolicy(PasswordEnforcementPolicy passwordEnforcementPolicy) throws SQLException {
        passwordEnforcementPolicyDao.update(passwordEnforcementPolicy);
    }

    public void deletePasswordEnforcementPolicy(PasswordEnforcementPolicy passwordEnforcementPolicy) throws SQLException {
        passwordEnforcementPolicyDao.delete(passwordEnforcementPolicy);
    }

    public void deletePasswordEnforcementPolicies(List<PasswordEnforcementPolicy> passwordEnforcementPolicies) throws SQLException {
        passwordEnforcementPolicyDao.delete(passwordEnforcementPolicies);
    }

    public List<PasswordEnforcementPolicy> getAllPasswordEnforcementPoliciesByUserProfileId(int userProfileId) throws SQLException {
        return passwordEnforcementPolicyDao.getAllByUserProfileId(userProfileId);
    }

    public void addPasswordGeneratorPolicy(PasswordGeneratorPolicy passwordGeneratorPolicy) throws SQLException {
        passwordGeneratorPolicyDao.add(passwordGeneratorPolicy);
    }

    public void updatePasswordGeneratorPolicy(PasswordGeneratorPolicy passwordGeneratorPolicy) throws SQLException {
        passwordGeneratorPolicyDao.update(passwordGeneratorPolicy);
    }

    public void deletePasswordGeneratorPolicy(PasswordGeneratorPolicy passwordGeneratorPolicy) throws SQLException {
        passwordGeneratorPolicyDao.delete(passwordGeneratorPolicy);
    }

    public void deletePasswordGeneratorPolicies(List<PasswordGeneratorPolicy> passwordGeneratorPolicies) throws SQLException {
        passwordGeneratorPolicyDao.delete(passwordGeneratorPolicies);
    }

    public List<PasswordGeneratorPolicy> getAllPasswordGeneratorPoliciesByUserProfileId(int userProfileId) throws SQLException {
        return passwordGeneratorPolicyDao.getAllByUserProfileId(userProfileId);
    }

    public void initializeDatabase() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null) {
                conn.setAutoCommit(false);
                Statement stmt = conn.createStatement();
                stmt.execute(CREATE_USER_PROFILE_TABLE_STATEMENT);
                stmt.execute(CREATE_ROOT_FOLDER_TABLE_STATEMENT);
                stmt.execute(CREATE_FOLDER_TABLE_STATEMENT);
                stmt.execute(CREATE_PWD_ENTITY_TABLE_STATEMENT);
                stmt.execute(CREATE_PWD_ENFORCEMENT_POLICY_TABLE_STATEMENT);
                stmt.execute(CREATE_PWD_GENERATOR_POLICY_TABLE_STATEMENT);
                conn.commit();
                conn.setAutoCommit(true);
            }
        }
    }

    private DataSource initializeDataSource() {
        SQLiteDataSource dataSource = (SQLiteDataSource) DataSourceFactory.getDataSource("SQLITE");
        dataSource.setUrl(databaseURL);
        return dataSource;
    }
}
