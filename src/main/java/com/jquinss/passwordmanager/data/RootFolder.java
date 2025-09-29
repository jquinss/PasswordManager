package com.jquinss.passwordmanager.data;

public class RootFolder extends Folder {
    private int userProfileId;

    public RootFolder(String name, int userProfileId) {
        super(name);
        this.userProfileId = userProfileId;
    }
    public RootFolder(int id, String name, int userProfileId) {
        super(id, name);
        this.userProfileId = userProfileId;
    }

    public int getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(int userProfileId) {
        this.userProfileId = userProfileId;
    }
}
