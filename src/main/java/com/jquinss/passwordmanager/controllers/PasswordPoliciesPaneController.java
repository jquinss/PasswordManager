package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.app.AppContext;
import com.jquinss.passwordmanager.control.PasswordEnforcementPolicyEditorDialog;
import com.jquinss.passwordmanager.control.PasswordGeneratorPolicyEditorDialog;
import com.jquinss.passwordmanager.vault.repository.VaultRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

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
    private final VaultRepository vaultRepository;
    private final Properties sessionVariables;
    private final Logger logger = LoggerFactory.getLogger(PasswordPoliciesPaneController.class);
    private PasswordEnforcementPolicy defaultPasswordEnforcementPolicy;
    private PasswordGeneratorPolicy defaultPasswordGeneratorPolicy;

    public PasswordPoliciesPaneController(AppContext appContext) {
        this.vaultRepository = appContext.vaultRepository();
        this.sessionVariables = appContext.sessionVariables();
    }

    @FXML
    private void addPasswordEnforcementPolicy(ActionEvent actionEvent) {
        PasswordEnforcementPolicyEditorDialog dialog = new PasswordEnforcementPolicyEditorDialog(getStageFromActionEvent(actionEvent), PasswordPolicyEditorMode.CREATE);
        dialog.showAndWait().ifPresent(passwordEnforcementPolicy -> {
            try {
                logger.info("Creating Password Enforcement policy");
                passwordEnforcementPolicy.setUserProfileId(Integer.parseInt(sessionVariables.getProperty("profileId")));
                vaultRepository.addPasswordEnforcementPolicy(passwordEnforcementPolicy);
                passwordEnforcementPolicyObsList.add(passwordEnforcementPolicy);

                if (passwordEnforcementPolicy.isDefaultPolicy()) {
                    swapDefaultPasswordEnforcementPolicy(passwordEnforcementPolicy);
                }

                passwordEnforcementPoliciesTableView.getSelectionModel().select(passwordEnforcementPolicy);
                logger.info("Password Enforcement policy has been created");
            }
            catch (SQLException e) {
                logger.error("Failed to create policy", e);
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to create policy",
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
            try {
                if (isPasswordEnforcementPolicyInUse(pwdEnforcementPolicy.getId())) {
                    logger.warn("Password Enforcement policy cannot be removed as it is being used");
                    Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Cannot remove policy",
                            "The policy is in use", Alert.AlertType.WARNING);
                    alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                    alertDialog.showAndWait();
                }
                else {
                    logger.info("Removing Password Enforcement policy");
                    vaultRepository.deletePasswordEnforcementPolicy(pwdEnforcementPolicy);
                    passwordEnforcementPolicyObsList.remove(pwdEnforcementPolicy);
                    logger.info("Password Enforcement policy has been removed");
                    if (pwdEnforcementPolicy.isDefaultPolicy()) {
                        defaultPasswordEnforcementPolicy = null;
                    }
                }
            }
            catch (SQLException e) {
                logger.error("Failed to remove Password Enforcement policy", e);
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to remove policy",
                        "A database error has occurred during the operation", Alert.AlertType.ERROR);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                alertDialog.showAndWait();
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
                    logger.info("Updating Password Enforcement policy");
                    vaultRepository.updatePasswordEnforcementPolicy(newPasswordEnforcementPolicy);

                    if (newPasswordEnforcementPolicy.isDefaultPolicy()) {
                        swapDefaultPasswordEnforcementPolicy(newPasswordEnforcementPolicy);
                    }

                    replacePasswordEnforcementPolicy(origPasswordEnforcementPolicy, newPasswordEnforcementPolicy);
                    passwordEnforcementPoliciesTableView.getSelectionModel().select(newPasswordEnforcementPolicy);
                    logger.info("Password Enforcement policy has been updated");
                }
                catch (SQLException e) {
                    logger.error("Failed to update policy", e);
                    Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to update policy",
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
                logger.info("Creating Password Generator policy");
                passwordGeneratorPolicy.setUserProfileId(Integer.parseInt(sessionVariables.getProperty("profileId")));
                vaultRepository.addPasswordGeneratorPolicy(passwordGeneratorPolicy);
                passwordGeneratorPolicyObsList.add(passwordGeneratorPolicy);

                if (passwordGeneratorPolicy.isDefaultPolicy()) {
                    swapDefaultPasswordGeneratorPolicy(passwordGeneratorPolicy);
                }

                passwordGeneratorPoliciesTableView.getSelectionModel().select(passwordGeneratorPolicy);
                logger.info("Password Generator policy has been created");
            }
            catch (SQLException e) {
                logger.error("Failed to create policy", e);
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to create policy",
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
                logger.warn("The default Password Generator policy cannot be removed");
                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Cannot remove policy",
                        "The default Password Generator policy cannot be removed", Alert.AlertType.WARNING);
                alertDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/jquinss/passwordmanager/styles/styles.css")).toString());
                alertDialog.showAndWait();
            }
            else {
                try {
                    logger.info("Removing Password Generator policy");
                    vaultRepository.deletePasswordGeneratorPolicy(pwdGeneratorPolicy);
                    passwordGeneratorPolicyObsList.remove(pwdGeneratorPolicy);
                    logger.info("Password Generator policy has been removed");
                }
                catch (SQLException e) {
                    logger.error("Failed to remove policy", e);
                    Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to remove policy",
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
                    logger.info("Updating Password Generator policy");
                    vaultRepository.updatePasswordGeneratorPolicy(newPasswordGeneratorPolicy);

                    if (newPasswordGeneratorPolicy.isDefaultPolicy()) {
                        swapDefaultPasswordGeneratorPolicy(newPasswordGeneratorPolicy);
                    }

                    replacePasswordGeneratorPolicy(origPasswordGeneratorPolicy, newPasswordGeneratorPolicy);
                    passwordGeneratorPoliciesTableView.getSelectionModel().select(newPasswordGeneratorPolicy);
                    logger.info("Password Generator policy has been updated");
                }
                catch (SQLException e) {
                    logger.error("Failed to update policy", e);
                    Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Failed to update policy",
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

    private boolean isPasswordEnforcementPolicyInUse(int policyId) throws SQLException {
        return !vaultRepository.getAllPasswordItemsByPasswordEnforcementPolicyId(policyId).isEmpty();
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
        passwordEnforcementPolicyNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

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
            logger.info("Loading Password Enforcement policies");
            List<PasswordEnforcementPolicy> passwordEnforcementPolicies = vaultRepository.getAllPasswordEnforcementPoliciesByUserProfileId(Integer.parseInt(sessionVariables.getProperty("profileId")));
            for (PasswordEnforcementPolicy pwdEnforcementPolicy : passwordEnforcementPolicies) {
                passwordEnforcementPolicyObsList.add(pwdEnforcementPolicy);
                if (pwdEnforcementPolicy.isDefaultPolicy()) {
                    defaultPasswordEnforcementPolicy = pwdEnforcementPolicy;
                }
            }
            logger.info("Password Enforcement policies have been loaded");
        }
        catch (SQLException e) {
            logger.error("Failed to load Password Enforcement policies", e);
        }
    }

    private void loadPasswordGeneratorPolicies() {
        try {
            logger.info("Loading Password Generator policies");
            List<PasswordGeneratorPolicy> passwordGeneratorPolicies =
                    vaultRepository.getAllPasswordGeneratorPoliciesByUserProfileId(Integer.parseInt(sessionVariables.getProperty("profileId")));
            for (PasswordGeneratorPolicy pwdGeneratorPolicy : passwordGeneratorPolicies) {
                passwordGeneratorPolicyObsList.add(pwdGeneratorPolicy);
                if (pwdGeneratorPolicy.isDefaultPolicy()) {
                    defaultPasswordGeneratorPolicy = pwdGeneratorPolicy;
                }
            }
            logger.info("Password Generator policies have been loaded");
        }
        catch (SQLException e) {
            logger.error("Failed to load Password Generator policies", e);
        }
    }
}
