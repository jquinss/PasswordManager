package com.jquinss.passwordmanager.security;

import java.util.HashMap;

public class UserSession {
    private final HashMap<String, String> sessionVariables = new HashMap<>();

    public void setVariable(String name, String value) {
        sessionVariables.put(name, value);
    }

    public String getVariable(String name) {
        return sessionVariables.get(name);
    }

    public void initiate(String username) {
        sessionVariables.put("currentUser", username);
    }

    public void terminate() {
        sessionVariables.clear();
    }

    public String getCurrentUser() {
        return sessionVariables.get("currentUser");
    }
}
