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
 * @param w the width
 * @param h the height
 * @author Naotsugu Kobayashi
 */
public record Rect(double x, double y, double w, double h) {

    public double x2() {
        return x + w;
    }

    public double y2() {
        return y + h;
    }

    /**
     * Create a new smaller rect.
     * @return a new smaller rect
     */
    public Rect smaller() {
        return smaller(0.7);
    }

    /**
     * Create a new smaller rect.
     * @param scale the scale(0 < scale <= 1)
     * @return a new smaller rect
     */
    public Rect smaller(double scale) {
        if (scale > 1 || scale <= 0) {
            throw new IllegalArgumentException("illegal scale.[" + scale + "]");
        }
        return new Rect(
            x + (w * (1 - scale)) / 2,
            y + (h * (1 - scale)) / 2,
            w * scale,
            h * scale);
    }

}

