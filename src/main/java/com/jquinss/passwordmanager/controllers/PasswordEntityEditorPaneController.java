package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.*;
import com.jquinss.passwordmanager.enums.DataEntityEditorMode;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import com.jquinss.passwordmanager.util.password.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.Validator;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class PasswordEntityEditorPaneController implements Initializable {
    @FXML
    public Button hidePasswordEditorPaneButton;
    @FXML
    private Button copyUrlButton;
    @FXML
    private Button copyUsernameButton;
    @FXML
    private Button copyEmailAddressButton;
    @FXML
    private Button copyPasswordButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button generatePasswordButton;
    @FXML
    private ImageView generatePasswordImage;
    @FXML
    private HBox dialogButtons;
    @FXML
    private ComboBox<PasswordGeneratorPolicy> passwordGeneratorPolicyComboBox;
    @FXML
    private ScrollPane passwordEntityEditorMainPane;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField urlTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField emailAddressTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField clearPasswordField;
    @FXML
    private CheckBox enforcePolicyCheckBox;
    @FXML
    private ComboBox<PasswordEnforcementPolicy> passwordEnforcementPolicyComboBox;
    @FXML
    private CheckBox passwordExpiresCheckBox;
    @FXML
    private DatePicker passwordExpirationDatePicker;
    @FXML
    private CheckBox showPasswordCheckBox;
    private DataEntityEditorMode editorMode;
    private PasswordManagerPaneController passwordManagerPaneController;
    private final Validator validator = new Validator();
    private final ObservableList<PasswordEnforcementPolicy> passwordEnforcementPolicyObsList = FXCollections.observableArrayList();
    private final ObservableList<PasswordGeneratorPolicy> passwordGeneratorPolicyObsList = FXCollections.observableArrayList();
    private final PasswordStrengthChecker passwordStrengthChecker = new PasswordStrengthChecker();
    private PasswordGenerator passwordGenerator;

    @FXML
    private void save() {
        switch (editorMode) {
            case CREATE -> createPasswordEntity();
            case EDIT -> editPasswordEntity();
        }

        setHideMode();
    }

    @FXML
    private void cancel() {
        passwordManagerPaneController.cancelEditMode();
        setHideMode();
    }

    @FXML
    private void generatePassword() {
        PasswordGeneratorPolicy passwordGeneratorPolicy = passwordGeneratorPolicyComboBox.getSelectionModel().getSelectedItem();
        if (passwordGeneratorPolicy != null) {
            setPasswordGeneratorSpecs(passwordGeneratorPolicy);
            passwordField.setText(passwordGenerator.generatePassword());
        }
        else {
            DialogBuilder.buildAlertDialog("Error", "Error generator password",
                    "No password policies have been selected", Alert.AlertType.ERROR).showAndWait();
        }

    }

    private void setPasswordGeneratorSpecs(PasswordGeneratorPolicy passwordGeneratorPolicy) {
        if (passwordGenerator == null) {
            passwordGenerator = new PasswordGenerator(passwordGeneratorPolicy.getPasswordSpecs());
        }
        else {
            passwordGenerator.setPasswordSpecs(passwordGeneratorPolicy.getPasswordSpecs());
        }
    }

    @FXML
    private void copyToClipboard(ActionEvent event) {
        String buttonId = ((Control) event.getSource()).getId();
        switch (buttonId) {
            case "copyPasswordButton" -> copyTextControlInputToClipboard(passwordField);
            case "copyUsernameButton" -> copyTextControlInputToClipboard(usernameTextField);
            case "copyUrlButton" -> copyTextControlInputToClipboard(urlTextField);
            case "copyEmailAddressButton" -> copyTextControlInputToClipboard(emailAddressTextField);
        }
    }

    private void copyTextControlInputToClipboard(TextField textField) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(textField.getText());
        clipboard.setContent(content);
    }

    private void createPasswordEntity() {
        editorMode.getDataEntity().ifPresent(entity -> {
            PasswordEntity pwdEntity = new PasswordEntity(entity.getId(), nameTextField.getText(), passwordField.getText());
            pwdEntity.setDescription(descriptionTextField.getText());
            pwdEntity.setUsername(usernameTextField.getText());
            pwdEntity.setUrl(urlTextField.getText());
            pwdEntity.setEmailAddress(emailAddressTextField.getText());
            pwdEntity.setPasswordEnforcementPolicyEnabled(enforcePolicyCheckBox.isSelected());
            if (enforcePolicyCheckBox.isSelected()) {
                pwdEntity.setPasswordEnforcementPolicyId(passwordEnforcementPolicyComboBox.getValue().getId());
            }

            pwdEntity.setPasswordExpires(passwordExpiresCheckBox.isSelected());
            if (passwordExpiresCheckBox.isSelected()) {
                pwdEntity.setExpirationDate(passwordExpirationDatePicker.getValue());
            }

            passwordManagerPaneController.savePasswordEntity(pwdEntity);
        });
    }

    private void editPasswordEntity() {
        editorMode.getDataEntity().ifPresent(entity -> {
            PasswordEntity pwdEntity = (PasswordEntity) entity;
            pwdEntity.setName(nameTextField.getText());
            pwdEntity.setPassword(passwordField.getText());
            pwdEntity.setDescription(descriptionTextField.getText());
            pwdEntity.setUsername(usernameTextField.getText());
            pwdEntity.setUrl(urlTextField.getText());
            pwdEntity.setEmailAddress(emailAddressTextField.getText());
            pwdEntity.setPasswordEnforcementPolicyEnabled(enforcePolicyCheckBox.isSelected());
            if (enforcePolicyCheckBox.isSelected()) {
                pwdEntity.setPasswordEnforcementPolicyId(passwordEnforcementPolicyComboBox.getValue().getId());
            }

            pwdEntity.setPasswordExpires(passwordExpiresCheckBox.isSelected());
            if (passwordExpiresCheckBox.isSelected()) {
                pwdEntity.setExpirationDate(passwordExpirationDatePicker.getValue());
            }

            passwordManagerPaneController.savePasswordEntity(pwdEntity);
        });
    }

    private void initializePasswordEnforcementPolicyComboBox() {
        setPasswordEnforcementPolicyComboBoxCellFactory();
        passwordEnforcementPolicyComboBox.setItems(passwordEnforcementPolicyObsList);
    }

    private void setPasswordEnforcementPolicyComboBoxCellFactory() {
        passwordEnforcementPolicyComboBox.setCellFactory(new Callback<ListView<PasswordEnforcementPolicy>, ListCell<PasswordEnforcementPolicy>>() {
            @Override
            public ListCell<PasswordEnforcementPolicy> call(ListView<PasswordEnforcementPolicy> param) {
                return new ListCell<PasswordEnforcementPolicy>() {
                    @Override
                    protected void updateItem(PasswordEnforcementPolicy item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            setText(item.toString());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
    }

    private void initializePolicies() {
        initializePasswordEnforcementPolicies();
        initializePasswordGeneratorPolicies();
    }

    private void initializePasswordEnforcementPolicies() {
        loadPasswordEnforcementPolicies();
        setDefaultPasswordEnforcementPolicy();
    }

    private void loadPasswordEnforcementPolicies() {
        try {
            passwordEnforcementPolicyObsList.setAll(DatabaseManager.getInstance().getAllPasswordEnforcementPoliciesByUserId(passwordManagerPaneController.getUserSession().getCurrentUserId()));
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        };
    }

    private void setDefaultPasswordEnforcementPolicy() {
        for (PasswordEnforcementPolicy pwdPolicy : passwordEnforcementPolicyObsList) {
            if (pwdPolicy.isDefaultPolicy()) {
                passwordEnforcementPolicyComboBox.getSelectionModel().select(pwdPolicy);
            }
        }
    }

    private void resetPasswordEnforcementPolicies() {
        passwordEnforcementPolicyObsList.clear();
    }

    private void initializePasswordGeneratorPolicyComboBox() {
        setPasswordGeneratorPolicyComboBoxCellFactory();
        passwordGeneratorPolicyComboBox.setItems(passwordGeneratorPolicyObsList);
    }

    private void setPasswordGeneratorPolicyComboBoxCellFactory() {
        passwordGeneratorPolicyComboBox.setCellFactory(new Callback<ListView<PasswordGeneratorPolicy>, ListCell<PasswordGeneratorPolicy>>() {
            @Override
            public ListCell<PasswordGeneratorPolicy> call(ListView<PasswordGeneratorPolicy> param) {
                return new ListCell<PasswordGeneratorPolicy>() {
                    @Override
                    protected void updateItem(PasswordGeneratorPolicy item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            setText(item.toString());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
    }

    private void initializePasswordGeneratorPolicies() {
        loadPasswordGeneratorPolicies();
        setDefaultPasswordGeneratorPolicy();
    }

    private void loadPasswordGeneratorPolicies() {
        try {
            passwordGeneratorPolicyObsList.setAll(DatabaseManager.getInstance().getAllPasswordGeneratorPoliciesByUserId(passwordManagerPaneController.getUserSession().getCurrentUserId()));
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        };
    }

    private void setDefaultPasswordGeneratorPolicy() {
        for (PasswordGeneratorPolicy pwdGenPolicy : passwordGeneratorPolicyObsList) {
            if (pwdGenPolicy.isDefaultPolicy()) {
                passwordGeneratorPolicyComboBox.getSelectionModel().select(pwdGenPolicy);
            }
        }
    }

    private void resetPasswordGeneratorPolicies() {
        passwordGeneratorPolicyObsList.clear();
    }

    private void initializePasswordGenerator() {
        PasswordGeneratorPolicy passwordGeneratorPolicy = passwordGeneratorPolicyComboBox.getSelectionModel().getSelectedItem();
        if (passwordGeneratorPolicy != null) {
            passwordGenerator = new PasswordGenerator(passwordGeneratorPolicy.getPasswordSpecs());
        }
    }

    private void setTooltips() {
        Tooltip.install(generatePasswordButton, new Tooltip("Generate password"));
        Tooltip.install(copyPasswordButton, new Tooltip("Copy password to clipboard"));
        Tooltip.install(copyUrlButton, new Tooltip("Copy URL to clipboard"));
        Tooltip.install(copyUsernameButton, new Tooltip("Copy username to clipboard"));
        Tooltip.install(copyEmailAddressButton, new Tooltip("Copy email address to clipboard"));
    }

    private void initializeValidator() {
        createRequiredTextFieldCheck(nameTextField);
        createRequiredTextFieldCheck(passwordField);
        createDateCheck();
        createPasswordEnforcementCheck();
        saveButton.disableProperty().bind(validator.containsErrorsProperty());
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
        if (text == null || text.isEmpty()) {
            context.error("This field is required");
        }
    }

    private void createDateCheck() {
        validator.createCheck().withMethod(c -> {
            boolean isPasswordExpiresCheckBoxSelected = c.get("passwordExpires");
            String passwordExpirationDate = c.get("passwordExpirationDate");
            if (isPasswordExpiresCheckBoxSelected &&
                    !validateDate(passwordExpirationDate, "M/d/yyyy")) {
                c.error("You must enter a valid future date");
            }
        }).dependsOn("passwordExpires", passwordExpiresCheckBox.selectedProperty())
                .dependsOn("passwordExpirationDate", passwordExpirationDatePicker.getEditor().textProperty())
                .decorates(passwordExpirationDatePicker.getEditor())
                .immediate();
    }

    private void createPasswordEnforcementCheck() {
        validator.createCheck().withMethod(c -> {
            if (c.get("enforcePolicyCheckBox")) {
                if (!passwordEnforcementPolicyObsList.isEmpty() &&
                        !passwordEnforcementPolicyComboBox.getSelectionModel().isEmpty()) {
                    PasswordStrengthCriteria pwdStrengthCriteria =
                            passwordEnforcementPolicyComboBox.getSelectionModel().getSelectedItem().getPasswordStrengthCriteria();
                    if (!passwordStrengthChecker.passwordMeetsStrengthCriteria(new Password(c.get("password")), pwdStrengthCriteria)) {
                        c.error("Password does not meet the policy enforcement criteria:\n" + pwdStrengthCriteria);
                    }
                }
                else if (passwordEnforcementPolicyObsList.isEmpty()) {
                    c.error("No password enforcement policies have been created");
                } else if (passwordEnforcementPolicyComboBox.getSelectionModel().isEmpty()) {
                    c.error("No password enforcement policies have been selected");
                }
            }
        }).dependsOn("password", passwordField.textProperty())
                .dependsOn("enforcePolicyCheckBox", enforcePolicyCheckBox.selectedProperty())
                .decorates(passwordField)
                .decorates(clearPasswordField)
                .decorates(passwordEnforcementPolicyComboBox)
                .immediate();
    }

    private boolean validateDate(String date, String pattern) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern)).isAfter(LocalDate.now());
        }
        catch (DateTimeParseException e) {
            return false;
        }
    }

    private void initializeShowPasswordCheckBox() {
        passwordField.textProperty().bindBidirectional(clearPasswordField.textProperty());
        showPasswordCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                clearPasswordField.toFront();
                clearPasswordField.setVisible(true);
                passwordField.setVisible(false);
            }
            else {
                passwordField.toFront();
                clearPasswordField.setVisible(false);
                passwordField.setVisible(true);
            }
        });
    }

    public void openPasswordEntityEditorInCreateMode(Folder folder) {
        setEditMode(DataEntityEditorMode.CREATE, folder);
        resetFields();
        initializePolicies();
        validator.clear();
        initializeValidator();
    }

    public void openPasswordEntityEditorInEditMode(PasswordEntity pwdEntity) {
        setEditMode(DataEntityEditorMode.EDIT, pwdEntity);
        resetFields();
        initializePolicies();
        loadPasswordEntity(pwdEntity);
        initializePasswordGenerator();
        validator.clear();
        initializeValidator();
    }

    public void openPasswordEntityEditorInViewMode(PasswordEntity pwdEntity) {
        setViewMode(pwdEntity);
        resetFields();
        initializePolicies();
        loadPasswordEntity(pwdEntity);
        validator.clear();
    }

    private void setViewMode(PasswordEntity pwdEntity) {
        editorMode = DataEntityEditorMode.VIEW;
        editorMode.setDataEntity(pwdEntity);
        setTextFieldsEditable(false);
        disableControls(true);
        dialogButtons.setVisible(false);
        passwordEntityEditorMainPane.setVisible(true);
        hidePasswordEditorPaneButton.setVisible(true);
    }

    private void setEditMode(DataEntityEditorMode editorMode, DataEntity dataEntity) {
        this.editorMode = editorMode;
        this.editorMode.setDataEntity(dataEntity);
        setTextFieldsEditable(true);
        disableControls(false);
        dialogButtons.setVisible(true);
        passwordEntityEditorMainPane.setVisible(true);
        hidePasswordEditorPaneButton.setVisible(false);
    }

    @FXML
    private void setHideMode() {
        editorMode = DataEntityEditorMode.HIDE;
        editorMode.setDataEntity(null);
        validator.clear();
        passwordEntityEditorMainPane.setVisible(false);
    }

    private void setTextFieldsEditable(boolean editable) {
        nameTextField.setEditable(editable);
        descriptionTextField.setEditable(editable);
        urlTextField.setEditable(editable);
        usernameTextField.setEditable(editable);
        emailAddressTextField.setEditable(editable);
        passwordField.setEditable(editable);
        clearPasswordField.setEditable(editable);
    }

    private void disableControls(boolean disable) {
        enforcePolicyCheckBox.setDisable(disable);
        passwordEnforcementPolicyComboBox.setDisable(disable);
        passwordGeneratorPolicyComboBox.setDisable(disable);
        passwordExpiresCheckBox.setDisable(disable);
        passwordExpirationDatePicker.setDisable(disable);
        generatePasswordButton.setDisable(disable);
    }

    private void loadPasswordEntity(PasswordEntity pwdEntity) {
        nameTextField.setText(pwdEntity.getName());
        descriptionTextField.setText(pwdEntity.getDescription());
        urlTextField.setText(pwdEntity.getUrl());
        usernameTextField.setText(pwdEntity.getUsername());
        passwordField.setText(pwdEntity.getPassword());
        enforcePolicyCheckBox.setSelected(pwdEntity.isPasswordEnforcementPolicyEnabled());
        if (pwdEntity.isPasswordEnforcementPolicyEnabled()) {
            setPasswordEnforcementPolicy(pwdEntity);
        }

        passwordExpiresCheckBox.setSelected(pwdEntity.isPasswordExpires());
        passwordExpirationDatePicker.setValue(pwdEntity.getExpirationDate());
    }

    private void setPasswordEnforcementPolicy(PasswordEntity pwdEntity) {
        for (PasswordEnforcementPolicy pwdPolicy : passwordEnforcementPolicyObsList) {
            if (pwdPolicy.getId() == pwdEntity.getPasswordEnforcementPolicyId()) {
                passwordEnforcementPolicyComboBox.getSelectionModel().select(pwdPolicy);
            }
        }
    }

    private void resetFields() {
        nameTextField.clear();
        descriptionTextField.clear();
        urlTextField.clear();
        usernameTextField.clear();
        emailAddressTextField.clear();
        passwordField.clear();
        clearPasswordField.clear();
        enforcePolicyCheckBox.setSelected(false);
        resetPasswordEnforcementPolicies();
        resetPasswordGeneratorPolicies();
        passwordExpiresCheckBox.setSelected(false);
        passwordExpirationDatePicker.setValue(null);
    }

    void setPasswordManagerPaneController(PasswordManagerPaneController passwordManagerPaneController) {
        this.passwordManagerPaneController = passwordManagerPaneController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializePasswordEnforcementPolicyComboBox();
        initializePasswordGeneratorPolicyComboBox();
        initializeShowPasswordCheckBox();
        setTooltips();
        setHideMode();
    }
}
