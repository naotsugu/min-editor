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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.AccessibleRole;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

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

    /** The thumb. */
    private final Color baseColor;

    /** This timeline is used to adjust the value of the bar when the track has been pressed but not released. */
    private Timeline timeline;


    public HScrollBar(Color baseColor) {

        this.baseColor = baseColor.deriveColor(0, 1, 1, 0.1);

        setHeight(WIDTH);
        setPrefHeight(WIDTH);
        setMaxHeight(WIDTH);
        setAccessibleRole(AccessibleRole.SCROLL_BAR);

        thumb = new ScrollThumb(WIDTH, WIDTH, baseColor);
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

        widthProperty().addListener(this::handleWidthChanged);

        setOnMouseEntered(this::handleMouseEntered);
        setOnMouseExited(this::handleMouseExited);
        setOnMouseClicked(this::handleTruckClicked);
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
        setBackground(new Background(new BackgroundFill(baseColor, null, null)));
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
        // TODO
    }

    public void thumbDragged(double position) {

    }

    public void trackPress(double position) {

    }

    public void trackRelease() {
        stopTimeline();
    }

    private void adjustValue(double position) {
        // figure out the "value" associated with the specified position
        double posValue = valueLength() * clamp(0, position, 1) + getMin();
        if (Double.compare(posValue, getValue()) != 0) {
            double newValue = (posValue > getValue())
                ? getValue() + getVisibleAmount()
                : getValue() - getVisibleAmount();
            setValue(clamp(getMin(), newValue, getMax()));
        }
    }


    private void positionThumb() {
        double clampedValue = clamp(getMin(), getValue(), getMax());
        double trackPos = (valueLength() > 0)
            ? ((getWidth() - thumb.getWidth()) * (clampedValue - getMin()) / valueLength()) : (0.0F);
        thumb.setTranslateX(snapPositionX(trackPos + snappedLeftInset()));
    }


    private void adjustThumbLength() {
        double thumbLength = thumbLength();
        if (thumbLength >= getWidth()) {
            setVisible(false);
        } else {
            setVisible(true);
            thumb.setWidth(thumbLength());
        }
    }


    private void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    /**
     * Clamps the given value to be strictly between the min and max values.
     */
    private double clamp(double min, double value, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
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
    public double getTruckLength() {
        return getWidth();
    }

    //</editor-fold>

}
