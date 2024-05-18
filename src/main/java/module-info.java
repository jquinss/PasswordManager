module com.jquinss.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires net.synedra.validatorfx;
    requires org.xerial.sqlitejdbc;
    requires org.bouncycastle.provider;


    opens com.jquinss.passwordmanager.app to javafx.fxml;
    opens com.jquinss.passwordmanager.controllers to javafx.fxml;
    opens com.jquinss.passwordmanager.control to javafx.fxml;

    exports com.jquinss.passwordmanager.app;
    exports com.jquinss.passwordmanager.controllers;
}