package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.control.PasswordEnforcementPolicyEditorDialog;
import com.jquinss.passwordmanager.control.PasswordGeneratorPolicyEditorDialog;
import com.jquinss.passwordmanager.dao.VaultRepository;
import com.jquinss.passwordmanager.data.PasswordEntity;
import com.jquinss.passwordmanager.data.PasswordGeneratorPolicy;
import com.jquinss.passwordmanager.data.PasswordEnforcementPolicy;
import com.jquinss.passwordmanager.enums.PasswordPolicyEditorMode;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
    private final ObservableList<PasswordEnforcementPolicy> passwordEnforcementPolicyObsList = FXCollections.observableArrayList();
    private final ObservableList<PasswordGeneratorPolicy> passwordGeneratorPolicyObsList = FXCollections.observableArrayList();
    private PasswordEnforcementPolicy defaultPasswordEnforcementPolicy;
    private PasswordGeneratorPolicy defaultPasswordGeneratorPolicy;
    private final PasswordManagerPaneController passwordManagerPaneController;
    private final VaultRepository vaultRepository;

    public PasswordPoliciesPaneController(PasswordManagerPaneController passwordManagerPaneController,
                                          VaultRepository vaultRepository) {
        this.passwordManagerPaneController = passwordManagerPaneController;
        this.vaultRepository = vaultRepository;
    }

    @FXML
    private void addPasswordEnforcementPolicy(ActionEvent actionEvent) {
        PasswordEnforcementPolicyEditorDialog dialog = new PasswordEnforcementPolicyEditorDialog(getStageFromActionEvent(actionEvent), PasswordPolicyEditorMode.CREATE);
        dialog.showAndWait().ifPresent(passwordEnforcementPolicy -> {
            try {
                passwordEnforcementPolicy.setUserProfileId(passwordManagerPaneController.getUserProfileSession().getCurrentUserProfileId());
                vaultRepository.addPasswordEnforcementPolicy(passwordEnforcementPolicy);
                passwordEnforcementPolicyObsList.add(passwordEnforcementPolicy);

                if (passwordEnforcementPolicy.isDefaultPolicy()) {
                    swapDefaultPasswordEnforcementPolicy(passwordEnforcementPolicy);
                }

                passwordEnforcementPoliciesTableView.getSelectionModel().select(passwordEnforcementPolicy);
            }
            catch (SQLException e) {
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error creating policy",
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
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
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                alertDialog.showAndWait();
            }
            else {
                try {
                    vaultRepository.deletePasswordEnforcementPolicy(pwdEnforcementPolicy);
                    passwordEnforcementPolicyObsList.remove(pwdEnforcementPolicy);

                    if (pwdEnforcementPolicy.isDefaultPolicy()) {
                        defaultPasswordEnforcementPolicy = null;
                    }
                }
                catch (SQLException e) {
                    Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error removing policy",
                            "A database error has occurred during the operation", Alert.AlertType.ERROR);
                    alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                    alertDialog.showAndWait();
                }
            }
        }
    }

    @FXML
    private void editPasswordEnforcementPolicy(ActionEvent actionEvent) {
        PasswordEnforcementPolicy origPasswordEnforcementPolicy = passwordEnforcementPoliciesTableView.getSelectionModel().getSelectedItem();

        if (origPasswordEnforcementPolicy != null) {
            PasswordEnforcementPolicyEditorDialog dialog = new PasswordEnforcementPolicyEditorDialog(getStageFromActionEvent(actionEvent),
                    PasswordPolicyEditorMode.EDIT, origPasswordEnforcementPolicy);

            dialog.showAndWait().ifPresent(newPasswordEnforcementPolicy -> {
                try {
                    vaultRepository.updatePasswordEnforcementPolicy(newPasswordEnforcementPolicy);

                    if (newPasswordEnforcementPolicy.isDefaultPolicy()) {
                        swapDefaultPasswordEnforcementPolicy(newPasswordEnforcementPolicy);
                    }

                    replacePasswordEnforcementPolicy(origPasswordEnforcementPolicy, newPasswordEnforcementPolicy);
                    passwordEnforcementPoliciesTableView.getSelectionModel().select(newPasswordEnforcementPolicy);
                }
                catch (SQLException e) {
                    Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error editing policy",
                            "A database error has occurred during the operation", Alert.AlertType.ERROR);
                    alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                    alertDialog.showAndWait();
                }
            });
        }
    }

    @FXML
    private void addPasswordGeneratorPolicy(ActionEvent actionEvent) {
        PasswordGeneratorPolicyEditorDialog dialog = new PasswordGeneratorPolicyEditorDialog(getStageFromActionEvent(actionEvent), PasswordPolicyEditorMode.CREATE);
        dialog.showAndWait().ifPresent(passwordGeneratorPolicy -> {
            try {
                passwordGeneratorPolicy.setUserProfileId(passwordManagerPaneController.getUserProfileSession().getCurrentUserProfileId());
                vaultRepository.addPasswordGeneratorPolicy(passwordGeneratorPolicy);
                passwordGeneratorPolicyObsList.add(passwordGeneratorPolicy);

                if (passwordGeneratorPolicy.isDefaultPolicy()) {
                    swapDefaultPasswordGeneratorPolicy(passwordGeneratorPolicy);
                }

                passwordGeneratorPoliciesTableView.getSelectionModel().select(passwordGeneratorPolicy);
            }
            catch (SQLException e) {
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error creating policy",
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/syles4.css")).toString());
                alertDialog.showAndWait();
            }
        });
    }

    @FXML
    private void removePasswordGeneratorPolicy() {
        PasswordGeneratorPolicy pwdGeneratorPolicy = passwordGeneratorPoliciesTableView.getSelectionModel().getSelectedItem();

        if (pwdGeneratorPolicy != null) {
            if (pwdGeneratorPolicy.isDefaultPolicy()) {
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error removing policy",
                        "Cannot remove the default policy", Alert.AlertType.ERROR);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                alertDialog.showAndWait();
            }
            else {
                try {
                    vaultRepository.deletePasswordGeneratorPolicy(pwdGeneratorPolicy);
                    passwordGeneratorPolicyObsList.remove(pwdGeneratorPolicy);

                    if (pwdGeneratorPolicy.isDefaultPolicy()) {
                        defaultPasswordEnforcementPolicy = null;
                    }
                }
                catch (SQLException e) {
                    Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error removing policy",
                            "A database error has occurred during the operation", Alert.AlertType.ERROR);
                    alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                    alertDialog.showAndWait();
                }
            }
        }
    }

    @FXML
    private void editPasswordGeneratorPolicy(ActionEvent actionEvent) {
        PasswordGeneratorPolicy origPasswordGeneratorPolicy = passwordGeneratorPoliciesTableView.getSelectionModel().getSelectedItem();

        if (origPasswordGeneratorPolicy != null) {
            PasswordGeneratorPolicyEditorDialog dialog = new PasswordGeneratorPolicyEditorDialog(getStageFromActionEvent(actionEvent),
                    PasswordPolicyEditorMode.EDIT, origPasswordGeneratorPolicy);

            dialog.showAndWait().ifPresent(newPasswordGeneratorPolicy -> {
                try {
                    vaultRepository.updatePasswordGeneratorPolicy(newPasswordGeneratorPolicy);

                    if (newPasswordGeneratorPolicy.isDefaultPolicy()) {
                        swapDefaultPasswordGeneratorPolicy(newPasswordGeneratorPolicy);
                    }

                    replacePasswordGeneratorPolicy(origPasswordGeneratorPolicy, newPasswordGeneratorPolicy);
                    passwordGeneratorPoliciesTableView.getSelectionModel().select(newPasswordGeneratorPolicy);
                }
                catch (SQLException e) {
                    Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Error editing policy",
                            "A database error has occurred during the operation", Alert.AlertType.ERROR);
                    alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                    alertDialog.showAndWait();
                }
            });
        }
    }

    private Stage getStageFromActionEvent(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        return (Stage) source.getScene().getWindow();
    }

    private boolean isPasswordEnforcementPolicyInUse(int policyId) {
        try {
            List<PasswordEntity> passwordEntities = vaultRepository.getAllPasswordEntitiesByPasswordEnforcementPolicyId(policyId);
            return !passwordEntities.isEmpty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void swapDefaultPasswordEnforcementPolicy(PasswordEnforcementPolicy newPasswordEnforcementPolicy) throws SQLException {
        if (defaultPasswordEnforcementPolicy != null) {
            defaultPasswordEnforcementPolicy.setDefaultPolicy(false);
            vaultRepository.updatePasswordEnforcementPolicy(defaultPasswordEnforcementPolicy);
        }
        defaultPasswordEnforcementPolicy = newPasswordEnforcementPolicy;
        passwordEnforcementPoliciesTableView.refresh();
    }

    private void swapDefaultPasswordGeneratorPolicy(PasswordGeneratorPolicy newPasswordGeneratorPolicy) throws SQLException {
        if (defaultPasswordGeneratorPolicy != null) {
            defaultPasswordGeneratorPolicy.setDefaultPolicy(false);
            vaultRepository.updatePasswordGeneratorPolicy(defaultPasswordGeneratorPolicy);
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

    private void replacePasswordGeneratorPolicy(PasswordGeneratorPolicy oldPasswordGeneratorPolicy,
                                                PasswordGeneratorPolicy newPasswordGeneratorPolicy) {
        int index = passwordGeneratorPolicyObsList.indexOf(oldPasswordGeneratorPolicy);
        passwordGeneratorPolicyObsList.remove(oldPasswordGeneratorPolicy);
        passwordGeneratorPolicyObsList.add(index, newPasswordGeneratorPolicy);
    }

    @FXML
    private void initialize() {
        initializePasswordEnforcementPoliciesTableView();
        initializePasswordGeneratorPoliciesTableView();
        initializePolicies();
    }

    void initializePolicies() {
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
            List<PasswordEnforcementPolicy> passwordEnforcementPolicies = vaultRepository.getAllPasswordEnforcementPoliciesByUserProfileId(passwordManagerPaneController.getUserProfileSession().getCurrentUserProfileId());
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
            List<PasswordGeneratorPolicy> passwordGeneratorPolicies = vaultRepository.getAllPasswordGeneratorPoliciesByUserProfileId(passwordManagerPaneController.getUserProfileSession().getCurrentUserProfileId());
            for (PasswordGeneratorPolicy pwdGeneratorPolicy : passwordGeneratorPolicies) {
                passwordGeneratorPolicyObsList.add(pwdGeneratorPolicy);
                if (pwdGeneratorPolicy.isDefaultPolicy()) {
                    defaultPasswordGeneratorPolicy = pwdGeneratorPolicy;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
