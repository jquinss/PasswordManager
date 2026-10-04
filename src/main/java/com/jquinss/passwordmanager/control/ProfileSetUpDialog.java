package com.jquinss.passwordmanager.control;

import com.jquinss.passwordmanager.controllers.ProfileSetUpPaneController;
import com.jquinss.passwordmanager.data.UserProfile;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

public class ProfileSetUpDialog extends Dialog<UserProfile> {
    public ProfileSetUpDialog(Window window) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/jquinss/passwordmanager/fxml/ProfileSetUpPane.fxml"));
            DialogPane dialogPane = loader.load();

            ProfileSetUpPaneController controller = loader.getController();
            setDialogPane(dialogPane);

            initOwner(window);
            setTitle("Create a profile");
            initModality(Modality.APPLICATION_MODAL);
            setResizable(false);
            setResultConverter(buttonType -> {
                if (!Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                    return null;
                }

                return controller.createUserProfile();
            });
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
