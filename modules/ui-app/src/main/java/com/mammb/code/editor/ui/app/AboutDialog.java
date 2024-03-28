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
package com.mammb.code.editor.ui.app;

import com.mammb.code.editor.ui.app.control.UiBasicDialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;

/**
 * The AboutDialog.
 * @author Naotsugu Kobayashi
 */
public class AboutDialog extends UiBasicDialog {

    /**
     * Constructor.
     * @param owner the parent
     */
    public AboutDialog(Window owner) {
        super(owner);
        setTitle("About");
        DialogPane pane = getDialogPane();
        pane.setContentText(STR."\{Version.appName} \{Version.value}");
        pane.getButtonTypes().addAll(ButtonType.CLOSE);
    }

}
