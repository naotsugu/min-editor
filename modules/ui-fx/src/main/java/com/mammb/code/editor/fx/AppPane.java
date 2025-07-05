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
package com.mammb.code.editor.fx;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import com.mammb.code.editor.core.Session;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The application main pane.
 * @author Naotsugu Kobayashi
 */
public class AppPane extends BorderPane {

    /**
     * Constructor.
     * @param stage the stage
     * @param path the path of content or {@code null}
     * @param ctx the application context
     */
    public AppPane(Stage stage, Path path, AppContext ctx) {

        // restore sessions
        var sessions = new ArrayList<>(ctx.config().sessions());
        if (path != null && Files.exists(path)) {
            // add a command line args path as the session
            sessions.add(Session.of(path));
        }
        if (sessions.isEmpty()) {
            // fill the session if empty
            sessions.add(Session.empty());
        }

        var panes = sessions.stream()
            .map(session -> new EditorPane(ctx).bindLater(session))
            .toArray(EditorPane[]::new);
        var container = new SplitTabPane(ctx, panes);
        setCenter(container);

        stage.focusedProperty().addListener((_, _, focused) -> {
            if (focused) container.reloadExternalChanges();
        });

        stage.setOnCloseRequest(e -> {
            e.consume();
            var notExistsUnsaved = container.closeAll();
            if (notExistsUnsaved) {
                stage.close();
            }
        });
    }

}
