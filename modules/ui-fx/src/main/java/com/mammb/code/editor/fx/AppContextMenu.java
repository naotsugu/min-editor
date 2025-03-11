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

    private final EditorPane editorPane;

    public AppContextMenu(EditorPane editorPane) {
        super();
        this.editorPane = editorPane;
        build();
    }

    private void build() {

        boolean selected = editorPane.query(Query.selectedCounts) > 0;

        var cut = new MenuItem("Cut");
        //cut.setAccelerator(CommandKeys.SC_X);
        cut.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.cut(FxClipboard.instance))));
        cut.setDisable(!selected);

        var copy = new MenuItem("Copy");
        //copy.setAccelerator(CommandKeys.SC_C);
        copy.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.copy(FxClipboard.instance))));
        copy.setDisable(!selected);

        var paste = new MenuItem("Paste");
        //paste.setAccelerator(CommandKeys.SC_V);
        paste.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.paste(FxClipboard.instance))));
        paste.setDisable(!FxClipboard.instance.hasContents());

        getItems().addAll(cut, copy, paste);
    }

}
