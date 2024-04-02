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

import com.mammb.code.editor.ui.app.control.UiContextMenu;
import com.mammb.code.editor.ui.pane.EditorDownCall;
import com.mammb.code.editor.ui.pane.Keys;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * The EditorContextMenu.
 * @author Naotsugu Kobayashi
 */
public class EditorContextMenu extends UiContextMenu {

    public EditorContextMenu(EditorDownCall downCall) {

        var cut = new MenuItem("Cut");
        cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
        cut.setOnAction(e -> downCall.requestKeyAction(Keys.Action.CUT));

        var copy = new MenuItem("Copy");
        copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
        copy.setOnAction(e -> downCall.requestKeyAction(Keys.Action.COPY));

        var paste = new MenuItem("Paste");
        paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN));
        paste.setOnAction(e -> downCall.requestKeyAction(Keys.Action.PASTE));

        getItems().addAll(cut, copy, paste);

    }

}
