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
 * Rect.
 * @param x the position x
 * @param y the position y
 * @param width the width
 * @param height the height
 * @author Naotsugu Kobayashi
 */
public record RectBox(double x, double y, double width, double height) implements Rect {

    /**
     * Create a new smaller rect.
     * @return a new smaller rect
     */
    public RectBox smaller() {
        return smaller(0.7);
    }

    /**
     * Create a new smaller rect.
     * @param scale the scale(0 < scale <= 1)
     * @return a new smaller rect
     */
    public RectBox smaller(double scale) {
        if (scale > 1 || scale <= 0) {
            throw new IllegalArgumentException(STR."illegal scale.[\{scale}]");
        }
        return new RectBox(
            x + (width  * (1 - scale)) / 2,
            y + (height * (1 - scale)) / 2,
            width * scale,
            height * scale);
    }

}

