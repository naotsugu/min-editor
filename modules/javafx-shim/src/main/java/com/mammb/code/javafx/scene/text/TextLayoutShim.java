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
package com.mammb.code.javafx.scene.text;

import com.mammb.code.editor.model.layout.GlyphRun;
import com.mammb.code.editor.model.layout.HitPosition;
import com.mammb.code.editor.model.layout.LayoutLine;
import com.mammb.code.editor.model.layout.Span;
import com.mammb.code.javafx.scene.text.impl.GlyphRunImpl;
import com.mammb.code.javafx.scene.text.impl.HitPositionImpl;
import com.mammb.code.javafx.scene.text.impl.LayoutLineImpl;
import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.scene.text.TextLine;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;

/**
 * TextLayoutShim.
 * @author Naotsugu Kobayashi
 */
public class TextLayoutShim {

    /** The delegated text layout. */
    private final TextLayout textLayout;

    /** The native font map. */
    private final Map<Font, Object> nativeFonts = new HashMap<>();


    public TextLayoutShim() {
        this.textLayout = Toolkit.getToolkit().getTextLayoutFactory().createLayout();
    }


    /**
     * Sets the content for the TextLayout.
     * Shorthand for single span text (no rich text).
     * @param string the text
     * @param font the font
     * @return {@code true} is the call modifies the layout internal state.
     */
    public boolean setContent(String string, Font font) {
        return textLayout.setContent(string,
            nativeFonts.computeIfAbsent(font, FontHelper::getNativeFont));
    }


    /**
     * Sets the content for the TextLayout.
     * Supports multiple spans (rich text).
     * @return {@code true} is the call modifies the layout internal state.
     */
    public boolean setContent(Span[] spans) {
        // TODO
        return false;
    }


    /**
     * Sets the content for the TextLayout.
     * @param string the text
     * @param font the native font
     * @return {@code true} is the call modifies the layout internal state.
     */
    private boolean setContent(String string, Object font) {
        return textLayout.setContent(string, font);
    }


    /**
     * Sets the alignment for the TextLayout.
     * @return returns true is the call modifies the layout internal state.
     */
    public boolean setAlignment(TextAlignment alignment) {
        return textLayout.setAlignment(alignment.ordinal());
    }


    /**
     * Sets the wrap width for the TextLayout.
     * @return returns true is the call modifies the layout internal state.
     */
    public boolean setWrapWidth(float wrapWidth) {
        return textLayout.setWrapWidth(wrapWidth);
    }


    /**
     * Sets the line spacing for the TextLayout.
     * @return returns true is the call modifies the layout internal state.
     */
    public boolean setLineSpacing(float spacing) {
        return textLayout.setLineSpacing(spacing);
    }


    /**
     * Sets the tab size for the TextLayout.
     * @param spaces the number of spaces represented by a tab. Default is 8.
     * Minimum is 1, lower values will be clamped to 1.
     * @return returns true if the call modifies the layout internal state.
     */
    public boolean setTabSize(int spaces) {
        return textLayout.setTabSize(spaces);
    }


    /**
     * Gets the lines of text layout.
     * @return the lines of text layout
     */
    public LayoutLine[] getLines() {
        TextLine[] lines = textLayout.getLines();
        LayoutLine[] ret = new LayoutLine[lines.length];
        for (int i = 0; i < lines.length; i++) {
            ret[i] = LayoutLineImpl.of(lines[i]);
        }
        return ret;
    }


    /**
     * Gets the GlyphRun of text layout.
     * The runs are returned order visually (rendering order), starting from the first line.
     * @return the GlyphRun of text layout
     */
    public GlyphRun[] getRuns() {
        GlyphList[] runs = textLayout.getRuns();
        GlyphRun[] ret = new GlyphRun[runs.length];
        for (int i = 0; i < runs.length; i++) {
            ret[i] = GlyphRunImpl.of(runs[i]);
        }
        return ret;
    }

    /**
     * Get the hit information in the text plane.
     * @param x the specified point x to be tested
     * @param y the specified point y to be tested
     * @return the hit position
     */
    public HitPosition getHitInfo(float x, float y) {
        return new HitPositionImpl(textLayout.getHitInfo(x, y));
    }

}
