package com.mammb.code.editor.ui.app;

import javafx.scene.layout.StackPane;

public class ToolBar extends StackPane {

    private AddressBar addressBar = new AddressBar();

    public ToolBar() {
        getChildren().add(addressBar);
    }

}
