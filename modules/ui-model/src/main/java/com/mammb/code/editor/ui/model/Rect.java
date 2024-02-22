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
 * @author Naotsugu Kobayashi
 */
public interface Rect {

    /**
     * Get the rect x.
     * @return the rect x
     */
    double x();

    /**
     * Get the rect y.
     * @return the rect y
     */
    double y();

    /**
     * Get the rect width.
     * @return the rect width
     */
    double width();

    /**
     * Get the rect height.
     * @return the rect height
     */
    double height();

    /**
     * Get the top position.
     * @return the top position
     */
    default double top() {
        return y();
    }

    /**
     * Get the bottom position.
     * @return the bottom position
     */
    default double bottom() {
        return y() + height();
    }

}
