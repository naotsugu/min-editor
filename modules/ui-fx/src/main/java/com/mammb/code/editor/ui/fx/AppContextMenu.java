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

        var cut = new FxMenuItem("Cut", CommandKeys.SC_X, !textSelected, _ ->
            editorPane.execute(new Command.ActionCommand(Action.cut(FxClipboard.instance))));
        var copy = new FxMenuItem("Copy", CommandKeys.SC_C, !textSelected, _ ->
            editorPane.execute(new Command.ActionCommand(Action.copy(FxClipboard.instance))));
        var paste = new FxMenuItem("Paste", CommandKeys.SC_V, !textCopied, _ ->
            editorPane.execute(new Command.ActionCommand(Action.paste(FxClipboard.instance, false))));
        var pasteAs = new FxMenuItem("Paste with context", CommandKeys.SC_SV, !textCopied, _ ->
            editorPane.execute(new Command.ActionCommand(Action.paste(FxClipboard.instance, true))));

        var backward = new FxMenuItem("Backward", CommandKeys.SC_BW, !editorPane.sessionHistory().hasBackward(), _->
            editorPane.execute(new Command.Backward()));
        var forward = new FxMenuItem("Forward", CommandKeys.SC_FW, !editorPane.sessionHistory().hasForward(), _ ->
            editorPane.execute(new Command.Forward()));

        var searchInBrowser = new FxMenuItem("Search In Browser", null, !textSelected, _ ->
            editorPane.execute(new Command.SearchInBrowser()));
        var translateInBrowser = new FxMenuItem("Translate In Browser", null, !textSelected, _ ->
            editorPane.execute(new Command.TranslateInBrowser()));

        return new MenuItem[] {
            cut, copy, paste, pasteAs,
            new SeparatorMenuItem(),
            backward, forward,
            new SeparatorMenuItem(),
            searchInBrowser, translateInBrowser
        };
    }

}
