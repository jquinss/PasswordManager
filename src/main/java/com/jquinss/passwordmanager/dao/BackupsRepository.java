package com.jquinss.passwordmanager.dao;

import javax.sql.DataSource;

public class BackupsRepository {
    private final DataSource dataSource;
    public BackupsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void initialize() {
        // TODO
    }
}
