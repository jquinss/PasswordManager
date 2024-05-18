package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.control.PasswordEnforcementEditorDialog;
import com.jquinss.passwordmanager.data.PasswordGeneratorPolicy;
import com.jquinss.passwordmanager.data.PasswordEnforcementPolicy;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

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

    @FXML
    private void addPasswordEnforcementPolicy() {
        // TODO
        PasswordEnforcementEditorDialog dialog = new PasswordEnforcementEditorDialog(stage);
        dialog.showAndWait();
    }

    @FXML
    private void removePasswordEnforcementPolicy() {
        // TODO
    }

    @FXML
    private void editPasswordEnforcementPolicy() {
        // TODO
    }

    @FXML
    private void addPasswordGeneratorPolicy() {
        // TODO
    }

    @FXML
    private void removePasswordGeneratorPolicy() {
        // TODO
    }

    @FXML
    private void editPasswordGeneratorPolicy() {
        // TODO
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
            List<PasswordEnforcementPolicy> passwordEnforcementPolicies = DatabaseManager.getInstance().getAllPasswordPolicies();
            passwordEnforcementPolicyObsList.setAll(passwordEnforcementPolicies);
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
