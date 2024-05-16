package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.PasswordGeneratorPolicy;
import com.jquinss.passwordmanager.data.PasswordPolicy;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PasswordPoliciesPaneController {
    @FXML
    private TableView<PasswordPolicy> passwordEnforcementPoliciesTableView;
    @FXML
    private TableColumn<PasswordPolicy, String> passwordEnforcementPolicyNameTableColumn;
    @FXML
    private TableColumn<PasswordPolicy, String> passwordEnforcementIsDefaultPolicyTableColumn;
    @FXML
    private TableView<PasswordGeneratorPolicy> passwordGeneratorPoliciesTableView;
    @FXML
    private TableColumn<PasswordGeneratorPolicy, String> passwordGeneratorPolicyNameTableColumn;
    @FXML
    private TableColumn<PasswordGeneratorPolicy, String> passwordGeneratorIsDefaultPolicyTableColumn;

    private final ObservableList<PasswordPolicy> passwordPolicyObsList = FXCollections.observableArrayList();
    private final ObservableList<PasswordGeneratorPolicy> passwordGeneratorPolicyObsList = FXCollections.observableArrayList();

    @FXML
    private void addPasswordEnforcementPolicy() {
        // TODO
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
    }

    private void initializePasswordGeneratorPoliciesTableView() {
        passwordGeneratorPolicyNameTableColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getName());
        });

        passwordGeneratorIsDefaultPolicyTableColumn.setCellValueFactory(cellData -> {
            boolean state = cellData.getValue().isDefaultPolicy();
            return state ? new SimpleStringProperty("Yes") : new SimpleStringProperty("No");
        });
    }

    private void loadPasswordEnforcementPolicies() {
        // TODO
    }

    private void loadPasswordGeneratorPolicies() {
        // TODO
    }
}
