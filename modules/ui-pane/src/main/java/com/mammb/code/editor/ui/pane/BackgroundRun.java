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
package com.mammb.code.editor.ui.pane;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import static java.lang.System.Logger.Level.ERROR;

/**
 * BackgroundRun.
 * @author Naotsugu Kobayashi
 */
public class BackgroundRun extends StackPane {

    /** logger. */
    private static final System.Logger log = System.getLogger(BackgroundRun.class.getName());


    /**
     * Constructor.
     */
    private BackgroundRun() {
        IndeterminateProgress progress = IndeterminateProgress.elongatedOf();
        StackPane.setAlignment(progress, Pos.TOP_CENTER);
        getChildren().add(progress);
    }


    public static void run(Pane pane, Task<?> task) {
        var run = new BackgroundRun();
        pane.getChildren().add(run);
        task.setOnSucceeded(withRelease(task.getOnSucceeded(), pane, run));
        task.setOnCancelled(withRelease(task.getOnCancelled(), pane, run));
        task.setOnFailed(withRelease(task.getOnFailed(), pane, run));
        TaskExecutor.submit(task);
    }


    private static EventHandler<WorkerStateEvent> withRelease(
            EventHandler<WorkerStateEvent> handler, Pane blocked, Pane block) {
        return e -> {
            if (e.getSource().getException() != null) {
                log.log(ERROR, e.getSource().getMessage(), e.getSource().getException());
            }
            if (handler != null) {
                handler.handle(e);
            }
            blocked.getChildren().remove(block);
        };
    }

}
