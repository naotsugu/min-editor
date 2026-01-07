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
package com.mammb.code.editor.ui.fx;

import java.util.Objects;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import com.mammb.code.editor.core.Theme;

/**
 * The float bar.
 * Information bar located in the lower right corner.
 * @author Naotsugu Kobayashi
 */
public class FloatBar extends HBox {

    private final Text text = new Text();

    /**
     * Constructor.
     * @param vScroll the vertical ScrollBar
     * @param hScroll the horizontal ScrollBar
     */
    public FloatBar(ScrollBar vScroll, ScrollBar hScroll) {

        setManaged(false);
        setAlignment(Pos.BOTTOM_CENTER);
        setBackground(new Background(new BackgroundFill(
            Color.web(Theme.current.baseColor().web()),
            new CornerRadii(2),
            new Insets(2, 0, 0, 2))));

        text.setFont(Font.font("Consolas", 12));
        text.setFill(Color.web(Theme.current.fgColor().web()));

        vScroll.layoutBoundsProperty().addListener((_, _, _) -> layout(vScroll, hScroll));
        hScroll.layoutBoundsProperty().addListener((_, _, _) -> layout(vScroll, hScroll));
        hScroll.visibleProperty().addListener((_, _, _) -> layout(vScroll, hScroll));
        layoutBoundsProperty().addListener((_, _, _) -> layout(vScroll, hScroll));

        getChildren().add(text);
        layout(vScroll, hScroll);
    }

    /**
     * Set the float bar information texts.
     * @param strings information texts
     */
    public void setText(String... strings) {
        String joined = String.join("  ", strings);
        if (Objects.equals(text.getText(), joined)) {
            return;
        }
        text.setText(joined);
        layoutSize();
    }

    // TODO add toast messages

    public void handleProgress(Task<?> task) {
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefHeight(text.getLayoutBounds().getHeight() - 4);
        progressBar.progressProperty().bind(task.progressProperty());
        EventHandler<WorkerStateEvent> h = e -> {
            getChildren().remove(progressBar);
            layoutSize();
        };
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, h);
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, h);
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, h);
        getChildren().addFirst(progressBar);
        // TODO queue task
        layoutSize();
    }

    private void layoutSize() {
        setWidth(text.getLayoutBounds().getWidth() + 16 + (getChildren().size() - 1) * 100);
        setHeight(text.getLayoutBounds().getHeight() + 4);
    }

    private void layout(ScrollBar vScroll, ScrollBar hScroll) {
        setLayoutX(hScroll.getWidth() - vScroll.getWidth() - getWidth());
        setLayoutY(vScroll.getHeight() - getHeight() - (hScroll.isVisible() ? hScroll.getHeight() : 0));
    }

}
