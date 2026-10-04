module com.jquinss.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires net.synedra.validatorfx;
    requires org.bouncycastle.provider;
    requires io.github.willena.sqlitejdbc;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;


    opens com.jquinss.passwordmanager.app to javafx.fxml,  com.fasterxml.jackson.databind;
    opens com.jquinss.passwordmanager.vault.metadata to com.fasterxml.jackson.databind;
    opens com.jquinss.passwordmanager.controllers to javafx.fxml;
    opens com.jquinss.passwordmanager.control to javafx.fxml;

    exports com.jquinss.passwordmanager.app;
    exports com.jquinss.passwordmanager.controllers;
    exports com.jquinss.passwordmanager.dao;
    exports com.jquinss.passwordmanager.vault.repository;
    exports com.jquinss.passwordmanager.vault.metadata;
    exports com.jquinss.passwordmanager.crypto;
}