package com.jquinss.passwordmanager.data;

import java.io.Serializable;

public abstract class VaultItem implements Cloneable, Serializable {
    private int id;
    private String name;
    private String description;

    public VaultItem(int id, String name) {
        this(name);
        this.id = id;
    }

    public VaultItem(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null || description.isEmpty() ? null : description;
    }

    @Override
    public Object clone() {
        try {
            VaultItem vaultItem = (VaultItem) super.clone();
            vaultItem.setId(this.getId());
            vaultItem.setName(this.getName());
            vaultItem.setDescription(this.getDescription());
            return vaultItem;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
