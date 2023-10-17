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
package com.mammb.code.editor.ui.model.screen;

import com.mammb.code.editor.javafx.layout.FxLayoutBuilder;
import com.mammb.code.editor.javafx.layout.FxSpanTranslate;
import com.mammb.code.editor.model.layout.LayoutWrapTranslate;
import com.mammb.code.editor.model.layout.LineLayout;
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.model.style.StyledText;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.model.text.TextualScroll;
import com.mammb.code.editor.model.text.Translate;
import com.mammb.code.editor.ui.model.ScreenText;
import com.mammb.code.editor.ui.prefs.Context;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * WrapScreenText.
 * @author Naotsugu Kobayashi
 */
public class WrapScreenText implements ScreenText {

    /** The Context. */
    private final Context context;
    /** The textual scroll. */
    private final TextualScroll<Textual> scroll;
    /** The text translator. */
    private final Translate<Textual, List<TextLine>> translator;
    /** The styling. */
    private final Translate<Textual, StyledText> styling;
    /** The LineLayout. */
    private final LineLayout layout = new FxLayoutBuilder();

    /** The lines maybe wrapped. */
    private final List<TextLine> lines = new LinkedList<>();
    /** The offset of lines. */
    private int lineOffset = 0;
    /** The count of rollup lines. */
    private int rollup = 0;

    private TextLine head = null;


    /**
     * Constructor.
     * @param context the context
     * @param scroll the textual scroll
     * @param styling the styling
     * @param wrapWidth the wrap width
     */
    public WrapScreenText(
            Context context,
            TextualScroll<Textual> scroll,
            Translate<Textual, StyledText> styling,
            double wrapWidth) {
        this.context = context;
        this.scroll = scroll;
        this.styling = styling;
        this.translator = translator(context, layout, wrapWidth, styling);
    }


    public ScreenText asPlain() {
        return new PlainScreenText(context, scroll, styling);
    }


    @Override
    public List<TextLine> lines() {
        if (lines.isEmpty()) {
            for (Textual textual : scroll.texts()) {
                lines.addAll(translator.applyTo(textual));
            }
        }
        int toIndex = lineOffset + scroll.pageSize();

        List<TextLine> ret = lines.subList(lineOffset + rollup, Math.min(toIndex, lines.size()));
        head = ret.get(0);
        return ret;
    }

    @Override
    public TextLine head() {
        if (lines.isEmpty()) lines();
        return head;
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
            List<Textual> added = scroll.prev(1);
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
    public int next(final int n) {

        if (n <= 0) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < n; i++) {
            int ret = next();
            if (ret == 0) {
                rollup = Math.min(rollup + (n - i), scroll.pageSize() / 2);
                return count;
            }
            count += ret;
        }
        return count;
    }


    private int next() {
        if (lines.size() <= lineOffset + scroll.pageSize()) {
            List<Textual> added = scroll.next(1);
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
        int start = visibleLines.get(0).offset();
        int end = visibleLines.get(visibleLines.size() - 1).tailOffset();
        if (start <= offset && offset < end) {
            return false;
        }

        final int first = lines.get(0).point().row();
        final int last = lines.get(lines.size() - 1).point().row();
        if (first > row || row > last) {
            if (row < first) {
                int nRow = first - row;
                List<Textual> added = scroll.prev(nRow);
                if (added.size() >= scroll.pageSize()) {
                    added.subList(scroll.pageSize(), added.size()).clear();
                }
                removeTailRow(lines, added.size());
                List<TextLine> list = added.stream().map(translator::applyTo)
                    .flatMap(Collection::stream).toList();
                lines.addAll(0, list);
            } else {
                int nRow = row - last;
                List<Textual> added = scroll.next(nRow);
                int removedCount = removeHeadRow(lines, added.size());
                List<TextLine> list = added.stream().map(translator::applyTo)
                    .flatMap(Collection::stream).toList();
                removeAfterRow(list, removedCount);
                lines.addAll(list);
            }
        }

        lineOffset = 0;
        while (true) {
            start = visibleLines.get(0).offset();
            end = visibleLines.get(visibleLines.size() - 1).tailOffset();
            if (start <= offset && offset < end) {
                return true;
            }
            int ret = next();
            if (ret == 0) return true;
        }
    }

    @Override
    public int pageSize() {
        return scroll.pageSize();
    }

    @Override
    public void setPageSize(int size) {
        scroll.setPageSize(size);
    }

    public int wrappedSize() {
        if (lines.isEmpty()) {
            lines();
        }
        return lines.size();
    }

    public void setWrapWidth(double wrapWidth) {
        layout.setWrapWidth(wrapWidth);
    }

    /**
     * Build the translator.
     * @return the translator
     */
    private Translate<Textual, List<TextLine>> translator(
            Context ctx,
            LineLayout layout,
            double wrapWidth,
            Translate<Textual, StyledText> styling) {
        layout.setWrapWidth(wrapWidth);
        return styling.compound(FxSpanTranslate.of(
                ctx.preference().fontName(),
                ctx.preference().fontSize(),
                ctx.preference().fgColor()))
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
