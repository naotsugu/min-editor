package com.mammb.code.editor.ui.app;

import javafx.scene.layout.StackPane;

public class ToolBar extends StackPane {

    private final ThemeColor themeColor;
    private AddressBar addressBar;

    public ToolBar(ThemeColor themeColor) {
        this.themeColor = themeColor;
        addressBar = new AddressBar(themeColor);
        getChildren().add(addressBar);
    }

}
