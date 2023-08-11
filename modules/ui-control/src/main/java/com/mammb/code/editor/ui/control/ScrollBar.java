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

/**
 * ScrollBar.
 * @author Naotsugu Kobayashi
 */
public interface ScrollBar<T extends Number> {

    /** The width of scroll bar. */
    double WIDTH = 9;

    /**
     * Get the min value.
     * @return the min value
     */
    T getMin();

    void setMin(T min);

    /**
     * Get the max value.
     * @return the max value
     */
    T getMax();

    void setMax(T max);

    /**
     * Get the visible amount.
     * @return the visible amount
     */
    T getVisibleAmount();
    void setVisibleAmount(T amount);

    /**
     * Get the value.
     * @return the value
     */
    T getValue();
    void setValue(T value);

    /**
     * Get the truck length.
     * @return the truck length
     */
    double getTruckLength();


    default double visiblePortion() {
        double length = valueLength();
        return (length > 0)
            ? getVisibleAmount().doubleValue() / length
            : 1.0;
    }

    default double valueLength() {
        double length = getMax().doubleValue() - getMin().doubleValue();
        return (length > 0) ? length : 0;
    }

    default double thumbLength() {
        double thumbLength = getTruckLength() * visiblePortion();
        if (thumbLength < WIDTH) {
            return WIDTH;
        } else if (thumbLength > getTruckLength()) {
            return getTruckLength();
        }
        return thumbLength;
    }

    static ScrollBar<Integer> vEmpty() {
        return new ScrollBar<>() {
            @Override public Integer getMin() { return 0; }
            @Override public void setMin(Integer min) { }
            @Override public Integer getMax() { return 0; }
            @Override public void setMax(Integer max) { }
            @Override public Integer getVisibleAmount() { return 0; }
            @Override public void setVisibleAmount(Integer amount) { }
            @Override public Integer getValue() { return 0; }
            @Override public void setValue(Integer value) { }
            @Override public double getTruckLength() { return 0; }
        };
    }

    static ScrollBar<Double> hEmpty() {
        return new ScrollBar<>() {
            @Override public Double getMin() { return 0.0; }
            @Override public void setMin(Double min) { }
            @Override public Double getMax() { return 0.0; }
            @Override public void setMax(Double max) { }
            @Override public Double getVisibleAmount() { return 0.0; }
            @Override public void setVisibleAmount(Double amount) { }
            @Override public Double getValue() { return 0.0; }
            @Override public void setValue(Double value) { }
            @Override public double getTruckLength() { return 0.0; }
        };
    }

}
