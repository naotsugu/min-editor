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
 * GlyphRun.
 * @author Naotsugu Kobayashi
 */
public interface GlyphRun<F> {

    /**
     * Get the number of glyphs in GlyphRun.
     * @return the number of glyphs in GlyphRun
     */
    int glyphCount();

    /**
     * Get the x position for the given glyphIndex relative the GlyphRun.
     * @param glyphIndex the index number of glyphs in GlyphRun
     * @return the x position for the given glyphIndex relative the GlyphRun
     */
    float posX(int glyphIndex);

    /**
     * Get the y position for the given glyphIndex relative the GlyphRun.
     * @param glyphIndex the index number of glyphs in GlyphRun
     * @return the y position for the given glyphIndex relative the GlyphRun
     */
    float posY(int glyphIndex);

    /**
     * Maps the given glyph index to the char offset.
     * @param glyphIndex the number of glyphs in GlyphRun
     * @return the number of character offset
     */
    int charOffset(int glyphIndex);

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
     * The top-left location of the GlyphRun relative to
     * the origin of the Text Layout.
     */
    Point location();

    /**
     * Get the text for this GlyphRun.
     * @return the text
     */
    String getText();

    /**
     * Get the font for this GlyphRun.
     * @return the font
     */
    F getFont();

    /**
     * Get the character length.
     * @return the character length
     */
    default int charLength() {
        return charOffset(glyphCount()) - charOffset(0);
    }

}
