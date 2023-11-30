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

import com.mammb.code.editor.ui.pane.EditorPane;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The Application.
 * @author Naotsugu Kobayashi
 */
public class App extends Application {

    private static final KeyCombination SC_N = new KeyCharacterCombination("n", KeyCombination.SHORTCUT_DOWN);

    @Override
    public void start(Stage stage) {
        buildScene(stage, Context.of()).show();
        stage.requestFocus();
    }


    private Stage buildScene(Stage stage, Context context) {

        var editorPane = new EditorPane(context);
        var borderPane = new BorderPane();
        var scene = new Scene(borderPane);
        //borderPane.setTop(new AddressBar());
        borderPane.setCenter(editorPane);
        borderPane.setFocusTraversable(false);

        stage.setScene(scene);
        stage.setTitle("min-editor");
        stage.setOnCloseRequest(editorPane::handleCloseRequest);

        borderPane.setOnKeyPressed(e -> {
            if (SC_N.match(e)) {
                e.consume();
                Stage newStage = new Stage();
                newStage.setX(stage.getX() + 15);
                newStage.setY(stage.getY() + 15);
                buildScene(newStage, context);
                newStage.show();
                newStage.requestFocus();
            }
        });
        return stage;
    }

}
