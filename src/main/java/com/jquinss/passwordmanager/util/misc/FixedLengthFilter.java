package com.jquinss.passwordmanager.util.misc;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class FixedLengthFilter implements UnaryOperator<TextFormatter.Change> {
    private final int maxLength;

    public FixedLengthFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        if (change.getControlNewText().length() > maxLength) {
            return null;
        }
        return change;
    }
}
