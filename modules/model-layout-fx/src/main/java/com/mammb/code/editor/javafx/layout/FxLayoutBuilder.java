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

import com.mammb.code.editor2.model.layout.*;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * FxLayoutBuilder.
 * @author Naotsugu Kobayashi
 */
public class FxLayoutBuilder implements LineLayout {

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


    public void add(List<Span> spans) {
        this.spans.addAll(spans);
    }


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

        com.sun.javafx.scene.text.TextLine[] lines = textLayout.getLines();
        for (int i = 0; i < lines.length; i++) {
            com.sun.javafx.scene.text.TextLine textLine = lines[i];

            List<TextRun> textRuns = new ArrayList<>();
            for (com.sun.javafx.text.TextRun run : (com.sun.javafx.text.TextRun[]) textLine.getRuns()) {

                Point2D location = run.getLocation();
                RectBounds rectBounds = run.getLineBounds();
                if (currentSpan != run.getTextSpan()) {
                    currentSpan = (TextSpan) run.getTextSpan();
                    offset = 0;
                }

                double baseline = -rectBounds.getMinY();
                Layout layout = Layout.of(
                    location.x, baseline + location.y,
                    rectBounds.getWidth(), rectBounds.getHeight());

                Function<Integer, Float> offsetToX = off -> run.getXAtOffset(off, true) + location.x;
                Function<Double, Integer> xToOffset = x -> run.getOffsetAtX(x.floatValue(), new int[1]);

                textRuns.add(TextRun.of(
                    layout,
                    offset,
                    run.getLength(),
                    currentSpan.peer(),
                    offsetToX,
                    xToOffset));
                offset += run.getLength();
            }

            OffsetPoint offsetPoint = OffsetPoint.of(
                textRuns.get(0).source().point().row(),
                point.offset(),
                point.cpOffset());
            TextLine line = TextLine.of(
                offsetPoint,
                textLine.getLength(),
                textLine.getBounds().getHeight(),
                textRuns);
            point = point.plus(line.text());
            textLines.add(line);
        }
        return textLines;
    }


    public void clear() {
        spans.clear();
    }


    public HitPosition hitInfo(double x, double y) {
        TextLayout.Hit hit = textLayout.getHitInfo((float) x, (float) y);
        record HitPositionRecord(int charIndex, int insertionIndex, boolean leading) implements HitPosition { }
        return new HitPositionRecord(hit.getCharIndex(), hit.getInsertionIndex(), hit.isLeading());
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

    @Override
    public List<TextLine> layout(Span span) {
        clear();
        add(List.of(span));
        return layout();
    }

}
