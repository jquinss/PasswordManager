package com.jquinss.passwordmanager.data;

import java.io.Serializable;
import java.util.Optional;

public abstract class DataEntity implements Cloneable, Serializable {
    private int id;
    private String name;
    private String description;

    public DataEntity(int id, String name) {
        this(name);
        this.id = id;
    }

    public DataEntity(String name) {
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
            DataEntity dataEntity = (DataEntity) super.clone();
            dataEntity.setId(this.getId());
            dataEntity.setName(this.getName());
            dataEntity.setDescription(this.getDescription());
            return dataEntity;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
