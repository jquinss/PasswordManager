package com.jquinss.passwordmanager.util.misc;

import com.jquinss.passwordmanager.data.BiValue;
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

    public static Dialog<BiValue<String, String>> buildTwoTextFieldInputDialog(String title, String headerText, String firstFieldName,
                                                                            TextField firstTextField, String secondFieldName,
                                                                            TextField secondTextField, boolean isOptionalSecondField) {
        Validator validator = new Validator();
        Dialog<BiValue<String, String>> dialog = new Dialog<>();

        dialog.setTitle(title);
        dialog.setHeaderText(headerText);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label(firstFieldName), 0, 0);
        grid.add(firstTextField, 1, 0);

        grid.add(new Label(secondFieldName), 0, 1);
        grid.add(secondTextField, 1, 1);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

        //okButton.setDisable(true);

        validator.createCheck()
                .withMethod(DialogBuilder::required)
                .dependsOn("text", firstTextField.textProperty())
                .decorates(firstTextField)
                .immediate();

        if (!isOptionalSecondField) {
            validator.createCheck()
                    .withMethod(DialogBuilder::required)
                    .dependsOn("text", secondTextField.textProperty())
                    .decorates(secondTextField)
                    .immediate();
        }

        okButton.disableProperty().bind(validator.containsErrorsProperty());

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new BiValue<String, String>(firstTextField.getText().trim(), secondTextField.getText());
                //return new Pair<>(firstTextField.getText().trim(), secondTextField.getText());
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
