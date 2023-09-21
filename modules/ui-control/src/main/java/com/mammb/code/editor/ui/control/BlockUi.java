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
package com.mammb.code.editor.ui.control;

import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.Logger.Level.ERROR;

/**
 * BlockUi.
 * @author Naotsugu Kobayashi
 */
public class BlockUi extends StackPane {

    /** logger. */
    private static final System.Logger log = System.getLogger(BlockUi.class.getName());

    /** The executor. */
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory());
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow));
    }

    /** The truck height. */
    private static final double TRUCK_HEIGHT = 8;

    /** The indeterminate bar width. */
    private static final double INDETERMINATE_BAR_WIDTH = 50;

    /** The background color. */
    private final Color bgColor;

    /** The progress truck. */
    private final StackPane progressTruck;
    /** The progress bar. */
    private final StackPane progressBar;

    /** The indeterminate transition. */
    private IndeterminateTransition indeterminateTransition;


    /**
     * Constructor.
     * @param bgColor the background color
     */
    private BlockUi(Color bgColor) {

        this.bgColor = bgColor;
        this.progressTruck = createProgressTruck();
        this.progressBar = createProgressBar();
        this.indeterminateTransition = createIndeterminateTimeline();

        progressTruck.getChildren().add(progressBar);
        indeterminateTransition.endProperty().bind(progressTruck.widthProperty());

        setBackground(new Background(new BackgroundFill(
            Color.color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0.75),
            CornerRadii.EMPTY, Insets.EMPTY)));
        setCursor(Cursor.DEFAULT);
        setOnKeyPressed(Event::consume);
        setOnKeyTyped(Event::consume);
        setOnMouseClicked(Event::consume);

        getChildren().add(progressTruck);

    }


    public static void run(Pane blocked, Task<?> task) {
        var block = new BlockUi(backgroundColor(blocked));
        blocked.getChildren().add(block);
        block.requestFocus();
        task.setOnSucceeded(withRelease(task.getOnSucceeded(), blocked, block));
        task.setOnCancelled(withRelease(task.getOnCancelled(), blocked, block));
        task.setOnFailed(withRelease(task.getOnFailed(), blocked, block));
        executor.submit(task);
    }


    private static EventHandler<WorkerStateEvent> withRelease(
            EventHandler<WorkerStateEvent> handler, Pane blocked, Pane block) {
        return e -> {
            if (e.getSource().getException() != null) {
                log.log(ERROR, e.getSource().getException());
            }
            if (handler != null) {
                handler.handle(e);
            }
            blocked.getChildren().remove(block);
        };
    }


    private static Color backgroundColor(Region region) {
        return (region.getBackground() == null) ? Color.WHITE : region.getBackground().getFills().stream()
            .map(BackgroundFill::getFill)
            .filter(Color.class::isInstance)
            .map(Color.class::cast)
            .findFirst()
            .orElse(Color.WHITE);
    }


    private StackPane createProgressTruck() {

        var truck = new StackPane();
        truck.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY.deriveColor(0, 1, 1, 0.1),
            new CornerRadii(4), Insets.EMPTY)));

        truck.setMinHeight(TRUCK_HEIGHT);
        truck.setPrefHeight(TRUCK_HEIGHT);
        truck.setMaxHeight(TRUCK_HEIGHT);
        StackPane.setMargin(truck, new Insets(0, 50, 0, 50));

        Rectangle clip = new Rectangle();
        clip.setArcWidth(4);
        clip.setArcHeight(4);
        truck.layoutBoundsProperty().addListener((observable, ov, nv) -> {
            clip.setWidth(nv.getWidth());
            clip.setHeight(nv.getHeight());
        });
        truck.setClip(clip);

        return truck;
    }


    private StackPane createProgressBar() {
        var bar = new StackPane();
        bar.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY.deriveColor(0, 1, 1, 0.5),
            new CornerRadii(4), Insets.EMPTY)));
        bar.setMinSize(INDETERMINATE_BAR_WIDTH, TRUCK_HEIGHT);
        bar.setPrefSize(INDETERMINATE_BAR_WIDTH, TRUCK_HEIGHT);
        bar.setMaxSize(INDETERMINATE_BAR_WIDTH, TRUCK_HEIGHT);
        StackPane.setAlignment(bar, Pos.CENTER_LEFT);
        return bar;
    }


    private IndeterminateTransition createIndeterminateTimeline() {
        var transition = new IndeterminateTransition(-INDETERMINATE_BAR_WIDTH, 100, progressBar);
        transition.play();
        return transition;
    }


    private static class IndeterminateTransition extends Transition {

        private final WeakReference<Node> barRef;
        private final DoubleProperty start = new SimpleDoubleProperty(0);
        private final DoubleProperty end = new SimpleDoubleProperty(0);

        public IndeterminateTransition(double startX, double endX, Node progressBar) {
            start.set(startX);
            end.set(endX);
            this.barRef = new WeakReference<>(progressBar);
            setCycleDuration(Duration.seconds(2));
            setCycleCount(Timeline.INDEFINITE);
        }

        @Override
        protected void interpolate(double frac) {
            Node b = barRef.get();
            if (b == null) {
                stop();
                return;
            }
            b.setScaleX(-1);
            b.setTranslateX(start.get() + frac * (end.get() - start.get()));
        }

        public DoubleProperty startProperty() { return start; }

        public DoubleProperty endProperty() { return end; }

    }

}
