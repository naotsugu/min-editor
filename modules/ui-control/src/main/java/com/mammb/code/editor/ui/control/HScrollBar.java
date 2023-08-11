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

        heightProperty().addListener(this::handleHeightChanged);

        setOnMouseEntered(this::handleMouseEntered);
        setOnMouseExited(this::handleMouseExited);
        setOnMouseClicked(this::handleTruckClicked);
    }

    private void handleMinValueChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
    }

    private void handleMaxValueChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
    }

    private void handleValueChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
    }

    private void handleVisibleAmountChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
    }

    private void handleHeightChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
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

    private void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
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
