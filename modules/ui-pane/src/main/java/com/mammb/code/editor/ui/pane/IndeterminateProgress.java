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
package com.mammb.code.editor.ui.pane;

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

    /** The progress bar. */
    private final Rectangle bar;

    /** The indeterminate transition. */
    private IndeterminateTransition transition;


    /**
     * Constructor.
     */
    private IndeterminateProgress(double height, double radii, double lrMargin) {

        setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY.deriveColor(0, 1, 1, 0.1),
            new CornerRadii(radii), Insets.EMPTY)));
        setMinHeight(height);
        setPrefHeight(height);
        setMaxHeight(height);
        setCursor(Cursor.DEFAULT);
        StackPane.setMargin(this, new Insets(0, lrMargin, 0, lrMargin));

        initClip(radii);

        bar = new Rectangle();
        bar.setFill(Color.LIGHTGRAY.deriveColor(0, 1, 1, 0.6));
        bar.setArcWidth(radii);
        bar.setArcHeight(radii);
        bar.setHeight(height);
        layoutBoundsProperty().addListener((observable, ov, nv)
            -> bar.setWidth(nv.getWidth() / 10));
        StackPane.setAlignment(bar, Pos.CENTER_LEFT);
        getChildren().add(bar);

        transition = new IndeterminateTransition(bar);
        transition.endProperty().bind(widthProperty());
        transition.startProperty().bind(bar.widthProperty().negate());
        transition.play();

    }


    public static IndeterminateProgress of() {
        return new IndeterminateProgress(8, 4, 50);
    }


    public static IndeterminateProgress elongatedOf() {
        return new IndeterminateProgress(3, 0, 0);
    }

    /**
     * Initialize clip.
     */
    private void initClip(double radii) {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(radii);
        clip.setArcHeight(radii);
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
