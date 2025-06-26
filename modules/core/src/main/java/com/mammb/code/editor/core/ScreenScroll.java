/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.core;

/**
 * Represents an interface for handling screen scrolling operations.
 * Provides methods for adjusting vertical and horizontal scroll
 * positions, as well as retrieving horizontal scroll values.
 * @author Naotsugu Kobayashi
 */
public interface ScreenScroll {

    /**
     * Adjusts vertical scroll position within defined limits.
     *
     * @param min the minimum value of the scroll range
     * @param max the maximum value of the scroll range
     * @param val the current value or position in the scroll range
     * @param len the length or extent of the scrollable content
     */
    void vertical(int min, int max, int val, int len);

    /**
     * Adjusts the horizontal scroll position within defined limits.
     *
     * @param min the minimum value of the scroll range
     * @param max the maximum value of the scroll range
     * @param val the current value or position in the scroll range
     * @param len the length or extent of the scrollable content
     */
    void horizontal(double min, double max, double val, double len);

    /**
     * Retrieves the horizontal scroll value or position on the x-axis.
     *
     * @return the current horizontal scroll value or position on the x-axis
     */
    double xVal();

    /**
     * Retrieves the vertical scroll value or position on the y-axis.
     *
     * @return the current vertical scroll value or position on the y-axis
     */
    int yVal();
}
