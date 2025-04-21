/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.fx;

import com.mammb.code.editor.core.Action;
import com.mammb.code.editor.core.Query;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * AppContextMenu.
 * @author Naotsugu Kobayashi
 */
public class AppContextMenu extends ContextMenu {

    /** The target {@link EditorPane}. */
    private final EditorPane editorPane;

    /**
     * Constructor.
     * @param editorPane the target {@link EditorPane}
     */
    public AppContextMenu(EditorPane editorPane) {
        super();
        this.editorPane = editorPane;
        build();
    }

    /**
     * Build the menu items.
     */
    private void build() {

        boolean textSelected = editorPane.query(Query.selectedCounts) > 0;
        String style = "-fx-font: normal 10pt System;";

        var cut = new MenuItem("Cut");
        cut.setStyle(style);
        cut.setAccelerator(CommandKeys.SC_X);
        cut.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.cut(FxClipboard.instance))));
        cut.setDisable(!textSelected);

        var copy = new MenuItem("Copy");
        copy.setStyle(style);
        copy.setAccelerator(CommandKeys.SC_C);
        copy.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.copy(FxClipboard.instance))));
        copy.setDisable(!textSelected);

        var paste = new MenuItem("Paste");
        paste.setStyle(style);
        paste.setAccelerator(CommandKeys.SC_V);
        paste.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.paste(FxClipboard.instance, false))));
        paste.setDisable(!FxClipboard.instance.hasContents());

        var pasteAs = new MenuItem("Paste as plain text");
        pasteAs.setStyle(style);
        pasteAs.setAccelerator(CommandKeys.SC_SV);
        pasteAs.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.paste(FxClipboard.instance, true))));
        pasteAs.setDisable(!FxClipboard.instance.hasContents());

        getItems().addAll(cut, copy, paste, pasteAs);
    }

}
