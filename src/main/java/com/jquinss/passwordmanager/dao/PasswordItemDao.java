package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.PasswordItem;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PasswordItemDao {
    Optional<PasswordItem> getById(int id) throws SQLException;

    List<PasswordItem> getAllByUserProfileId(int userProfileId) throws SQLException;

    List<PasswordItem> getAllByFolderId(int folderId) throws SQLException;

    List<PasswordItem> getAllByPasswordEnforcementPolicyId(int passwordEnforcementPolicyId) throws SQLException;

    void add(PasswordItem pwdItem) throws SQLException;

    void update(PasswordItem pwdItem) throws SQLException;

    void delete(PasswordItem pwdItem) throws SQLException;

    void delete(List<PasswordItem> pwdItems) throws SQLException;
}
