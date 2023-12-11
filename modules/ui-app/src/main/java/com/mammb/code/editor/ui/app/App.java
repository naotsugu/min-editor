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
import com.mammb.code.editor.ui.pane.Session;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.nio.file.Path;

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

        var upCall = new AppEditorUpCall();
        var editorPane = new EditorPane(context, upCall);
        var bar = new ThemeBar(themeColor(context));
        var session = new EditorSession();

        var borderPane = new BorderPane(editorPane, bar, null, null, null);
        borderPane.setFocusTraversable(false);
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

        stage.setScene(new Scene(borderPane));
        stage.setTitle("min-editor");
        stage.setOnCloseRequest(editorPane::handleCloseRequest);

        // initEditorHandle
        upCall.setAddressPathProperty(bar.addressTextProperty());
        var downCall = editorPane.downCall();
        bar.textCommitted(s -> downCall.pathChangeRequest(Session.of(Path.of(s))));

        session.setForwardDisableProperty(bar.forwardDisableProperty());
        session.setBackwardDisableProperty(bar.backwardDisableProperty());

        return stage;
    }

    /**
     * Get the app theme color.
     * @param context the context
     * @return the app theme color
     */
    private static ThemeColor themeColor(Context context) {
        return switch (context.preference().colorScheme()) {
            case DARK  -> ThemeColor.darkDefault();
            case LIGHT -> ThemeColor.lightDefault();
        };
    }

}
