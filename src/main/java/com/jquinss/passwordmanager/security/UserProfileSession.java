package com.jquinss.passwordmanager.security;

import com.jquinss.passwordmanager.data.User;

import java.util.HashMap;

public class UserSession {
    private final HashMap<String, String> sessionVariables = new HashMap<>();

    public void setVariable(String name, String value) {
        sessionVariables.put(name, value);
    }

    public String getVariable(String name) {
        return sessionVariables.get(name);
    }

    public void initiate(User user) {
        sessionVariables.put("currentUser", user.getName());
        sessionVariables.put("currentUserId", String.valueOf(user.getId()));
    }

    public void terminate() {
        sessionVariables.clear();
    }

    public String getCurrentUser() {
        return sessionVariables.get("currentUser");
    }

    public int getCurrentUserId() {
        return Integer.parseInt(sessionVariables.get("currentUserId"));
    }
}
