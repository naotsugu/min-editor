package com.mammb.code.editor.ui.app;

import javafx.scene.control.Button;

public class ThemeButton extends Button {

    private final ThemeColor themeColor;

    public ThemeButton(String text, ThemeColor themeColor) {
        super(text);
        this.themeColor = themeColor;
        themeColor.apply(this);
        themeColor.applyHover(this);
    }

}
