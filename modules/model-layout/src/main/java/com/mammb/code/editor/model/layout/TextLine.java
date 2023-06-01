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
package com.mammb.code.editor.model.layout;

/**
 * LayoutLine.
 * @author Naotsugu Kobayashi
 */
public interface TextLine {

    /**
     * Get the list of GlyphRun in the line. The list is visually ordered.
     * @return the array of GlyphRun in the line. The list is visually ordered.
     */
    GlyphRun[] runs();

    /**
     * Returns metrics information about the line as follows:
     *
     * bounds().getWidth() - the width of the line.
     * The width for the line is sum of all run's width in the line, it is not
     * affect by any wrapping width but it will include any changes caused by
     * justification.
     *
     * bounds().getHeight() - the height of the line.
     * The height of the line is sum of the max ascent, max descent, and
     * max line gap of all the fonts in the line.
     *
     * bounds.().getMinY() - the ascent of the line (negative).
     * The ascent of the line is the max ascent of all fonts in the line.
     *
     * bounds().getMinX() - the x origin of the line (relative to the layout).
     * The x origin is defined by TextAlignment of the text layout, always zero
     * for left-aligned text.
     */
    Bounds bounds();

    /**
     * Gets the line start offset.
     * @return the line start offset
     */
    int startOffset();

    /**
     * Gets the line length in character.
     * @return the line length in character
     */
    int length();

}
