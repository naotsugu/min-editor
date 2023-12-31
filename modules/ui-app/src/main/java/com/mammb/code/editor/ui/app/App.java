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

import com.mammb.code.editor.ui.app.control.ThemeCss;
import com.mammb.code.editor.ui.pane.EditorPane;
import com.mammb.code.editor.ui.pane.Session;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.application.Application;
import javafx.geometry.Insets;
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
        Context context = AppContext.of(getParameters());
        ThemeCss.install(context.preference().colorScheme());
        buildScene(stage, context).show();
        stage.requestFocus();
    }


    /**
     * Build the scene.
     * @param stage the stage
     * @param context the context
     * @return the stage
     */
    private Stage buildScene(Stage stage, Context context) {

        var upCall = new AppEditorUpCall();
        var editorPane = new EditorPane(context, upCall);
        var downCall = editorPane.downCall();
        var bar = new CommandBar();
        var session = new EditorSession();

        var borderPane = new BorderPane(editorPane, bar, null, null, null);
        BorderPane.setMargin(bar, new Insets(4, 2, 4, 2));
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
            } else if (AppKeys.SC_FORWARD.match(e)) {
                downCall.requestPathChange(session.forward());
            } else if (AppKeys.SC_BACKWARD.match(e)) {
                downCall.requestPathChange(session.backward());
            } else if (AppKeys.SC_F.match(e)) {
                bar.setVisibleSearchField(true);
            }
        });

        var scene = new Scene(borderPane);
        ThemeCss.install(context.preference().colorScheme());
        ThemeCss.of().into(scene);
        stage.setScene(scene);
        stage.setTitle(Version.appName);
        stage.setOnCloseRequest(editorPane::handleCloseRequest);

        // initEditorHandle
        upCall.onPathChanged(c -> {
            bar.setPathText(c.session().path());
            session.push(c.session(), c.prevSession());
        });
        upCall.onContentModified(c -> bar.setPathModified(c.modified()));

        bar.setOnTextCommitted(s -> downCall.requestPathChange(Session.of(Path.of(s))));
        bar.setOnPathSelected(p -> downCall.requestPathChange(Session.of(p)));
        bar.setOnForwardClicked(() -> downCall.requestPathChange(session.forward()));
        bar.setOnBackwardClicked(() -> downCall.requestPathChange(session.backward()));

        session.setForwardDisableProperty(bar.forwardDisableProperty());
        session.setBackwardDisableProperty(bar.backwardDisableProperty());

        return stage;
    }

}
