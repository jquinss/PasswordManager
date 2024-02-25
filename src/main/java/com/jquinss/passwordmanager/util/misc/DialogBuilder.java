package com.jquinss.passwordmanager.util.misc;

import javafx.stage.FileChooser;

public class DialogBuilder {
    private DialogBuilder() {}

    public static FileChooser buildFileChooser(String title, FileChooser.ExtensionFilter... extFilters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(extFilters);

        return fileChooser;
    }
}
