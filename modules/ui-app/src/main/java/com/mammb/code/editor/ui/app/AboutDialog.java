package com.mammb.code.editor.ui.app;

import com.mammb.code.editor.ui.app.control.BasicDialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class AboutDialog extends BasicDialog {

    public AboutDialog() {
        super();
        final DialogPane dialogPane = getDialogPane();
        dialogPane.setContentText("min-editor " + Version.value);
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
        setTitle("About");
    }

}
