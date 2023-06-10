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

import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * LayoutBuilder.
 * @author Naotsugu Kobayashi
 */
public class LayoutBuilder {

    /** The delegated text layout. */
    private final TextLayout textLayout;

    /** The native font map. */
    private final TreeMap<Integer, Font> fonts = new TreeMap<>();

    /** The spans. */
    private final List<Span> spans = new ArrayList<>();

    /** The text of spans. */
    private final StringBuilder text = new StringBuilder();


    /**
     * Create a new Layout.
     */
    public LayoutBuilder() {
        textLayout = Toolkit.getToolkit().getTextLayoutFactory().getLayout();
        textLayout.setTabSize(4);
    }


    /**
     * Add spans.
     * @param spans the adding spans
     */
    public void add(List<Span> spans) {
        this.spans.addAll(spans);
        spans.stream().map(Span::text).forEach(text::append);
    }


    /**
     * Layout.
     * @return
     */
    public List<TextLine> layout() {
        record TextSpan(String getText, Object getFont, RectBounds getBounds, Span peer)
            implements com.sun.javafx.scene.text.TextSpan { }
        TextSpan[] textSpans = new TextSpan[spans.size()];
        for (int i = 0; i < spans.size(); i++) {
            Span span = spans.get(i);
            textSpans[i] = new TextSpan(span.text(), span.style().font(), null, span);
        }

        textLayout.setContent(textSpans);

        List<TextLine> textLines = new ArrayList<>();
        int offset = 0;
        for (com.sun.javafx.scene.text.TextLine textLine : textLayout.getLines()) {
            List<TextRun> textRuns = new ArrayList<>();
            for (GlyphList glyphList : textLine.getRuns()) {

                Point2D location = glyphList.getLocation();
                RectBounds rectBounds = glyphList.getLineBounds();
                TextSpan textSpan = (TextSpan) glyphList.getTextSpan();
                int endOffset = glyphList.getCharOffset(glyphList.getGlyphCount());

                double baselineOffset = -rectBounds.getMinY();
                Layout layout = Layout.of(
                    location.x, baselineOffset + location.y,
                    rectBounds.getWidth(), rectBounds.getHeight());

                String runText = text.substring(offset, endOffset);
                offset = endOffset;

                Style style = textSpan.peer().style();

                textRuns.add(TextRun.of(layout, runText, style));
            }
            textLines.add(TextLine.of(textRuns));
        }
        return textLines;
    }


    /**
     * Clear.
     */
    public void clear() {
        spans.clear();
        text.setLength(0);
    }


    /**
     * Get the hit information in the text plane.
     * @param x the specified point x to be tested
     * @param y the specified point y to be tested
     * @return the hit position
     */
    public HitPosition hitInfo(float x, float y) {
        return HitPosition.of(textLayout.getHitInfo(x, y));
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

}
