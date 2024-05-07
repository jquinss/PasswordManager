package com.jquinss.passwordmanager.data;

public class Folder extends DataEntity implements Cloneable {
    private int parentFolderId;

    public Folder(int id, String name) {
        super(id, name);
    }

    public Folder(String name) {
        super(name);
    }

    public int getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(int parentFolderId) {
        this.parentFolderId = parentFolderId;
    }


    @Override
    public Folder clone() {
        Folder folder = (Folder) super.clone();
        folder.setParentFolderId(this.parentFolderId);
        return folder;
    }
}
