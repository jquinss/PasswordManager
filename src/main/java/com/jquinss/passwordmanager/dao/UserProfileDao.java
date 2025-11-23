package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.UserProfile;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserProfileDao {
    Optional<UserProfile> getByName(String name) throws SQLException;

    void add(UserProfile userProfile) throws SQLException;

    void update(UserProfile userProfile) throws SQLException;

    void delete(UserProfile userProfile) throws SQLException;

    List<UserProfile> getAllUserProfiles() throws SQLException;

    Optional<UserProfile> getDefaultUserProfile() throws SQLException;
}
