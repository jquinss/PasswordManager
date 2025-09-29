package com.jquinss.passwordmanager.security;

import com.jquinss.passwordmanager.data.UserProfile;

import java.util.HashMap;

public class UserProfileSession {
    private final HashMap<String, String> sessionVariables = new HashMap<>();

    public void setVariable(String name, String value) {
        sessionVariables.put(name, value);
    }

    public String getVariable(String name) {
        return sessionVariables.get(name);
    }

    public void initiate(UserProfile userProfile) {
        sessionVariables.put("currentUser", userProfile.getName());
        sessionVariables.put("currentUserProfileId", String.valueOf(userProfile.getId()));
    }

    public void terminate() {
        sessionVariables.clear();
    }

    public String getCurrentUserProfileName() {
        return sessionVariables.get("currentUserProfileName");
    }

    public int getCurrentUserProfileId() {
        return Integer.parseInt(sessionVariables.get("currentUserProfileId"));
    }
}
