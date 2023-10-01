module com.jquinss.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.jquinss.passwordmanager.app to javafx.fxml;
    opens com.jquinss.passwordmanager.controllers to javafx.fxml;

    exports com.jquinss.passwordmanager.app;
    exports com.jquinss.passwordmanager.controllers;
}