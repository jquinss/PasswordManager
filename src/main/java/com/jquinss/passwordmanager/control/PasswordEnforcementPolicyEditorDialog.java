package com.jquinss.passwordmanager.control;

import com.jquinss.passwordmanager.data.PasswordEnforcementPolicy;
import com.jquinss.passwordmanager.enums.PasswordPolicyEditorMode;
import com.jquinss.passwordmanager.util.misc.IntRangeStringConverter;
import com.jquinss.passwordmanager.util.password.PasswordStrengthCriteria;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.Validator;

import java.io.IOException;
import java.util.Objects;

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
    private final PasswordPolicyEditorMode passwordPolicyEditorMode;
    private final Validator validator = new Validator();
    private PasswordEnforcementPolicy passwordEnforcementPolicy;

    public PasswordEnforcementPolicyEditorDialog(Window window, PasswordPolicyEditorMode passwordPolicyEditorMode) {
        this.passwordPolicyEditorMode = passwordPolicyEditorMode;

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/jquinss/passwordmanager/fxml/PasswordEnforcementPolicyEditorPane.fxml"));
            loader.setController(this);

            DialogPane dialogPane = loader.load();
            dialogPane.lookupButton(saveButtonType).disableProperty().bind(validator.containsErrorsProperty());
            initOwner(window);
            initModality(Modality.APPLICATION_MODAL);
            setResizable(false);
            setDialogPane(dialogPane);
            setResultConverter(buttonType -> {
                if (!Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                    return null;
                }

                return getPasswordEnforcementPolicy();
            });
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PasswordEnforcementPolicyEditorDialog(Window window, PasswordPolicyEditorMode passwordPolicyEditorMode,
                                                 PasswordEnforcementPolicy passwordEnforcementPolicy) {
        this(window, passwordPolicyEditorMode);
        this.passwordEnforcementPolicy = passwordEnforcementPolicy;
        loadPasswordEnforcementPolicy(this.passwordEnforcementPolicy);
    }

    private PasswordEnforcementPolicy getPasswordEnforcementPolicy() {
        switch (passwordPolicyEditorMode) {
            case CREATE -> createPasswordEnforcementPolicy();
            case EDIT -> modifyPasswordEnforcementPolicy();
        }
        return this.passwordEnforcementPolicy;
    }

    private void createPasswordEnforcementPolicy() {
        passwordEnforcementPolicy = new PasswordEnforcementPolicy(policyNameTextField.getText(), buildPasswordStrengthCriteria());
        passwordEnforcementPolicy.setDefaultPolicy(isDefaultPolicyCheckBox.isSelected());
    }

    private PasswordStrengthCriteria buildPasswordStrengthCriteria() {

        return new PasswordStrengthCriteria.Builder().minLength(Integer.parseInt(minLengthTextField.getText()))
                .maxConsecutiveChars(Integer.parseInt(maxConsecutiveEqualCharsTextField.getText()))
                .minSymbols(Integer.parseInt(minSymbolsTextField.getText()))
                .minDigits(Integer.parseInt(minDigitsTextField.getText()))
                .minUppercaseChars(Integer.parseInt(minUpperCaseCharsTextField.getText()))
                .minLowerCaseChars(Integer.parseInt(minLowerCaseCharsTextField.getText())).build();
    }

    private void modifyPasswordEnforcementPolicy() {
        PasswordEnforcementPolicy passwordEnforcementPolicy = new PasswordEnforcementPolicy(policyNameTextField.getText(), buildPasswordStrengthCriteria());
        passwordEnforcementPolicy.setDefaultPolicy(isDefaultPolicyCheckBox.isSelected());
        passwordEnforcementPolicy.setId(this.passwordEnforcementPolicy.getId());
        this.passwordEnforcementPolicy = passwordEnforcementPolicy;
    }

    @FXML
    public void initialize() {
        setTextFieldsFormatters();
        initializeValidator();
    }

    private void setTextFieldsFormatters() {
        minLengthTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(1, 100), 6));
        maxConsecutiveEqualCharsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 50), 20));
        minSymbolsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 20), 2));
        minDigitsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 20), 2));
        minUpperCaseCharsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 20), 2));
        minLowerCaseCharsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 20), 2));
    }

    private void initializeValidator() {
        createRequiredTextFieldsCheck();
    }

    private void createRequiredTextFieldsCheck() {
        createRequiredTextFieldCheck(policyNameTextField);
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

    private void loadPasswordEnforcementPolicy(PasswordEnforcementPolicy passwordEnforcementPolicy) {
        policyNameTextField.setText(passwordEnforcementPolicy.getName());
        PasswordStrengthCriteria passwordStrengthCriteria = passwordEnforcementPolicy.getPasswordStrengthCriteria();
        minLengthTextField.setText(Integer.toString(passwordStrengthCriteria.getMinLength()));
        maxConsecutiveEqualCharsTextField.setText(Integer.toString(passwordStrengthCriteria.getMaxConsecutiveEqualChars()));
        minSymbolsTextField.setText(Integer.toString(passwordStrengthCriteria.getMinSymbols()));
        minDigitsTextField.setText(Integer.toString(passwordStrengthCriteria.getMinDigits()));
        minUpperCaseCharsTextField.setText(Integer.toString(passwordStrengthCriteria.getMinUpperCaseChars()));
        minLowerCaseCharsTextField.setText(Integer.toString(passwordStrengthCriteria.getMinLowerCaseChars()));
        isDefaultPolicyCheckBox.setSelected(passwordEnforcementPolicy.isDefaultPolicy());
    }
}
