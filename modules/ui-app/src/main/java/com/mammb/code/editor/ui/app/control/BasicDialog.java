package com.mammb.code.editor.ui.app.control;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class BasicDialog extends Dialog<ButtonType> {

    public BasicDialog() {
        StCss.instance.into(getDialogPane());
    }

}
