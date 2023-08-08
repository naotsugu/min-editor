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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.AccessibleRole;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Vertical ScrollBar.
 * @author Naotsugu Kobayashi
 */
public class VScrollBar extends StackPane implements ScrollBar {

    /** The min value of scroll bar. */
    public final IntegerProperty min = new SimpleIntegerProperty(this, "min", 0);

    /** The max value of scroll bar. */
    public final IntegerProperty max = new SimpleIntegerProperty(this, "max", 100);

    /** The value of scroll bar. */
    public final IntegerProperty value = new SimpleIntegerProperty(this, "value", 0);

    /** The visible amount. */
    public final IntegerProperty visibleAmount = new SimpleIntegerProperty(this, "visibleAmount", 100);

    /** The thumb. */
    private final ScrollThumb thumb;

    private final Color backGround;

    /** This timeline is used to adjust the value of the bar when the track has been pressed but not released. */
    private Timeline timeline;


    /**
     * Constructor.
     */
    public VScrollBar(Color baseColor) {

        backGround = baseColor.deriveColor(0, 1, 1, 0.1);

        setWidth(WIDTH);
        setPrefWidth(WIDTH);
        setMaxWidth(WIDTH);
        setAccessibleRole(AccessibleRole.SCROLL_BAR);

        thumb = ScrollThumb.rowOf(WIDTH);
        thumb.setFill(baseColor.deriveColor(0, 1, 1, 0.2));
        getChildren().add(thumb);

        initListener();
    }


    /**
     * Initialize listener.
     */
    private void initListener() {

        min.addListener(this::handleMinValueChanged);
        max.addListener(this::handleMaxValueChanged);
        value.addListener(this::handleValueChanged);
        visibleAmount.addListener(this::handleVisibleAmountChanged);

        heightProperty().addListener(this::handleHeightChanged);

        setOnMouseEntered(this::handleMouseEntered);
        setOnMouseExited(this::handleMouseExited);
        setOnMouseClicked(this::handleTruckClicked);
    }

    private void handleHeightChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        adjustThumbLength();
    }


    private void handleMinValueChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        adjustThumbLength();
    }

    private void handleMaxValueChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        adjustThumbLength();
    }

    private void handleValueChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        positionThumb();
    }

    private void handleVisibleAmountChanged(ObservableValue<? extends Number> observable,
            Number oldValue, Number newValue) {
        adjustThumbLength();
    }

    private void handleMouseEntered(MouseEvent event) {
        setBackground(new Background(new BackgroundFill(backGround, null, null)));
    }

    private void handleMouseExited(MouseEvent event) {
        setBackground(null);
    }


    /**
     * The truck clicked handler.
     * @param event the MouseEvent
     */
    private void handleTruckClicked(MouseEvent event) {
        if (event.isSynthesized()) {
            event.consume();
            return;
        }
    }


    public void thumbDragged(double position) {
        // stop the timeline for continuous increments as drags take precedence
        stopTimeline();

        if (!isFocused() && isFocusTraversable()) requestFocus();
        int newValue = (int) (position * valueLength()) + min.getValue();
        value.setValue(clamp(newValue));
    }


    public void trackPress(double position) {

        if (timeline != null) return;
        if (!isFocused() && isFocusTraversable()) requestFocus();
        final double pos = position;
        final boolean incrementing = (pos > ((value.getValue() - min.getValue()) / valueLength()));
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        final EventHandler<ActionEvent> step =
            event -> {
                boolean i = (pos > ((value.getValue() - min.getValue()) / valueLength()));
                if (incrementing == i) {
                    // we started incrementing and still are, or we
                    // started decrementing and still are
                    adjustValue(pos);
                }
                else {
                    stopTimeline();
                }
            };

        final KeyFrame kf = new KeyFrame(Duration.millis(200), step);
        timeline.getKeyFrames().add(kf);
        // do the first step immediately
        timeline.play();
        step.handle(null);

    }


    public void trackRelease() {
        stopTimeline();
    }

    private void adjustValue(double position) {
        // figure out the "value" associated with the specified position
        int posValue = (int) ((max.getValue() - min.getValue()) * clamp(0, position, 1)) + min.getValue();
        if (Integer.compare(posValue, value.getValue()) != 0) {
            int newValue = (posValue > value.getValue())
                ? value.getValue() + visibleAmount.getValue()
                : value.getValue() - visibleAmount.getValue();
            value.setValue(clamp(newValue));
        }
    }

    private void positionThumb() {
        double clampedValue = clamp(value.getValue());
        double trackPos = (valueLength() > 0)
            ? ((getHeight() - thumb.getHeight()) * (clampedValue - min.getValue()) / valueLength()) : (0.0F);

        thumb.setTranslateY(snapPositionY(trackPos + snappedTopInset()));
    }

    private void adjustThumbLength() {
        double thumbLength = thumbLength();
        if (thumbLength >= getHeight()) {
            setVisible(false);
        } else {
            setVisible(true);
            thumb.setHeight(thumbLength());
        }

    }

    private double visiblePortion() {
        return (valueLength() > 0)
            ? (double) visibleAmount.getValue() / valueLength()
            : 1.0;
    }


    private double thumbLength() {
        return clamp(WIDTH, getHeight() * visiblePortion(), getHeight());
    }

    private int valueLength() {
        return Math.max(0, max.getValue() - min.getValue());
    }

    /**
     * Clamps the given value to be strictly between the min and max values.
     */
    private int clamp(int value) {
        if (value < min.getValue()) return min.getValue();
        if (value > max.getValue()) return max.getValue();
        return value;
    }

    /**
     * Clamps the given value to be strictly between the min and max values.
     */
    private double clamp(double min, double value, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

}
