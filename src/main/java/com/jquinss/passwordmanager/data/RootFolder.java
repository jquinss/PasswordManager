package com.jquinss.passwordmanager.data;

public class RootFolder extends Folder {
    private int userId;

    public RootFolder(String name, int userId) {
        super(name);
        this.userId = userId;
    }
    public RootFolder(int id, String name, int userId) {
        super(id, name);
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
