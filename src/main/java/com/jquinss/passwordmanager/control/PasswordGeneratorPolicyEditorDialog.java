package com.jquinss.passwordmanager.control;

import com.jquinss.passwordmanager.data.PasswordGeneratorPolicy;
import com.jquinss.passwordmanager.enums.PasswordPolicyEditorMode;
import com.jquinss.passwordmanager.util.misc.IntRangeStringConverter;
import com.jquinss.passwordmanager.util.password.PasswordSpecs;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.Validator;

import java.io.IOException;
import java.util.Objects;

public class PasswordGeneratorPolicyEditorDialog extends Dialog<PasswordGeneratorPolicy> {
    @FXML
    private Label passwordGeneratorPolicyEditorTitle;
    @FXML
    private TextField policyNameTextField;
    @FXML
    private TextField numSymbolsTextField;
    @FXML
    private TextField numDigitsTextField;
    @FXML
    private TextField numUpperCaseCharsTextField;
    @FXML
    private TextField numLowerCaseCharsTextField;
    @FXML
    private CheckBox isDefaultPolicyCheckBox;
    @FXML
    private ButtonType saveButtonType;
    private final PasswordPolicyEditorMode passwordPolicyEditorMode;
    private final Validator validator = new Validator();
    private PasswordGeneratorPolicy passwordGeneratorPolicy;

    public PasswordGeneratorPolicyEditorDialog(Window window, PasswordPolicyEditorMode passwordPolicyEditorMode) {
        this.passwordPolicyEditorMode = passwordPolicyEditorMode;

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/jquinss/passwordmanager/fxml/PasswordGeneratorPolicyEditorPane.fxml"));
            loader.setController(this);

            DialogPane dialogPane = loader.load();
            dialogPane.lookupButton(saveButtonType).disableProperty().bind(validator.containsErrorsProperty());
            initOwner(window);
            setTitle();
            initModality(Modality.APPLICATION_MODAL);
            setResizable(false);
            setDialogPane(dialogPane);
            setResultConverter(buttonType -> {
                if (!Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                    return null;
                }

                return getPasswordGeneratorPolicy();
            });
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PasswordGeneratorPolicyEditorDialog(Window window, PasswordPolicyEditorMode passwordPolicyEditorMode,
                                               PasswordGeneratorPolicy passwordGeneratorPolicy) {
        this(window, passwordPolicyEditorMode);
        this.passwordGeneratorPolicy = passwordGeneratorPolicy;
        loadPasswordGeneratorPolicy(this.passwordGeneratorPolicy);
    }

    private PasswordGeneratorPolicy getPasswordGeneratorPolicy() {
        switch (passwordPolicyEditorMode) {
            case CREATE -> createPasswordGeneratorPolicy();
            case EDIT -> modifyPasswordGeneratorPolicy();
        }
        return this.passwordGeneratorPolicy;
    }

    private void createPasswordGeneratorPolicy() {
        passwordGeneratorPolicy = new PasswordGeneratorPolicy(policyNameTextField.getText(), buildPasswordSpecs());
        passwordGeneratorPolicy.setDefaultPolicy(isDefaultPolicyCheckBox.isSelected());
    }

    private PasswordSpecs buildPasswordSpecs() {

        return new PasswordSpecs.Builder().numSymbols(Integer.parseInt(numSymbolsTextField.getText()))
                .numDigits(Integer.parseInt(numDigitsTextField.getText()))
                .numUppercaseChars(Integer.parseInt(numUpperCaseCharsTextField.getText()))
                .numLowerCaseChars(Integer.parseInt(numLowerCaseCharsTextField.getText())).build();
    }

    private void modifyPasswordGeneratorPolicy() {
        PasswordGeneratorPolicy passwordGeneratorPolicy = new PasswordGeneratorPolicy(policyNameTextField.getText(), buildPasswordSpecs());
        passwordGeneratorPolicy.setDefaultPolicy(isDefaultPolicyCheckBox.isSelected());
        passwordGeneratorPolicy.setId(this.passwordGeneratorPolicy.getId());
        this.passwordGeneratorPolicy = passwordGeneratorPolicy;
    }

    private void setTitle() {
        switch (passwordPolicyEditorMode) {
            case EDIT ->  {
                this.setTitle("Edit password generator policy");
                passwordGeneratorPolicyEditorTitle.setText("Edit password generator policy:");}
            case CREATE -> {
                this.setTitle("Create a password generator policy");
                passwordGeneratorPolicyEditorTitle.setText("Create password generator policy:");
            }
        }
    }

    @FXML
    public void initialize() {
        setTextFieldsFormatters();
        initializeValidator();
    }

    private void setTextFieldsFormatters() {
        numSymbolsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 20), 2));
        numDigitsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 20), 2));
        numUpperCaseCharsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 20), 2));
        numLowerCaseCharsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(2, 20), 2));
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

    private void loadPasswordGeneratorPolicy(PasswordGeneratorPolicy passwordGeneratorPolicy) {
        policyNameTextField.setText(passwordGeneratorPolicy.getName());
        PasswordSpecs passwordSpecs = passwordGeneratorPolicy.getPasswordSpecs();
        numSymbolsTextField.setText(Integer.toString(passwordSpecs.getNumSymbols()));
        numDigitsTextField.setText(Integer.toString(passwordSpecs.getNumDigits()));
        numUpperCaseCharsTextField.setText(Integer.toString(passwordSpecs.getNumUpperCaseChars()));
        numLowerCaseCharsTextField.setText(Integer.toString(passwordSpecs.getNumLowerCaseChars()));
        isDefaultPolicyCheckBox.setSelected(passwordGeneratorPolicy.isDefaultPolicy());
    }
}
