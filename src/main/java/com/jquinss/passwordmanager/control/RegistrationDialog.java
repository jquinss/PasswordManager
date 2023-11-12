package com.jquinss.passwordmanager.control;

import com.jquinss.passwordmanager.util.password.PasswordStrength;
import com.jquinss.passwordmanager.util.password.PasswordStrengthChecker;
import com.jquinss.passwordmanager.util.password.PasswordStrengthCriteria;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;

public class RegistrationDialog extends AuthenticationDialog {
    protected final PasswordField passwordConfirmationField = new PasswordField();
    private final PasswordStrengthChecker passwordStrengthChecker = new PasswordStrengthChecker();
    private PasswordStrength minPasswordStrength = PasswordStrength.EXCELLENT;
    public RegistrationDialog(String title, String headerText,
                              String usernameText, String passwordText, String passwordConfirmText) {
        super(title, headerText, usernameText, passwordText);

        GridPane gridPane = (GridPane) this.getDialogPane().getContent();
        gridPane.add(new Label(passwordConfirmText), 0, 2);
        gridPane.add(passwordConfirmationField, 1, 2);

        Button okButton = (Button) this.getDialogPane().lookupButton(ButtonType.OK);

        validator.createCheck()
                .withMethod(c -> {
                    if (!c.get("passwordField").equals(c.get("passwordConfirmationField"))) {
                        c.error("Passwords do not match");
                    }
                })
                .dependsOn("passwordField", passwordField.textProperty())
                .dependsOn("passwordConfirmationField", passwordConfirmationField.textProperty())
                .decorates(passwordField)
                .decorates(passwordConfirmationField)
                .immediate();

        validator.createCheck()
                .withMethod(c -> {
                    String pwd = c.get("passwordField");
                    PasswordStrength pwdStrength = passwordStrengthChecker.checkPasswordStrength(pwd);
                    if (pwdStrength.getValue() < minPasswordStrength.getValue()) {
                        PasswordStrengthCriteria pwdStrengthCriteria =
                                passwordStrengthChecker.getCriteria(minPasswordStrength);
                        c.error("The password must meet the following requirements:\n" + pwdStrengthCriteria.toString());
                    }
                })
                .dependsOn("passwordField", passwordField.textProperty())
                .decorates(passwordField)
                .immediate();
    }

    public void setMinPasswordStrength(PasswordStrength minPasswordStrength) {
        this.minPasswordStrength = minPasswordStrength;
    }
}
