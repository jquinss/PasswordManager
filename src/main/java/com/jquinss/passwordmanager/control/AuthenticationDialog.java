package com.jquinss.passwordmanager.control;

import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import net.synedra.validatorfx.Check.Context;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;

public class AuthenticationDialog extends Dialog<Pair<String, String>> {
    protected final TextField usernameField = new TextField();
    protected final PasswordField passwordField = new PasswordField();
    protected Validator validator = new Validator();
    public AuthenticationDialog(String title, String headerText,
                                String usernameText, String passwordText) {

        this.setTitle(title);
        this.setHeaderText(headerText);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) this.getDialogPane().lookupButton(ButtonType.OK);
        TooltipWrapper<Button> okButtonWrapper = new TooltipWrapper<>(
                okButton,
                validator.containsErrorsProperty(),
                Bindings.concat("Cannot create profile:\n", validator.createStringBinding())
        );

        validator.createCheck()
                .withMethod(this::required)
                .dependsOn("text", usernameField.textProperty())
                .decorates(usernameField)
                .immediate();

        validator.createCheck()
                .withMethod(this::required)
                .dependsOn("text", passwordField.textProperty())
                .decorates(passwordField)
                .immediate();

        GridPane gridPane = new GridPane();
        gridPane.add(new Label(usernameText), 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(new Label(passwordText),0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.setVgap(10);
        gridPane.setHgap(5);
        this.getDialogPane().setContent(gridPane);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<String, String>(usernameField.getText(), passwordField.getText());
            }
            return null;
        });
    }

    private void required(Context context) {
        String text = context.get("text");
        if (text == null || text.isEmpty()) {
            context.error("This field is required");
        }
    }
}
