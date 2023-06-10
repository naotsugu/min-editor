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
package com.mammb;

/**
 * Layout.
 * @author Naotsugu Kobayashi
 */
public interface Layout {

    /**
     * Get the X coordinate of this {@code Layout}.
     * @return the X coordinate of this {@code Layout}
     */
    double x();

    /**
     * Get the Y coordinate of this {@code Layout}.
     * @return the Y coordinate of this {@code Layout}
     */
    double y();

    /**
     * The width of the {@code Layout}.
     * @return the width of the {@code Layout}
     */
    double width();

    /**
     * The height of the {@code Layout}.
     * @return the height of the {@code Layout}
     */
    double height();

    /**
     * Create a new Layout.
     * @param x the X coordinate of this {@code Layout}
     * @param y the Y coordinate of this {@code Layout}
     * @param width the width of the {@code Layout}
     * @param height the height of the {@code Layout}
     * @return a created Layout
     */
    static Layout of(double x, double y, double width, double height) {
        record LayoutRecord(double x, double y, double width, double height) implements Layout { }
        return new LayoutRecord(x, y, width, height);
    }

}
