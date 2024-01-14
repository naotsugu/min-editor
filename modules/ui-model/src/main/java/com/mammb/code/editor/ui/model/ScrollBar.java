/*
 * Copyright 2023-2024 the original author or authors.
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
package com.mammb.code.editor.ui.model;

/**
 * ScrollBar.
 * @author Naotsugu Kobayashi
 */
public interface ScrollBar<T extends Number> {

    /** The width of scroll bar. */
    double WIDTH = 10;

    /**
     * Get the minimum value represented by this {@code ScrollBar}.
     * @return the minimum value represented by this {@code ScrollBar}
     */
    T getMin();

    /**
     * Set the minimum value.
     * @param min the minimum value
     */
    void setMin(T min);

    /**
     * Get the maximum value represented by this {@code ScrollBar}.
     * @return the maximum value represented by this {@code ScrollBar}
     */
    T getMax();

    /**
     * Set the maximum value.
     * @param max the maximum value
     */
    void setMax(T max);

    /**
     * Get the visible amount of the scrollbar's range.
     * This amount is typically represented by the size of the scroll bar's thumb
     * @return the visible amount of the scrollbar's range
     */
    T getVisibleAmount();

    /**
     * Set the visible amount of the scrollbar's range.
     * @param amount the visible amount of the scrollbar's range
     */
    void setVisibleAmount(T amount);

    /**
     * Get the current value represented by this {@code ScrollBar}.
     * @return the current value represented by this {@code ScrollBar}
     */
    T getValue();

    /**
     * Set the current value.
     * @param value the current value
     */
    void setValue(T value);

    /**
     * Set the scrolled handler.
     * @param listener the scrolled handler
     */
    void setOnScrolled(ScrolledHandler<T> listener);

    /**
     * Get the track length.
     * @return the track length
     */
    double getTrackLength();


    /**
     * Gets the ratio of the display area.
     * @return the ratio of the display area
     */
    default double visiblePortion() {
        double length = valueLength();
        return (length > 0)
            ? getVisibleAmount().doubleValue() / length
            : 1.0;
    }


    /**
     * Get the length from the minimum value to the maximum value.
     * @return the length from the minimum value to the maximum value
     */
    default double valueLength() {
        double length = getMax().doubleValue() - getMin().doubleValue();
        return (length > 0) ? length : 0;
    }


    /**
     * Get the size of the scroll bar's thumb.
     * @return the size of the scroll bar's thumb
     */
    default double thumbSize() {
        double thumbLength = getTrackLength() * visiblePortion();
        if (thumbLength <= WIDTH * 2) {
            return WIDTH * 2;
        } else if (thumbLength > getTrackLength()) {
            return getTrackLength();
        }
        return thumbLength;
    }

}
