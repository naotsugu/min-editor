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
import com.mammb.code.editor.ui.prefs.ColorScheme;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.application.Application;
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
        buildScene(stage, AppContext.of(getParameters())).show();
        stage.requestFocus();
    }


    private Stage buildScene(Stage stage, Context context) {
        var handle = new AppEditorHandle();
        var editorPane = new EditorPane(context, handle);
        var borderPane = new BorderPane();
        var scene = new Scene(borderPane);

        var colorScheme = ColorScheme.DARK;
        var themeColor = switch (colorScheme) {
            case DARK  -> ThemeColor.darkDefault();
            case LIGHT -> ThemeColor.lightDefault();
        };
        var bar = new ThemeBar(themeColor);

        borderPane.setTop(bar);
        borderPane.setCenter(editorPane);
        borderPane.setFocusTraversable(false);

        stage.setScene(scene);
        stage.setTitle("min-editor");
        stage.setOnCloseRequest(editorPane::handleCloseRequest);

        borderPane.setOnKeyPressed(e -> {
            if (AppKeys.SC_N.match(e)) {
                e.consume();
                Stage newStage = new Stage();
                newStage.setX(stage.getX() + 15);
                newStage.setY(stage.getY() + 15);
                buildScene(newStage, context);
                newStage.show();
                newStage.requestFocus();
            }
        });

        handle.setAddressPathProperty(bar.addressTextProperty());

        return stage;
    }

}
