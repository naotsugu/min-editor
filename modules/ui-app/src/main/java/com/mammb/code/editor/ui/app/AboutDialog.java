/*
 * Copyright 2019-2023 the original author or authors.
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

import com.mammb.code.editor.ui.app.control.BasicDialog;
import javafx.scene.control.ButtonType;

/**
 * The AboutDialog.
 * @author Naotsugu Kobayashi
 */
public class AboutDialog extends BasicDialog {

    public AboutDialog() {
        var dialogPane = getDialogPane();
        dialogPane.setContentText("min-editor " + Version.value);
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
        setTitle("About");
    }

}
