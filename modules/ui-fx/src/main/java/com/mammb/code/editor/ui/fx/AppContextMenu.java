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
package com.mammb.code.editor.ui.fx;

import com.mammb.code.editor.core.Action;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.ui.base.Command;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * AppContextMenu.
 * @author Naotsugu Kobayashi
 */
public class AppContextMenu extends FxContextMenu {

    /**
     * Constructor.
     * @param editorPane the target {@link EditorPane}
     */
    public AppContextMenu(EditorPane editorPane) {
        super(true, buildMenuItems(editorPane));
    }

    /**
     * Build the menu items.
     */
    private static MenuItem[] buildMenuItems(EditorPane editorPane) {

        boolean textSelected = editorPane.query(Query.selectedCounts) > 0;
        boolean textCopied = FxClipboard.instance.hasContents();
        boolean hasBackward = editorPane.sessionHistory().hasBackward();
        boolean hasForward = editorPane.sessionHistory().hasForward();

        var cut = new MenuItem("Cut");
        cut.setAccelerator(CommandKeys.SC_X);
        cut.setDisable(!textSelected);
        cut.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.cut(FxClipboard.instance))));

        var copy = new MenuItem("Copy");
        copy.setAccelerator(CommandKeys.SC_C);
        copy.setDisable(!textSelected);
        copy.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.copy(FxClipboard.instance))));

        var paste = new MenuItem("Paste");
        paste.setAccelerator(CommandKeys.SC_V);
        paste.setDisable(!textCopied);
        paste.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.paste(FxClipboard.instance, false))));

        var pasteAs = new MenuItem("Paste with Context");
        pasteAs.setAccelerator(CommandKeys.SC_SV);
        pasteAs.setDisable(!textCopied);
        pasteAs.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.paste(FxClipboard.instance, true))));

        var backward = new MenuItem("Backward");
        backward.setAccelerator(CommandKeys.SC_BW);
        backward.setDisable(!hasBackward);
        backward.setOnAction(_-> editorPane.execute(new Command.Backward()));

        var forward = new MenuItem("Forward");
        forward.setAccelerator(CommandKeys.SC_FW);
        forward.setDisable(!hasForward);
        forward.setOnAction(_ -> editorPane.execute(new Command.Forward()));

        var searchInBrowser = new MenuItem("Search in Browser");
        searchInBrowser.setDisable(!textSelected);
        searchInBrowser.setOnAction(_ -> editorPane.execute(new Command.SearchInBrowser()));

        var translateInBrowser = new MenuItem("Translate in Browser");
        translateInBrowser.setDisable(!textSelected);
        translateInBrowser.setOnAction(_ -> editorPane.execute(new Command.TranslateInBrowser()));

        return new MenuItem[] {
            cut, copy, paste, pasteAs,
            new SeparatorMenuItem(),
            backward, forward,
            new SeparatorMenuItem(),
            searchInBrowser, translateInBrowser
        };
    }

}
