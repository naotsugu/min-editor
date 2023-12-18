package com.mammb.code.editor.ui.app;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class UiAboutDialog extends UiDialog<ButtonType> {
    public UiAboutDialog(UiColor themeColor) {
        super(themeColor);

        final DialogPane dialogPane = getDialogPane();
        dialogPane.setContentText("min-editor " + Version.value);
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
        setTitle("About");
    }

}
