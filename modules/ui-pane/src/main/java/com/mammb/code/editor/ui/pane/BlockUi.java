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
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import static java.lang.System.Logger.Level.ERROR;

/**
 * BlockUi.
 * @author Naotsugu Kobayashi
 */
public class BlockUi extends StackPane {

    /** logger. */
    private static final System.Logger log = System.getLogger(BlockUi.class.getName());


    /**
     * Constructor.
     * @param bgColor the background color
     */
    private BlockUi(Color bgColor) {

        setBackground(new Background(new BackgroundFill(
            Color.color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0.75),
            CornerRadii.EMPTY, Insets.EMPTY)));
        setCursor(Cursor.DEFAULT);
        setOnKeyPressed(Event::consume);
        setOnKeyTyped(Event::consume);
        setOnMouseClicked(Event::consume);

        getChildren().add(IndeterminateProgress.of());

    }


    public static void run(Pane blocked, Task<?> task) {
        var block = new BlockUi(selectBackground(blocked));
        blocked.getChildren().add(block);
        block.requestFocus();
        task.setOnSucceeded(withRelease(task.getOnSucceeded(), blocked, block));
        task.setOnCancelled(withRelease(task.getOnCancelled(), blocked, block));
        task.setOnFailed(withRelease(task.getOnFailed(), blocked, block));
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


    /**
     * Selects the background color for the specified region.
     * @param region the specified region
     * @return the background color
     */
    private static Color selectBackground(Region region) {

        if (region.getBackground() == null) {
            return Color.TRANSPARENT;
        }

        for (BackgroundFill fill : region.getBackground().getFills()) {
            if (fill.getFill() instanceof Color color) {
                return color;
            }
        }

        return Color.TRANSPARENT;

    }

}
