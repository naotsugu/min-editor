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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.lang.ref.WeakReference;

/**
 * The indeterminate progress.
 * @author Naotsugu Kobayashi
 */
public class IndeterminateProgress extends StackPane {

    /** The height. */
    private static final double HEIGHT = 8;

    /** The radii. */
    private static final double RADII = 4;

    /** The progress bar. */
    private final Rectangle bar;

    /** The indeterminate transition. */
    private IndeterminateTransition transition;


    /**
     * Constructor.
     */
    public IndeterminateProgress() {

        setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY.deriveColor(0, 1, 1, 0.1),
            new CornerRadii(RADII), Insets.EMPTY)));
        setMinHeight(HEIGHT);
        setPrefHeight(HEIGHT);
        setMaxHeight(HEIGHT);
        setCursor(Cursor.DEFAULT);
        StackPane.setMargin(this, new Insets(0, 50, 0, 50));

        initClip();

        bar = new Rectangle();
        bar.setFill(Color.LIGHTGRAY.deriveColor(0, 1, 1, 0.5));
        bar.setArcWidth(RADII);
        bar.setArcHeight(RADII);
        bar.setHeight(HEIGHT);
        layoutBoundsProperty().addListener((observable, ov, nv)
            -> bar.setWidth(nv.getWidth() / 10));
        StackPane.setAlignment(bar, Pos.CENTER_LEFT);
        getChildren().add(bar);

        transition = new IndeterminateTransition(bar);
        transition.endProperty().bind(widthProperty());
        transition.startProperty().bind(bar.widthProperty().negate());
        transition.play();

    }


    /**
     * Initialize clip.
     */
    private void initClip() {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(RADII);
        clip.setArcHeight(RADII);
        layoutBoundsProperty().addListener((observable, ov, nv) -> {
            clip.setWidth(nv.getWidth());
            clip.setHeight(nv.getHeight());
        });
        setClip(clip);
    }


    /**
     * Indeterminate transition.
     */
    private static class IndeterminateTransition extends Transition {

        private final WeakReference<Node> barRef;
        private final DoubleProperty start = new SimpleDoubleProperty(-100);
        private final DoubleProperty end = new SimpleDoubleProperty(100);

        public IndeterminateTransition(Node progressBar) {
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
