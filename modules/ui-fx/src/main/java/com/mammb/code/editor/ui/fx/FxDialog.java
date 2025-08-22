/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.editor.ui.fx;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import com.mammb.code.editor.ui.Version;

/**
 * The BasicDialog.
 * @author Naotsugu Kobayashi
 */
public class FxDialog extends Dialog<ButtonType> {

    /**
     * Constructor.
     * @param owner the parent
     * @param title the title
     * @param content the content
     * @param buttonTypes the button types
     */
    public FxDialog(Window owner, String title, String content, ButtonType... buttonTypes) {
        if (owner != null) {
            initOwner(owner);
        }
        setTitle(title);
        DialogPane pane = getDialogPane();
        pane.setContentText(content);
        pane.getButtonTypes().addAll(buttonTypes);
    }

    /**
     * Create the about dialog.
     * @param owner the owner of the dialog
     * @return the about dialog
     */
    public static FxDialog about(Window owner, AppContext ctx) {
        FxDialog dialog = new FxDialog(owner, "About", Version.appName + " " + Version.val, ButtonType.CLOSE);
        var label = new Label(Version.appName + " " + Version.val);
        label.setPrefWidth(300);
        var link = new Hyperlink("Keyboard Shortcut");
        link.setOnAction(_ ->
            ctx.getApp().getHostServices().showDocument("https://github.com/naotsugu/min-editor/blob/main/docs/keyboard-shortcut.md"));
        var box = new VBox(label, link);
        box.setAlignment(Pos.BASELINE_LEFT);
        dialog.getDialogPane().setContent(box);
        return dialog;
    }

    /**
     * Create the confirmation dialog.
     * @param owner the owner of the dialog
     * @param text the text
     * @return the confirmation dialog
     */
    public static FxDialog confirmation(Window owner, String text) {
        return new FxDialog(owner, "Confirmation", text, ButtonType.CANCEL, ButtonType.OK);
    }

}
