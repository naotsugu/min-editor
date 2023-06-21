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
package com.mammb.code.editor.javafx.layout;

import com.mammb.code.editor2.model.layout.HitPosition;
import com.mammb.code.editor2.model.layout.Layout;
import com.mammb.code.editor2.model.layout.Span;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FxLayoutBuilder.
 * @author Naotsugu Kobayashi
 */
public class FxLayoutBuilder implements com.mammb.code.editor2.model.layout.LayoutBuilder {

    /** The delegated text layout. */
    private final TextLayout textLayout;

    /** The native font map. */
    private final Map<Font, Object> fonts = new HashMap<>();

    /** The spans. */
    private final List<Span> spans = new ArrayList<>();


    /**
     * Create a new Layout.
     */
    public FxLayoutBuilder() {
        this(-1);
    }


    /**
     * Create a new Layout.
     * @param wrapWidth the wrap width
     */
    public FxLayoutBuilder(double wrapWidth) {
        textLayout = Toolkit.getToolkit().getTextLayoutFactory().getLayout();
        textLayout.setTabSize(4);
        textLayout.setWrapWidth((float) wrapWidth);
    }


    @Override
    public void add(List<Span> spans) {
        this.spans.addAll(spans);
    }


    @Override
    public List<TextLine> layout() {

        record TextSpan(String getText, Object getFont, RectBounds getBounds, Span peer)
            implements com.sun.javafx.scene.text.TextSpan { }

        TextSpan[] textSpans = new TextSpan[spans.size()];
        for (int i = 0; i < spans.size(); i++) {
            Span span = spans.get(i);
            FxFontStyle fxStyle = (FxFontStyle) span.style();
            Object font = fonts.computeIfAbsent(fxStyle.font(), FontHelper::getNativeFont);
            textSpans[i] = new TextSpan(span.text(), font, null, span);
        }

        OffsetPoint point = textSpans.length > 0 ? spans.get(0).point() : OffsetPoint.zero;

        textLayout.setContent(textSpans);

        List<TextLine> textLines = new ArrayList<>();
        TextSpan currentSpan = null;
        int offset = 0;
        for (com.sun.javafx.scene.text.TextLine textLine : textLayout.getLines()) {
            List<TextRun> textRuns = new ArrayList<>();

            for (GlyphList glyphList : textLine.getRuns()) {

                Point2D location = glyphList.getLocation();
                RectBounds rectBounds = glyphList.getLineBounds();
                TextSpan textSpan = (TextSpan) glyphList.getTextSpan();

                double baselineOffset = -rectBounds.getMinY();
                Layout layout = Layout.of(
                    location.x, baselineOffset + location.y,
                    rectBounds.getWidth(), rectBounds.getHeight());

                if (currentSpan != textSpan) {
                    currentSpan = textSpan;
                    offset = 0;
                }

                int start = offset;
                offset += glyphList.getCharOffset(glyphList.getGlyphCount());
                if (start == offset) {
                    if (textSpan.getText().length() > offset &&
                        textSpan.getText().charAt(offset) == '\r') offset++;
                    if (textSpan.getText().length() > offset &&
                        textSpan.getText().charAt(offset) == '\n') offset++;
                }
                String runText = textSpan.getText().substring(start, offset);
                textRuns.add(TextRun.of(layout, runText, textSpan.peer()));
            }
            OffsetPoint p = OffsetPoint.of(
                textRuns.get(0).source().point().row(),
                point.offset(),
                point.cpOffset());
            TextLine line = TextLine.of(p, textRuns);
            point = point.plus(line.text());
            textLines.add(line);
        }
        return textLines;
    }


    @Override
    public void clear() {
        spans.clear();
    }


    @Override
    public HitPosition hitInfo(double x, double y) {
        TextLayout.Hit hit = textLayout.getHitInfo((float) x, (float) y);
        record HitPositionRecord(int charIndex, int insertionIndex, boolean leading) implements HitPosition { }
        return new HitPositionRecord(hit.getCharIndex(), hit.getInsertionIndex(), hit.isLeading());
    }

    public PathElement[] caretShape(int offset) {
        return textLayout.getCaretShape(offset, true, 0.0F, 0.0F);
    }

    public PathElement[] rangeShape(int start, int end) {
        return textLayout.getRange(start, end, 1, 0.0F, 0.0F);
    }

    @Override
    public boolean setWrapWidth(double wrapWidth) {
        return textLayout.setWrapWidth((float) wrapWidth);
    }


    @Override
    public boolean setLineSpacing(double spacing) {
        return textLayout.setLineSpacing((float) spacing);
    }


    @Override
    public boolean setTabSize(int spaces) {
        return textLayout.setTabSize(spaces);
    }

}