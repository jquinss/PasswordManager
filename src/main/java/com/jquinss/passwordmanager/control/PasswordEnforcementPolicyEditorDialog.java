package com.jquinss.passwordmanager.control;

import com.jquinss.passwordmanager.data.PasswordEnforcementPolicy;
import com.jquinss.passwordmanager.util.misc.IntRangeStringConverter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;

public class PasswordEnforcementPolicyEditorDialog extends Dialog<PasswordEnforcementPolicy> {
    @FXML
    private TextField policyNameTextField;
    @FXML
    private TextField minLengthTextField;
    @FXML
    private TextField maxConsecutiveEqualCharsTextField;
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

    public PasswordEnforcementPolicyEditorDialog(Window window) {
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

    @FXML
    public void initialize() {
        setTextFieldsFormatters();
    }

    private void setTextFieldsFormatters() {
        minLengthTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(1, 100), 6));
        maxConsecutiveEqualCharsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 50), 20));
        minSymbolsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 20), 2));
        minDigitsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 20), 2));
        minUpperCaseCharsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 20), 2));
        minLowerCaseCharsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 20), 2));
    }
}
