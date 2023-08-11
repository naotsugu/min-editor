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

    T getMin();
    void setMin(T min);
    T getMax();
    void setMax(T max);
    T getVisibleAmount();
    void setVisibleAmount(T amount);
    T getValue();
    void setValue(T value);

    double getTruckLength();


    default double visiblePortion() {
        double length = valueLength();
        return (length > 0)
            ? getVisibleAmount().doubleValue() / length
            : 1.0;
    }

    default double valueLength() {
        return Math.max(0, getMax().doubleValue() - getMin().doubleValue());
    }

    default double thumbLength() {
        return clamp(WIDTH, getTruckLength() * visiblePortion(), getTruckLength());
    }

    /**
     * Clamps the given value to be strictly between the min and max values.
     */
    private static double clamp(double min, double value, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static <T extends Number> ScrollBar<T> empty() {
        return new ScrollBar<>() {
            @Override public T getMin() { return null; }
            @Override public void setMin(T min) { }
            @Override public T getMax() { return null; }
            @Override public void setMax(T max) { }
            @Override public T getVisibleAmount() { return null; }
            @Override public void setVisibleAmount(T amount) { }
            @Override public T getValue() { return null; }
            @Override public void setValue(T value) { }
            @Override public double getTruckLength() { return 0; }
        };
    }

}
