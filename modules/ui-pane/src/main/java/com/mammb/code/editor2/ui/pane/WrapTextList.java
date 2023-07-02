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
    private final Translate<Textual, List<TextLine>> translator = translator();
    /** The source un-wrapped lines. */
    private final List<TextLine> sourceLines = new LinkedList<>();

    /** The lines maybe wrapped. */
    private List<TextLine> lines;
    /** The offset of lines. */
    private int lineOffset = 0;

    /**
     * Constructor.
     * @param editBuffer the edit buffer
     */
    public WrapTextList(TextBuffer<Textual> editBuffer) {
        this.editBuffer = editBuffer;
    }


    @Override
    public List<TextLine> lines() {
        if (sourceLines.isEmpty()) {
            for (Textual textual : editBuffer.texts()) {
                lines.addAll(translator.applyTo(textual));
            }
            int size = Math.min(lines.size(), editBuffer.maxLineSize());
            lines = sourceLines.subList(0, size);
            lineOffset = 0;
        }
        return lines;
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
        if (lines.size() >= lineOffset + editBuffer.maxLineSize()) {
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
    public int at(int n) {
        return 0;
    }

    private static Translate<Textual, List<TextLine>> translator() {
        FxLayoutBuilder layout = new FxLayoutBuilder();
        return StylingTranslate.passThrough()
            .compound(SpanTranslate.of())
            .compound(LayoutWrapTranslate.of(layout));
    }


    private static int removeHeadRow(List<TextLine> lines, int n) {
        return removeRow(lines, n, true);
    }

    private static int removeTailRow(List<TextLine> lines, int n) {
        return removeRow(lines, n, false);
    }

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

}
