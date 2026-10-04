package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.UserProfile;
import com.jquinss.passwordmanager.util.misc.FixedLengthFilter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.Validator;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileSetUpPaneController implements Initializable {
    @FXML
    private DialogPane dialogPane;
    @FXML
    private TextField userProfileNameTextField;
    @FXML
    private CheckBox defaultProfileCheckBox;
    @FXML
    private Label message;
    @FXML
    private ButtonType saveButtonType;
    private final Validator validator = new Validator();


    public UserProfile createUserProfile() {
        return new UserProfile(userProfileNameTextField.getText(), defaultProfileCheckBox.isSelected());
    }

    private void initializeTextFormatters() {
        userProfileNameTextField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(50)));
    }

    private void initializeValidator() {
        createRequiredTextFieldsCheck();
        dialogPane.lookupButton(saveButtonType).disableProperty().bind(validator.containsErrorsProperty());
    }

    private void createRequiredTextFieldsCheck() {
        createRequiredTextFieldCheck(userProfileNameTextField);
    }

    private void createRequiredTextFieldCheck(TextField textField) {
        validator.createCheck()
                .withMethod(this::required)
                .dependsOn("text", textField.textProperty())
                .decorates(textField)
                .immediate();
    }

    private void required(Check.Context context) {
        String text = context.get("text");
        if (text == null || text.trim().isEmpty()) {
            context.error("This field is required");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTextFormatters();
        initializeValidator();
    }
}
