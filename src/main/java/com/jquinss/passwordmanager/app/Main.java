package com.jquinss.passwordmanager.app;

import com.jquinss.passwordmanager.controllers.PasswordManagerController;
import com.jquinss.passwordmanager.dao.BackupsRepository;
import com.jquinss.passwordmanager.dao.VaultRepository;
import com.jquinss.passwordmanager.enums.DataSourceType;
import com.jquinss.passwordmanager.factories.DataSourceFactory;
import com.jquinss.passwordmanager.util.misc.OSChecker;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.nio.file.Path;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) {
        try {
            DataSource vaultDataSource = DataSourceFactory.getDataSource(DataSourceType.SQLITE,
                    Path.of(OSChecker.getOSDataDirectory(), "PasswordManager",
                    "data", "vault.db").toString());
            DataSource backupsDataSource = DataSourceFactory.getDataSource(DataSourceType.SQLITE,
                    Path.of(OSChecker.getOSDataDirectory(), "PasswordManager",
                    "data", "backups.db").toString());

            VaultRepository vaultRepository = new VaultRepository(vaultDataSource);
            BackupsRepository backupsRepository = new BackupsRepository(backupsDataSource);

            AppContext appContext = new AppContext(vaultRepository, backupsRepository);

            PasswordManagerController controller = new PasswordManagerController(primaryStage, appContext);

            controller.loadMainMenuPane();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
