package com.jquinss.passwordmanager.util.misc;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.Validator;

import java.util.Optional;

public class DialogBuilder {
    private DialogBuilder() {}

    public static FileChooser buildFileChooser(String title, FileChooser.ExtensionFilter... extFilters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(extFilters);

        return fileChooser;
    }

    public static Dialog<Pair<String, String>> buildTwoTextFieldInputDialog(String title, String headerText, String firstFieldName,
                                                                            String secondFieldName, boolean isOptionalSecondField,
                                                                            Optional<Pair<String, String>> defaultValues) {
        Validator validator = new Validator();
        Dialog<Pair<String, String>> dialog = new Dialog<>();

        dialog.setTitle(title);
        dialog.setHeaderText(headerText);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstField = new TextField();
        firstField.setPromptText(firstFieldName);


        TextField secondField = new TextField();
        secondField.setPromptText(secondFieldName);

        defaultValues.ifPresent(
                values -> {
                    firstField.setText(values.getKey());
                    secondField.setText(values.getValue());
                }
        );

        grid.add(new Label(firstFieldName), 0, 0);
        grid.add(firstField, 1, 0);

        grid.add(new Label(secondFieldName), 0, 1);
        grid.add(secondField, 1, 1);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

        //okButton.setDisable(true);

        validator.createCheck()
                .withMethod(DialogBuilder::required)
                .dependsOn("text", firstField.textProperty())
                .decorates(firstField)
                .immediate();

        if (!isOptionalSecondField) {
            validator.createCheck()
                    .withMethod(DialogBuilder::required)
                    .dependsOn("text", secondField.textProperty())
                    .decorates(secondField)
                    .immediate();
        }

        okButton.disableProperty().bind(validator.containsErrorsProperty());

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(firstField.getText().trim(), secondField.getText());
            }

            return null;
        });

        return dialog;
    }

    public static Alert buildAlertDialog(String title, String headerText,
                                         String contentText, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        return alert;
    }

    private static void required(Check.Context context) {
        String text = context.get("text");
        if (text == null || text.isEmpty()) {
            context.error("This field is required");
        }
    }
}
