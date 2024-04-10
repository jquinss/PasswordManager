package com.jquinss.passwordmanager.data;

import java.util.Optional;

public abstract class DataEntity {
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

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public void setDescription(String description) {
        this.description = description.isEmpty() ? null : description;
    }
}
