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
package com.mammb.code.editor2.model.shaped;

/**
 * ShapedRun.
 * @param <F> the type of font
 * @param <C> the type of color
 * @author Naotsugu Kobayashi
 */
public interface ShapedRun<F, C> extends Shaped<F, C> {

    @Override
    F font();

    @Override
    C color();

    /**
     * Get the X coordinate of this {@code CoordPoint}.
     * @return the X coordinate of this {@code CoordPoint}
     */
    float x();

    /**
     * Get the Y coordinate of this {@code CoordPoint}.
     * @return the Y coordinate of this {@code CoordPoint}
     */
    float y();

    /**
     * The width of the {@code GlyphRun}.
     * @return the width of the {@code GlyphRun}
     */
    float width();

    /**
     * The height of the {@code GlyphRun}.
     * @return the height of the {@code GlyphRun}
     */
    float height();

    /**
     * Gets the line start offset.
     * @return the line start offset
     */
    int offset();

    /**
     * Gets the line length in character.
     * @return the line length in character
     */
    int length();

}
