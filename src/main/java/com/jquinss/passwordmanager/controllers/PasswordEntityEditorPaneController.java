package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.*;
import com.jquinss.passwordmanager.enums.EditorMode;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.util.password.PasswordGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    private PasswordField passwordField;
    @FXML
    private ComboBox<PasswordPolicy> passwordPolicyComboBox;
    @FXML
    private CheckBox passwordExpiresCheckBox;
    @FXML
    private DatePicker passwordExpirationDatePicker;
    @FXML
    private CheckBox showPasswordCheckBox;
    private EditorMode editorMode;
    private PasswordManagerPaneController passwordManagerPaneController;
    private final Validator validator = new Validator();
    private final ObservableList<PasswordPolicy> passwordPolicyObsList = FXCollections.observableArrayList();
    private final ObservableList<PasswordGeneratorPolicy> passwordGeneratorPolicyObsList = FXCollections.observableArrayList();
    private PasswordGenerator passwordGenerator;

    @FXML
    private void save() {
        // TODO
        switch (editorMode) {
            case CREATE -> createPasswordEntity();
        }

        setHideMode();
    }

    @FXML
    private void cancel() {
        setHideMode();
    }

    @FXML
    private void generatePassword() {
        passwordField.setText(passwordGenerator.generatePassword());
    }

    @FXML
    private void copyToClipboard(ActionEvent event) {
        // TODO
    }

    private void createPasswordEntity() {
        editorMode.getDataEntity().ifPresent(entity -> {
            Folder folder = (Folder) entity;
            PasswordEntity pwdEntity = new PasswordEntity(entity.getId(), nameTextField.getText(), passwordField.getText());
            pwdEntity.setDescription(descriptionTextField.getText());
            pwdEntity.setUsername(usernameTextField.getText());
            pwdEntity.setUrl(urlTextField.getText());
            pwdEntity.setPasswordExpires(passwordExpiresCheckBox.isSelected());
            if (passwordExpiresCheckBox.isSelected()) {
                pwdEntity.setExpirationDate(passwordExpirationDatePicker.getValue());
            }

            passwordManagerPaneController.addPasswordEntityToTreeView(pwdEntity, folder);
        });


    }

    private void initializePasswordPolicyComboBox() {
        setPasswordPolicyComboBoxCellFactory();
        passwordPolicyComboBox.setItems(passwordPolicyObsList);
        loadPasswordPolicies();
        setDefaultPasswordPolicy();;
    }

    private void setPasswordPolicyComboBoxCellFactory() {
        passwordPolicyComboBox.setCellFactory(new Callback<ListView<PasswordPolicy>, ListCell<PasswordPolicy>>() {
            @Override
            public ListCell<PasswordPolicy> call(ListView<PasswordPolicy> param) {
                return new ListCell<PasswordPolicy>() {
                    @Override
                    protected void updateItem(PasswordPolicy item, boolean empty) {
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

    private void loadPasswordPolicies() {
        try {
            passwordPolicyObsList.setAll(DatabaseManager.getInstance().getAllPasswordPolicies());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        };
    }

    private void setDefaultPasswordPolicy() {
        for (PasswordPolicy pwdPolicy : passwordPolicyObsList) {
            if (pwdPolicy.isDefaultPolicy()) {
                passwordPolicyComboBox.getSelectionModel().select(pwdPolicy);
            }
        }
    }

    private void initializePasswordGeneratorPolicyComboBox() {
        setPasswordGeneratorPolicyComboBoxCellFactory();
        passwordGeneratorPolicyComboBox.setItems(passwordGeneratorPolicyObsList);
        loadPasswordGeneratorPolicies();
        setDefaultPasswordGeneratorPolicy();;
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

    private void loadPasswordGeneratorPolicies() {
        try {
            passwordGeneratorPolicyObsList.setAll(DatabaseManager.getInstance().getAllPasswordGeneratorPolicies());
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

    private void initializePasswordGenerator() {
        passwordGenerator = new PasswordGenerator(passwordGeneratorPolicyComboBox.getValue().getPasswordSpecs());
    }

    private void initializeValidator() {
        createRequiredTextFieldCheck(nameTextField);
        createRequiredTextFieldCheck(passwordField);
        createDateCheck();
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

    private boolean validateDate(String date, String pattern) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern)).isAfter(LocalDate.now());
        }
        catch (DateTimeParseException e) {
            return false;
        }
    }

    public void openPasswordEntityEditorInCreateMode(Folder folder) {
        setEditMode(EditorMode.CREATE, folder);
        resetFields();
    }

    public void openPasswordEntityEditorInEditMode(PasswordEntity pwdEntity) {
        setEditMode(EditorMode.EDIT, pwdEntity);
        resetFields();
        loadPasswordEntity(pwdEntity);
    }

    public void openPasswordEntityEditorInViewMode(PasswordEntity pwdEntity) {
        setViewMode(pwdEntity);
        resetFields();
        loadPasswordEntity(pwdEntity);
    }

    private void setViewMode(PasswordEntity pwdEntity) {
        editorMode = EditorMode.VIEW;
        editorMode.setDataEntity(pwdEntity);
        setTextFieldsEditable(false);
        disableControls(true);
        dialogButtons.setVisible(false);
        passwordEntityEditorMainPane.setVisible(true);
    }

    private void setEditMode(EditorMode editorMode, DataEntity dataEntity) {
        this.editorMode = editorMode;
        this.editorMode.setDataEntity(dataEntity);
        setTextFieldsEditable(true);
        disableControls(false);
        dialogButtons.setVisible(true);
        passwordEntityEditorMainPane.setVisible(true);
    }

    private void setHideMode() {
        editorMode = EditorMode.HIDE;
        editorMode.setDataEntity(null);
        passwordEntityEditorMainPane.setVisible(false);
    }

    private void setTextFieldsEditable(boolean editable) {
        nameTextField.setEditable(editable);
        descriptionTextField.setEditable(editable);
        urlTextField.setEditable(editable);
        usernameTextField.setEditable(editable);
        passwordField.setEditable(editable);
    }

    private void disableControls(boolean disable) {
        passwordPolicyComboBox.setDisable(disable);
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
        setPasswordPolicy(pwdEntity);
        passwordExpiresCheckBox.setSelected(pwdEntity.isPasswordExpires());
        passwordExpirationDatePicker.setValue(pwdEntity.getExpirationDate());
    }

    private void setPasswordPolicy(PasswordEntity pwdEntty) {
        for (PasswordPolicy pwdPolicy : passwordPolicyObsList) {
            if (pwdPolicy.getId() == pwdEntty.getPasswordPolicyId()) {
                passwordPolicyComboBox.getSelectionModel().select(pwdPolicy);
            }
        }
    }

    private void resetFields() {
        nameTextField.clear();
        descriptionTextField.clear();
        urlTextField.clear();
        usernameTextField.clear();
        passwordField.clear();
        setDefaultPasswordPolicy();
        passwordExpiresCheckBox.setSelected(false);
        passwordExpirationDatePicker.setValue(null);
    }

    void setPasswordManagerPaneController(PasswordManagerPaneController passwordManagerPaneController) {
        this.passwordManagerPaneController = passwordManagerPaneController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeValidator();
        initializePasswordPolicyComboBox();
        initializePasswordGeneratorPolicyComboBox();
        initializePasswordGenerator();
        setHideMode();
    }
}
