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
package com.mammb.code.editor2.ui.pane;

import com.mammb.code.editor.javafx.layout.FxLayoutBuilder;
import com.mammb.code.editor.javafx.layout.SpanTranslate;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.layout.LayoutTranslate;
import com.mammb.code.editor2.model.layout.LineLayout;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.style.StylingTranslate;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.text.Translate;

import java.util.LinkedList;
import java.util.List;

/**
 * LinearTextList.
 * @author Naotsugu Kobayashi
 */
public class LinearTextList implements TextList {

    /** The edit buffer. */
    private final TextBuffer<Textual> buffer;
    /** The text translator. */
    private final Translate<Textual, TextLine> translator;
    /** The lines. */
    private final List<TextLine> lines = new LinkedList<>();
    /** The styling. */
    private final Translate<Textual, StyledText> styling;

    /** The LineLayout. */
    private final LineLayout layout = new FxLayoutBuilder();
    /** The count of rollup lines. */
    private int rollup = 0;

    private TextLine top = null;

    /** The high water width. */
    private double highWaterWidth = 0;


    /**
     * Constructor.
     * @param buffer the edit buffer
     */
    public LinearTextList(TextBuffer<Textual> buffer) {
        this(buffer, StylingTranslate.passThrough());
    }


    /**
     * Constructor.
     * @param buffer the edit buffer
     * @param styling the styling
     */
    public LinearTextList(TextBuffer<Textual> buffer, Translate<Textual, StyledText> styling) {
        this.buffer = buffer;
        this.styling = styling;
        this.translator = translator(layout, styling, passThroughTrace());
    }


    public TextList asWrapped(double width) {
        return new WrapTextList(buffer, styling, width);
    }

    @Override
    public List<TextLine> lines() {
        if (lines.isEmpty()) {
            for (Textual textual : buffer.texts()) {
                lines.add(translator.applyTo(textual));
            }
        }
        List<TextLine> ret = (rollup > 0) ? lines.subList(rollup, lines.size()): lines;
        top = ret.get(0);
        return ret;
    }

    @Override
    public TextLine top() {
        if (lines.isEmpty()) lines();
        return top;
    }

    @Override
    public void markDirty() {
        lines.clear();
    }


    @Override
    public int prev(int n) {

        if (rollup > 0) {
            if (rollup >= n) {
                rollup -= n;
                return n;
            } else {
                n -= rollup;
                rollup = 0;
            }
        }

        if (n <= 0) return 0;

        List<Textual> added = buffer.prev(n);
        int size = added.size();
        if (size == 0) {
            return 0;
        }

        if (size >= buffer.maxLineSize()) {
            // if N exceeds the number of page lines, clear and add all.
            // forward scrolling can skip re-parsing syntax.
            added.subList(buffer.maxLineSize(), size).clear();
            lines.clear();
            lines.addAll(added.stream().map(translator::applyTo).toList());
        } else {
            lines.subList(lines.size() - size, lines.size()).clear();
            List<TextLine> list = added.stream().map(translator::applyTo).toList();
            lines.addAll(0, list);
        }
        return size;
    }


    @Override
    public int next(int n) {

        if (n <= 0) return 0;

        List<Textual> added = buffer.next(n);
        int size = added.size();
        if (size < n) {
            rollup = Math.min(rollup + (n - size), buffer.maxLineSize() / 2);
        }
        if (size == 0) {
            return 0;
        }

        // delete rows to avoid inadvertently increasing the list size.
        lines.subList(0, Math.min(size, lines.size())).clear();

        List<TextLine> list = added.stream().map(translator::applyTo).toList();
        if (size > buffer.maxLineSize()) {
            list.subList(0, size - buffer.maxLineSize()).clear();
        }

        // add lines added by scrolling to the end.
        lines.addAll(list);
        // TODO if it is the last line, add an empty TextLine

        return size;
    }


    @Override
    public boolean scrollAt(int row, int offset) {
        final int head = head().point().row();
        final int tail = tail().point().row();
        if (head <= row && row <= tail) {
            return false;
        }
        if (row < head) {
            return prev(head - row) > 0;
        } else {
            return next(row - tail) > 0;
        }
    }


    @Override
    public int capacity() {
        return buffer.maxLineSize();
    }

    @Override
    public LineLayout lineLayout() {
        return layout;
    }

    /**
     * Get the high water width.
     * @return the high water width
     */
    public double highWaterWidth() {
        return highWaterWidth;
    }


    /**
     * Build the translator.
     * @return the translator
     */
    private static Translate<Textual, TextLine> translator(
            LineLayout layout, Translate<Textual, StyledText> styling,
            Translate<TextLine, TextLine> passThrough) {
        return styling.compound(SpanTranslate.of())
                      .compound(LayoutTranslate.of(layout))
                      .compound(passThrough);
    }


    private Translate<TextLine, TextLine> passThroughTrace() {
        return textLine -> {
            if (highWaterWidth < textLine.width()) {
                highWaterWidth = textLine.width();
            }
            return textLine;
        };
    }

}
