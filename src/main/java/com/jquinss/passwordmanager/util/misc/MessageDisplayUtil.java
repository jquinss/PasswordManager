package com.jquinss.passwordmanager.util.misc;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class MessageDisplayUtil {
    private MessageDisplayUtil(){}

    public static void showTemporaryMessage(Label label, String text, String cssStyleClass, int delay) {
        label.getStyleClass().remove(label.getStyleClass().toString());
        label.getStyleClass().add(cssStyleClass);
        label.setText(text);
        label.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(delay));
        pause.setOnFinished(e -> label.setVisible(false));
        pause.play();
    }
}
