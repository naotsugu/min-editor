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

import com.mammb.code.editor.ui.app.control.ThemeCss;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The Application.
 * @author Naotsugu Kobayashi
 */
public class App extends Application {


    @Override
    public void start(Stage stage) {
        Context context = AppContext.of(getParameters());
        ThemeCss.install(context.preference().colorScheme());
        buildScene(stage, context).show();
        stage.requestFocus();
    }


    /**
     * Build the editor scene.
     * @param stage the stage
     * @param context the context
     * @return the stage
     */
    Stage buildScene(Stage stage, Context context) {

        var editorStage = new EditorStage(stage, context, this);

        var borderPane = new BorderPane(
            editorStage.editorPane(),
            editorStage.commandBar(),
            null, null, null);
        BorderPane.setMargin(editorStage.commandBar(), new Insets(4, 2, 4, 2));
        borderPane.setFocusTraversable(false);
        borderPane.setOnKeyPressed(editorStage::handleKeyPressed);
        borderPane.setOnKeyReleased(editorStage::handleKeyReleased);

        var scene = new Scene(borderPane);
        ThemeCss.of().into(scene);

        return editorStage.bind(scene);
    }

}
