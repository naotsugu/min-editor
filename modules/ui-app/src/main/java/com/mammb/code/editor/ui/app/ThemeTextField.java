package com.mammb.code.editor.ui.app;

import javafx.scene.control.TextField;

public class ThemeTextField extends TextField {

    private final ThemeColor themeColor;

    public ThemeTextField(ThemeColor themeColor) {
        this.themeColor = themeColor;
        themeColor.apply(this);
        setStyle("-fx-text-fill: %1$s; -fx-font: 14px \"Consolas\";"
            .formatted(themeColor.foregroundColorString()));
    }

}
