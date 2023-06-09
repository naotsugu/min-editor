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
import com.mammb.code.editor2.model.layout.LayoutWrapTranslate;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.style.StylingTranslate;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.text.Translate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * WrapTextList.
 * @author Naotsugu Kobayashi
 */
public class WrapTextList implements TextList {

    /** The edit buffer. */
    private final TextBuffer<Textual> editBuffer;
    /** The text translator. */
    private final Translate<Textual, List<TextLine>> translator;
    /** The styling. */
    private final StylingTranslate styling;

    /** The lines maybe wrapped. */
    private final List<TextLine> lines = new LinkedList<>();
    /** The offset of lines. */
    private int lineOffset = 0;


    /**
     * Constructor.
     * @param editBuffer the edit buffer
     */
    public WrapTextList(TextBuffer<Textual> editBuffer) {
        this(editBuffer, StylingTranslate.passThrough(), -1);
    }


    /**
     * Constructor.
     * @param editBuffer the edit buffer
     * @param styling the styling
     * @param wrapWidth the wrap width
     */
    public WrapTextList(TextBuffer<Textual> editBuffer, StylingTranslate styling, double wrapWidth) {
        this.editBuffer = editBuffer;
        this.styling = styling;
        this.translator = translator(wrapWidth, styling);
    }


    public TextList asLinear() {
        return new LinearTextList(editBuffer, styling);
    }

    @Override
    public List<TextLine> lines() {
        if (lines.isEmpty()) {
            for (Textual textual : editBuffer.texts()) {
                lines.addAll(translator.applyTo(textual));
            }
            lineOffset = 0;
        }
        int toIndex = lineOffset + editBuffer.maxLineSize();
        return lines.subList(lineOffset, Math.min(toIndex, lines.size()));
    }

    @Override
    public void markDirty() {
        lines.clear();
    }

    @Override
    public int prev(int n) {
        if (n <= 0) return 0;
        int count = 0;
        for (int i = 0; i < n; i++) {
            int ret = prev();
            if (ret == 0) return count;
            count += ret;
        }
        return count;
    }


    private int prev() {
        if (lineOffset == 0) {
            List<Textual> added = editBuffer.prev(1);
            int size = added.size();
            if (size == 0) return 0;

            removeTailRow(lines, 1);
            List<TextLine> list = translator.applyTo(added.get(0));
            lines.addAll(0, list);
            lineOffset = list.size() - 1;
        } else {
            lineOffset--;
        }
        return 1;
    }


    @Override
    public int next(int n) {
        if (n <= 0) return 0;
        int count = 0;
        for (int i = 0; i < n; i++) {
            int ret = next();
            if (ret == 0) return count;
            count += ret;
        }
        return count;
    }


    private int next() {
        if (lines.size() <= lineOffset + editBuffer.maxLineSize()) {
            List<Textual> added = editBuffer.next(1);
            int size = added.size();
            if (size == 0) return 0;

            int removedCount = removeHeadRow(lines, 1);
            List<TextLine> list = translator.applyTo(added.get(0));
            lines.addAll(list);
            // TODO if it is the last line, add an empty TextLine
            lineOffset -= removedCount;
        }
        lineOffset++;
        return 1;
    }

    @Override
    public boolean scrollAt(int row, int offset) {

        List<TextLine> visibleLines = lines();
        int start = visibleLines.get(0).start();
        int end = visibleLines.get(visibleLines.size() - 1).end();
        if (start <= offset && offset < end) {
            return false;
        }

        final int first = lines.get(0).point().row();
        final int last = lines.get(lines.size() - 1).point().row();
        if (first > row || row > last) {
            if (row < first) {
                int nRow = first - row;
                List<Textual> added = editBuffer.prev(nRow);
                if (added.size() >= editBuffer.maxLineSize()) {
                    added.subList(editBuffer.maxLineSize(), added.size()).clear();
                }
                removeTailRow(lines, added.size());
                List<TextLine> list = added.stream().map(translator::applyTo)
                    .flatMap(Collection::stream).toList();
                lines.addAll(0, list);
            } else {
                int nRow = row - last;
                List<Textual> added = editBuffer.next(nRow);
                int removedCount = removeHeadRow(lines, added.size());
                List<TextLine> list = added.stream().map(translator::applyTo)
                    .flatMap(Collection::stream).toList();
                removeAfterRow(list, removedCount);
                lines.addAll(list);
            }
        }

        lineOffset = 0;
        while (true) {
            start = visibleLines.get(0).start();
            end = visibleLines.get(visibleLines.size() - 1).end();
            if (start <= offset && offset < end) {
                return true;
            }
            int ret = next();
            if (ret == 0) return true;
        }
    }

    @Override
    public int capacity() {
        return editBuffer.maxLineSize();
    }

    private static Translate<Textual, List<TextLine>> translator(
        double wrapWidth, StylingTranslate styling) {
        FxLayoutBuilder layout = new FxLayoutBuilder(wrapWidth);
        return styling.compound(SpanTranslate.of())
            .compound(LayoutWrapTranslate.of(layout));
    }


    /**
     *
     * @param lines
     * @param n
     * @return the number of removed row
     */
    private static int removeHeadRow(List<TextLine> lines, int n) {
        return removeRow(lines, n, true);
    }

    /**
     *
     * @param lines
     * @param n
     * @return the number of removed row
     */
    private static int removeTailRow(List<TextLine> lines, int n) {
        return removeRow(lines, n, false);
    }

    /**
     *
     * @param lines
     * @param n
     * @param asc
     * @return the number of removed row
     */
    private static int removeRow(List<TextLine> lines, int n, boolean asc) {

        if (n <= 0) return 0;

        int size = lines.size();

        if (n >= size) {
            lines.clear();
            return size;
        }

        int count = 0;
        int prev = -1;
        while (true) {
            int index = asc ? 0 : lines.size() - 1;
            TextLine line = lines.get(index);
            if (prev != -1 && prev != line.point().row()) {
                n--;
            }
            if (n == 0) break;
            prev = line.point().row();
            lines.remove(index);
            count++;
        }
        return count;
    }


    private static void removeAfterRow(List<TextLine> lines, int n) {

        if (n <= 0) return;

        int size = lines.size();
        if (n >= size) {
            return;
        }

        int count = 0;
        int prev = -1;
        for (TextLine line : lines) {
            if (prev != -1 && prev != line.point().row()) {
                n--;
            }
            if (n == 0) break;
            prev = line.point().row();
            count++;
        }
        lines.subList(count, lines.size()).clear();
    }

}
