package com.jquinss.passwordmanager.control;

import com.jquinss.passwordmanager.data.PasswordEnforcementPolicy;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;

public class PasswordEnforcementEditorDialog extends Dialog<PasswordEnforcementPolicy> {
    @FXML
    private TextField policyNameTextField;
    @FXML
    private TextField minLengthTextField;
    @FXML
    private TextField minConsecCharsTextField;
    @FXML
    private TextField minSymbolsTextField;
    @FXML
    private TextField minDigitsTextField;
    @FXML
    private TextField minUpperCaseCharsTextField;
    @FXML
    private TextField minLowerCaseCharsTextField;
    @FXML
    private CheckBox isDefaultPolicyCheckBox;
    @FXML
    private ButtonType saveButtonType;

    public PasswordEnforcementEditorDialog(Window window) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/jquinss/passwordmanager/fxml/PasswordEnforcementPolicyEditorPane.fxml"));
            loader.setController(this);

            DialogPane dialogPane = loader.load();
            initOwner(window);
            initModality(Modality.APPLICATION_MODAL);
            setResizable(false);
            setDialogPane(dialogPane);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
