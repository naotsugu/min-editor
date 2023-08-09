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
import javafx.scene.AccessibleRole;
import javafx.scene.layout.StackPane;

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

    /** This timeline is used to adjust the value of the bar when the track has been pressed but not released. */
    private Timeline timeline;


    public HScrollBar() {
        setAccessibleRole(AccessibleRole.SCROLL_BAR);
    }

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

}
