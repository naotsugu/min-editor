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

import com.mammb.code.editor.model.layout.HitPosition;
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.javafx.scene.text.impl.GlyphRunImpl;
import com.mammb.code.javafx.scene.text.impl.HitPositionImpl;
import com.mammb.code.javafx.scene.text.impl.TextLineImpl;
import com.mammb.code.javafx.scene.text.impl.TextSpanImpl;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.scene.text.TextSpan;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;

import java.util.TreeMap;

/**
 * TextLayoutShim.
 * @author Naotsugu Kobayashi
 */
public class TextLayoutShim {

    /** The delegated text layout. */
    private final TextLayout textLayout;

    /** The native font map. */
    private final TreeMap<Integer, Font> fonts = new TreeMap<>();

    private String text = "";


    /**
     * Create a new TextLayoutShim.
     */
    public TextLayoutShim() {
        textLayout = Toolkit.getToolkit().getTextLayoutFactory().getLayout();
        textLayout.setTabSize(4);
    }

    /**
     * Sets the content for the TextLayout.
     * Supports multiple spans (rich text).
     * @return {@code true} if the call modifies the layout internal state.
     */
    public boolean setContent(FxTextSpan[] spans) {
        StringBuilder sb = new StringBuilder();
        fonts.clear();
        TextSpan[] textSpans = new TextSpan[spans.length];
        for (int i = 0; i < spans.length; i++) {
            fonts.put(sb.length(), spans[i].figure().font());
            sb.append(spans[i].text());
            textSpans[i] = new TextSpanImpl(spans[i]);
        }
        text = sb.toString();
        return textLayout.setContent(textSpans);
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
    public TextLine<Font>[] getLines() {
        com.sun.javafx.scene.text.TextLine[] lines = textLayout.getLines();
        TextLineImpl[] ret = new TextLineImpl[lines.length];
        int offset = 0;
        for (int i = 0; i < lines.length; i++) {
            com.sun.javafx.scene.text.TextLine line = lines[i];
            GlyphList[] runs = line.getRuns();
            GlyphRunImpl[] glyphRuns = new GlyphRunImpl[runs.length];
            for (int j = 0; j < runs.length; j++) {
                GlyphList run = runs[j];
                int start = offset;
                offset += run.getCharOffset(run.getGlyphCount());
                String str = text.substring(start, offset);
                Font font = fonts.ceilingEntry(start).getValue();
                glyphRuns[j] = new GlyphRunImpl(run, str, font);
            }
            ret[i] = new TextLineImpl(line, glyphRuns);
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
