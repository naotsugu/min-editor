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

import com.mammb.code.editor.ui.model.ScrollBar;
import com.mammb.code.editor.ui.model.ScrolledHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Horizontal ScrollBar.
 * @author Naotsugu Kobayashi
 */
public class HScrollBar extends StackPane implements ScrollBar<Double> {

    /** The min value of scroll bar. */
    public final DoubleProperty min = new SimpleDoubleProperty(this, "min", 0);

    /** The max value of scroll bar. */
    public final DoubleProperty max = new SimpleDoubleProperty(this, "max", 100);

    /** The value of scroll bar. */
    public final DoubleProperty value = new SimpleDoubleProperty(this, "value", 0);

    /** The visible amount. */
    public final DoubleProperty visibleAmount = new SimpleDoubleProperty(this, "visibleAmount", 100);

    /** The thumb. */
    private final ScrollThumb thumb;

    /** The active background color. */
    private final Background backGroundActive;
    /** The passive background color. */
    private final Background backGroundPassive;


    /** This timeline is used to adjust the value of the bar when the track has been pressed but not released. */
    private Timeline timeline;

    private double preDragThumbPos;

    private double dragStart;

    private ScrolledHandler<Double> listener = (oldValue, newValue) -> { };


    public HScrollBar(Color baseColor) {

        backGroundActive = new Background(new BackgroundFill(
            baseColor.deriveColor(0, 1, 1, 0.1), CornerRadii.EMPTY, Insets.EMPTY));
        backGroundPassive = new Background(new BackgroundFill(
            Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));

        setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_PREF_SIZE);
        setPrefHeight(WIDTH);
        setCursor(Cursor.DEFAULT);
        setBackground(backGroundPassive);

        thumb = new ScrollThumb(WIDTH * 2, WIDTH, baseColor);
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

        setOnMousePressed(this::handleTrackMousePressed);
        setOnMouseReleased(this::handleTrackMouseReleased);
        widthProperty().addListener(this::handleWidthChanged);
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            setBackground(backGroundActive);
        } else {
            setOnMouseEntered(this::handleMouseEntered);
            setOnMouseExited(this::handleMouseExited);
        }
        thumb.setOnMousePressed(this::handleThumbMousePressed);
        thumb.setOnMouseDragged(this::handleThumbMouseDragged);
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

    private void handleVisibleAmountChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        adjustThumbLength();
    }

    private void handleWidthChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        adjustThumbLength();
    }

    private void handleMouseEntered(MouseEvent event) {
        setBackground(backGroundActive);
    }

    private void handleMouseExited(MouseEvent event) {
        setBackground(backGroundPassive);
    }

    /**
     * The truck clicked handler.
     * @param event the MouseEvent
     */
    private void handleTrackMousePressed(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) return;
        trackPress(event.getY() / getTrackLength());
        event.consume();
    }

    /**
     * The track mouse released handler.
     * @param event the MouseEvent
     */
    private void handleTrackMouseReleased(MouseEvent event) {
        trackRelease();
        event.consume();
    }

    private void handleThumbMousePressed(MouseEvent event) {
        if (event.isSynthesized()) {
            event.consume();
            return;
        }
        dragStart = thumb.localToParent(event.getX(), event.getY()).getX();
        preDragThumbPos = (Math.clamp(getValue(), getMin(), getMax()) - getMin()) / valueLength();
        event.consume();
    }

    private void handleThumbMouseDragged(MouseEvent event) {
        if (event.isSynthesized()) {
            event.consume();
            return;
        }
        double cur = thumb.localToParent(event.getX(), event.getY()).getX();
        double dragPos = cur - dragStart;
        thumbDragged(preDragThumbPos + dragPos / (getWidth() - thumb.getWidth()));
        event.consume();
    }

    public void thumbDragged(double position) {
        stopTimeline();
        if (!isFocused() && isFocusTraversable()) requestFocus();
        double newValue = (position * valueLength()) + min.getValue();
        double oldValue = value.getValue();
        value.setValue(Math.clamp(newValue, getMin(), getMax()));
        listener.handle(oldValue, value.getValue());
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
                    double old = value.getValue();
                    adjustValue(pos);
                    listener.handle(old, value.getValue());
                } else {
                    stopTimeline();
                }
            };

        final KeyFrame kf = new KeyFrame(Duration.millis(100), step);
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
        double posValue = valueLength() * Math.clamp(position, 0, 1) + getMin();
        if (Double.compare(posValue, getValue()) != 0) {
            double newValue = (posValue > getValue())
                ? getValue() + getVisibleAmount()
                : getValue() - getVisibleAmount();
            setValue(Math.clamp(newValue, getMin(), getMax()));
        }
    }


    private void positionThumb() {
        double clampedValue = Math.clamp(getValue(), getMin(), getMax());
        double trackPos = (valueLength() > 0)
            ? ((getWidth() - thumb.getWidth()) * (clampedValue - getMin()) / valueLength()) : (0.0F);
        thumb.setTranslateX(snapPositionX(trackPos + snappedLeftInset()));
    }


    private void adjustThumbLength() {
        double thumbLength = thumbSize();
        if (thumbLength >= getWidth()) {
            setVisible(false);
        } else {
            setVisible(true);
            thumb.setWidth(thumbSize());
        }
    }


    private void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    @Override
    public void setOnScrolled(ScrolledHandler<Double> listener) {
        this.listener = listener;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">

    @Override
    public Double getMin() {
        return min.getValue();
    }

    @Override
    public void setMin(Double min) {
        this.min.setValue(min);
    }

    @Override
    public Double getMax() {
        return max.getValue();
    }

    @Override
    public void setMax(Double max) {
        this.max.setValue(max);
    }

    @Override
    public Double getVisibleAmount() {
        return visibleAmount.getValue();
    }

    @Override
    public void setVisibleAmount(Double amount) {
        this.visibleAmount.setValue(amount);
    }

    @Override
    public Double getValue() {
        return value.getValue();
    }

    @Override
    public void setValue(Double value) {
        this.value.setValue(value);
    }

    @Override
    public double getTrackLength() {
        return getWidth();
    }

    //</editor-fold>

}
