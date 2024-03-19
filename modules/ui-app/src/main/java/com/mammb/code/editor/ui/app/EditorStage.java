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

import com.mammb.code.editor.ui.pane.EditorDownCall;
import com.mammb.code.editor.ui.pane.EditorPane;
import com.mammb.code.editor.ui.pane.EditorUpCall;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * The EditorStage.
 * @author Naotsugu Kobayashi
 */
public class EditorStage {

    private final App app;
    private final Stage stage;
    private final Context context;

    private final EditorUpCall upCall;
    private final EditorPane editorPane;
    private final EditorDownCall downCall;
    private final CommandBar bar;
    private final EditorSession session;


    public EditorStage(Stage stage, Context context, App app) {

        this.stage = stage;
        this.context = context;
        this.app = app;

        this.upCall = new AppEditorUpCall();
        this.editorPane = new EditorPane(context, upCall);
        this.downCall = editorPane.downCall();
        this.bar = new CommandBar();
        this.session = new EditorSession();
    }

    void handleKeyPressed(KeyEvent e) {
        if (AppKeys.SC_N.match(e)) {
            e.consume();
            Stage newStage = new Stage();
            newStage.setX(stage.getX() + 15);
            newStage.setY(stage.getY() + 15);
            app.buildScene(newStage, context);
            newStage.show();
            newStage.requestFocus();
        } else if (AppKeys.SC_FORWARD.match(e)) {
            downCall.requestPathChange(session.forward());
        } else if (AppKeys.SC_BACKWARD.match(e)) {
            downCall.requestPathChange(session.backward());
        } else if (AppKeys.SC_F.match(e)) {
            var query = EditorDownCall.selectedText();
            downCall.requestQuery(query);
            if (query.ret() != null && !query.ret().isBlank() && !query.ret().contains("\n")) {
                bar.setVisibleSearchField(query.ret());
                downCall.requestSelectClear();
            }
        }

    }

    public Stage getStage() {
        return stage;
    }

}
