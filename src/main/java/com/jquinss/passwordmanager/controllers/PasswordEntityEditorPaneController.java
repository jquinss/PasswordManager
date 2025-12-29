package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.dao.VaultRepository;
import com.jquinss.passwordmanager.data.*;
import com.jquinss.passwordmanager.enums.DataEntityEditorMode;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import com.jquinss.passwordmanager.util.misc.FixedLengthFilter;
import com.jquinss.passwordmanager.util.password.*;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.Duration;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.Validator;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.ResourceBundle;

public class PasswordEntityEditorPaneController implements Initializable {
    @FXML
    private ImageView passwordExpirationImageView;
    @FXML
    private Button hidePasswordEditorPaneButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button generatePasswordButton;
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
    private final PasswordManagerPaneController passwordManagerPaneController;
    private final VaultRepository vaultRepository;
    private final Validator validator = new Validator();
    private final ObservableList<PasswordEnforcementPolicy> passwordEnforcementPolicyObsList = FXCollections.observableArrayList();
    private final ObservableList<PasswordGeneratorPolicy> passwordGeneratorPolicyObsList = FXCollections.observableArrayList();
    private final PasswordStrengthChecker passwordStrengthChecker = new PasswordStrengthChecker();
    private PasswordGenerator passwordGenerator;

    public PasswordEntityEditorPaneController(PasswordManagerPaneController passwordManagerPaneController,
                                              VaultRepository vaultRepository) {
        this.passwordManagerPaneController = passwordManagerPaneController;
        this.vaultRepository = vaultRepository;
    }

    @FXML
    private void save() {
        switch (editorMode) {
            case CREATE -> createPasswordEntity();
            case EDIT -> editPasswordEntity();
        }

        closePasswordEditor();
    }

    @FXML
    private void cancel() {
        passwordManagerPaneController.cancelEditMode();
        closePasswordEditor();
    }

    @FXML
    void closePasswordEditor() {
        setHideMode(true);
    }

    @FXML
    private void generatePassword() {
        PasswordGeneratorPolicy passwordGeneratorPolicy = passwordGeneratorPolicyComboBox.getSelectionModel().getSelectedItem();
        if (passwordGeneratorPolicy != null) {
            setPasswordGeneratorSpecs(passwordGeneratorPolicy);
            passwordField.setText(passwordGenerator.generatePassword());
        }
        else {
            Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error generating password",
                    "No password generator policies have been selected", Alert.AlertType.ERROR);
            alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
            alertDialog.showAndWait();
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
            case "copy-pwd-btn" -> copyTextControlInputToClipboard(passwordField);
            case "copy-username-btn" -> copyTextControlInputToClipboard(usernameTextField);
            case "copy-url-btn" -> copyTextControlInputToClipboard(urlTextField);
            case "copy-email-address-btn" -> copyTextControlInputToClipboard(emailAddressTextField);
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
            passwordEnforcementPolicyObsList.setAll(vaultRepository.getAllPasswordEnforcementPoliciesByUserProfileId(passwordManagerPaneController.getUserProfileSession().getCurrentUserProfileId()));
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
            passwordGeneratorPolicyObsList.setAll(vaultRepository.getAllPasswordGeneratorPoliciesByUserProfileId(passwordManagerPaneController.getUserProfileSession().getCurrentUserProfileId()));
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

    private void initializeValidator() {
        createRequiredTextFieldCheck(nameTextField);
        createRequiredTextFieldCheck(passwordField);
        createRequiredTextFieldCheck(clearPasswordField);
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

    private void initializeTextFormatters() {
        nameTextField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(50)));
        descriptionTextField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(100)));
        urlTextField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(200)));
        usernameTextField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(50)));
        emailAddressTextField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(100)));
        passwordField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(200)));
        clearPasswordField.setTextFormatter(new TextFormatter<String>(new FixedLengthFilter(50)));
    }

    public void openPasswordEntityEditorInCreateMode(Folder folder) {
        setEditMode(DataEntityEditorMode.CREATE, folder);
        resetFields();
        initializePolicies();
        passwordExpirationImageView.setVisible(false);
        validator.clear();
        initializeValidator();
    }

    public void openPasswordEntityEditorInEditMode(PasswordEntity pwdEntity) {
        setEditMode(DataEntityEditorMode.EDIT, pwdEntity);
        resetFields();
        initializePolicies();
        loadPasswordEntity(pwdEntity);
        initializePasswordGenerator();
        passwordExpirationImageView.setVisible(isPasswordEntityExpired(pwdEntity));
        validator.clear();
        initializeValidator();
    }

    public void openPasswordEntityEditorInViewMode(PasswordEntity pwdEntity) {
        setViewMode(pwdEntity);
        resetFields();
        initializePolicies();
        loadPasswordEntity(pwdEntity);
        passwordExpirationImageView.setVisible(isPasswordEntityExpired(pwdEntity));
        validator.clear();
    }

    private void setViewMode(PasswordEntity pwdEntity) {
        editorMode = DataEntityEditorMode.VIEW;
        editorMode.setDataEntity(pwdEntity);
        setTextFieldsEditable(false);
        disableControls(true);
        dialogButtons.setVisible(false);
        //passwordEntityEditorMainPane.setVisible(true);
        showNode(passwordEntityEditorMainPane);
        hidePasswordEditorPaneButton.setVisible(true);
    }

    private void setEditMode(DataEntityEditorMode editorMode, DataEntity dataEntity) {
        this.editorMode = editorMode;
        this.editorMode.setDataEntity(dataEntity);
        setTextFieldsEditable(true);
        disableControls(false);
        dialogButtons.setVisible(true);
        // passwordEntityEditorMainPane.setVisible(true);
        showNode(passwordEntityEditorMainPane);
        hidePasswordEditorPaneButton.setVisible(false);
    }

    @FXML
    private void setHideMode(boolean addFadeOutEffect) {
        editorMode = DataEntityEditorMode.HIDE;
        editorMode.setDataEntity(null);
        validator.clear();
        hideNode(passwordEntityEditorMainPane, addFadeOutEffect);
    }

    private void showNode(Node node) {
        node.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void hideNode(Node node, boolean addFadeOutEffect) {
        if (addFadeOutEffect) {
            hideNodeWithFadeOutEffect(node);
        }
        else {
            node.setVisible(false);
        }
    }

    private void hideNodeWithFadeOutEffect(Node node) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> node.setVisible(false));
        fadeOut.play();
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
        emailAddressTextField.setText(pwdEntity.getEmailAddress());
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
        showPasswordCheckBox.setSelected(false);
        enforcePolicyCheckBox.setSelected(false);
        resetPasswordEnforcementPolicies();
        resetPasswordGeneratorPolicies();
        passwordExpiresCheckBox.setSelected(false);
        passwordExpirationDatePicker.setValue(null);
    }

    private boolean isPasswordEntityExpired(PasswordEntity passwordEntity) {
        return passwordEntity.isPasswordExpires() && !passwordEntity.getExpirationDate().isAfter(LocalDate.now());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializePasswordEnforcementPolicyComboBox();
        initializePasswordGeneratorPolicyComboBox();
        initializeShowPasswordCheckBox();
        initializeTextFormatters();
        setHideMode(false);
    }
}
