package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.control.PasswordEnforcementPolicyEditorDialog;
import com.jquinss.passwordmanager.control.PasswordGeneratorPolicyEditorDialog;
import com.jquinss.passwordmanager.data.PasswordEntity;
import com.jquinss.passwordmanager.data.PasswordGeneratorPolicy;
import com.jquinss.passwordmanager.data.PasswordEnforcementPolicy;
import com.jquinss.passwordmanager.enums.PasswordPolicyEditorMode;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class PasswordPoliciesPaneController {
    @FXML
    private TableView<PasswordEnforcementPolicy> passwordEnforcementPoliciesTableView;
    @FXML
    private TableColumn<PasswordEnforcementPolicy, String> passwordEnforcementPolicyNameTableColumn;
    @FXML
    private TableColumn<PasswordEnforcementPolicy, String> passwordEnforcementIsDefaultPolicyTableColumn;
    @FXML
    private TableView<PasswordGeneratorPolicy> passwordGeneratorPoliciesTableView;
    @FXML
    private TableColumn<PasswordGeneratorPolicy, String> passwordGeneratorPolicyNameTableColumn;
    @FXML
    private TableColumn<PasswordGeneratorPolicy, String> passwordGeneratorIsDefaultPolicyTableColumn;
    private Stage stage;

    private final ObservableList<PasswordEnforcementPolicy> passwordEnforcementPolicyObsList = FXCollections.observableArrayList();
    private final ObservableList<PasswordGeneratorPolicy> passwordGeneratorPolicyObsList = FXCollections.observableArrayList();
    private PasswordEnforcementPolicy defaultPasswordEnforcementPolicy;
    private PasswordGeneratorPolicy defaultPasswordGeneratorPolicy;

    @FXML
    private void addPasswordEnforcementPolicy() {
        PasswordEnforcementPolicyEditorDialog dialog = new PasswordEnforcementPolicyEditorDialog(stage, PasswordPolicyEditorMode.CREATE);
        dialog.showAndWait().ifPresent(passwordEnforcementPolicy -> {
            try {
                DatabaseManager.getInstance().addPasswordEnforcementPolicy(passwordEnforcementPolicy);
                passwordEnforcementPolicyObsList.add(passwordEnforcementPolicy);

                if (passwordEnforcementPolicy.isDefaultPolicy()) {
                    swapDefaultPasswordEnforcementPolicy(passwordEnforcementPolicy);
                }

                passwordEnforcementPoliciesTableView.getSelectionModel().select(passwordEnforcementPolicy);
            }
            catch (SQLException e) {
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error creating policy",
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css")).toString());
                alertDialog.showAndWait();
            }
        });
    }

    @FXML
    private void removePasswordEnforcementPolicy() {
        PasswordEnforcementPolicy pwdEnforcementPolicy = passwordEnforcementPoliciesTableView.getSelectionModel().getSelectedItem();

        if (pwdEnforcementPolicy != null) {
            if (isPasswordEnforcementPolicyInUse(pwdEnforcementPolicy.getId())) {
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Cannot remove policy",
                        "The policy is in use", Alert.AlertType.ERROR);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css")).toString());
                alertDialog.showAndWait();
            }
            else {
                try {
                    DatabaseManager.getInstance().deletePasswordEnforcementPolicy(pwdEnforcementPolicy);
                    passwordEnforcementPolicyObsList.remove(pwdEnforcementPolicy);

                    if (pwdEnforcementPolicy.isDefaultPolicy()) {
                        defaultPasswordEnforcementPolicy = null;
                    }
                }
                catch (SQLException e) {
                    Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error removing policy",
                            "A database error has occurred during the operation", Alert.AlertType.ERROR);
                    alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css")).toString());
                    alertDialog.showAndWait();
                }
            }
        }

    }

    @FXML
    private void editPasswordEnforcementPolicy() {
        PasswordEnforcementPolicy origPasswordEnforcementPolicy = passwordEnforcementPoliciesTableView.getSelectionModel().getSelectedItem();

        if (origPasswordEnforcementPolicy != null) {
            PasswordEnforcementPolicyEditorDialog dialog = new PasswordEnforcementPolicyEditorDialog(stage,
                    PasswordPolicyEditorMode.EDIT, origPasswordEnforcementPolicy);

            dialog.showAndWait().ifPresent(newPasswordEnforcementPolicy -> {
                try {
                    DatabaseManager.getInstance().updatePasswordEnforcementPolicy(newPasswordEnforcementPolicy);

                    if (newPasswordEnforcementPolicy.isDefaultPolicy()) {
                        swapDefaultPasswordEnforcementPolicy(newPasswordEnforcementPolicy);
                    }

                    replacePasswordEnforcementPolicy(origPasswordEnforcementPolicy, newPasswordEnforcementPolicy);
                    passwordEnforcementPoliciesTableView.getSelectionModel().select(newPasswordEnforcementPolicy);
                }
                catch (SQLException e) {
                    Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error editing policy",
                            "A database error has occurred during the operation", Alert.AlertType.ERROR);
                    alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css")).toString());
                    alertDialog.showAndWait();
                }
            });
        }
    }

    @FXML
    private void addPasswordGeneratorPolicy() {
        // TODO
        PasswordGeneratorPolicyEditorDialog dialog = new PasswordGeneratorPolicyEditorDialog(stage, PasswordPolicyEditorMode.CREATE);
        dialog.showAndWait().ifPresent(passwordGeneratorPolicy -> {
            try {
                DatabaseManager.getInstance().addPasswordGeneratorPolicy(passwordGeneratorPolicy);
                passwordGeneratorPolicyObsList.add(passwordGeneratorPolicy);

                if (passwordGeneratorPolicy.isDefaultPolicy()) {
                    swapDefaultPasswordGeneratorPolicy(passwordGeneratorPolicy);
                }

                passwordGeneratorPoliciesTableView.getSelectionModel().select(passwordGeneratorPolicy);
            }
            catch (SQLException e) {
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error creating policy",
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/application.css")).toString());
                alertDialog.showAndWait();
            }
        });
    }

    @FXML
    private void removePasswordGeneratorPolicy() {
        // TODO
    }

    @FXML
    private void editPasswordGeneratorPolicy() {
        // TODO
    }

    private boolean isPasswordEnforcementPolicyInUse(int policyId) {
        try {
            List<PasswordEntity> passwordEntities = DatabaseManager.getInstance().getAllPasswordEntitiesByPasswordEnforcementPolicyId(policyId);
            return !passwordEntities.isEmpty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void swapDefaultPasswordEnforcementPolicy(PasswordEnforcementPolicy newPasswordEnforcementPolicy) throws SQLException {
        if (defaultPasswordEnforcementPolicy != null) {
            defaultPasswordEnforcementPolicy.setDefaultPolicy(false);
            DatabaseManager.getInstance().updatePasswordEnforcementPolicy(defaultPasswordEnforcementPolicy);
        }
        defaultPasswordEnforcementPolicy = newPasswordEnforcementPolicy;
        passwordEnforcementPoliciesTableView.refresh();
    }

    private void swapDefaultPasswordGeneratorPolicy(PasswordGeneratorPolicy newPasswordGeneratorPolicy) throws SQLException {
        if (defaultPasswordGeneratorPolicy != null) {
            defaultPasswordGeneratorPolicy.setDefaultPolicy(false);
            DatabaseManager.getInstance().updatePasswordGeneratorPolicy(defaultPasswordGeneratorPolicy);
        }
        defaultPasswordGeneratorPolicy = newPasswordGeneratorPolicy;
        passwordGeneratorPoliciesTableView.refresh();
    }

    private void replacePasswordEnforcementPolicy(PasswordEnforcementPolicy oldPasswordEnforcementPolicy,
                                                  PasswordEnforcementPolicy newPasswordEnforcementPolicy) {
        int index = passwordEnforcementPolicyObsList.indexOf(oldPasswordEnforcementPolicy);
        passwordEnforcementPolicyObsList.remove(oldPasswordEnforcementPolicy);
        passwordEnforcementPolicyObsList.add(index, newPasswordEnforcementPolicy);
    }

    @FXML
    private void initialize() {
        // TODO
        initializePasswordEnforcementPoliciesTableView();
        initializePasswordGeneratorPoliciesTableView();
        loadPasswordEnforcementPolicies();
        loadPasswordGeneratorPolicies();
    }

    private void initializePasswordEnforcementPoliciesTableView() {
        passwordEnforcementPolicyNameTableColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getName());
        });

        passwordEnforcementIsDefaultPolicyTableColumn.setCellValueFactory(cellData -> {
            boolean state = cellData.getValue().isDefaultPolicy();
            return state ? new SimpleStringProperty("Yes") : new SimpleStringProperty("No");
        });

        passwordEnforcementPoliciesTableView.setItems(passwordEnforcementPolicyObsList);
    }

    private void initializePasswordGeneratorPoliciesTableView() {
        passwordGeneratorPolicyNameTableColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getName());
        });

        passwordGeneratorIsDefaultPolicyTableColumn.setCellValueFactory(cellData -> {
            boolean state = cellData.getValue().isDefaultPolicy();
            return state ? new SimpleStringProperty("Yes") : new SimpleStringProperty("No");
        });

        passwordGeneratorPoliciesTableView.setItems(passwordGeneratorPolicyObsList);
    }

    private void loadPasswordEnforcementPolicies() {
        try {
            List<PasswordEnforcementPolicy> passwordEnforcementPolicies = DatabaseManager.getInstance().getAllPasswordEnforcementPolicies();
            for (PasswordEnforcementPolicy pwdEnforcementPolicy : passwordEnforcementPolicies) {
                passwordEnforcementPolicyObsList.add(pwdEnforcementPolicy);
                if (pwdEnforcementPolicy.isDefaultPolicy()) {
                    defaultPasswordEnforcementPolicy = pwdEnforcementPolicy;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadPasswordGeneratorPolicies() {
        try {
            List<PasswordGeneratorPolicy> passwordGeneratorPolicies = DatabaseManager.getInstance().getAllPasswordGeneratorPolicies();
            passwordGeneratorPolicyObsList.setAll(passwordGeneratorPolicies);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }
}
