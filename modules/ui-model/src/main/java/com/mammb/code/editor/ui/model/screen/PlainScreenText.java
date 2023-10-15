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
import com.mammb.code.editor.model.layout.LayoutTranslate;
import com.mammb.code.editor.model.layout.LineLayout;
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.model.style.StyledText;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.model.text.TextualScroll;
import com.mammb.code.editor.model.text.Translate;
import com.mammb.code.editor.ui.model.ScreenText;
import com.mammb.code.editor.ui.prefs.Context;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PlainScreenText.
 * @author Naotsugu Kobayashi
 */
public class PlainScreenText implements ScreenText {

    /** The Context. */
    private final Context context;
    /** The edit buffer. */
    private final TextualScroll<Textual> scroll;
    /** The text translator. */
    private final Translate<Textual, TextLine> translator;
    /** The lines. */
    private final List<TextLine> lines = new LinkedList<>();
    /** The styling. */
    private final Translate<Textual, StyledText> styling;
    /** The count of rollup lines. */
    private int rollup = 0;
    /** The line of head. */
    private TextLine head = null;


    /**
     * Constructor.
     * @param context the context
     * @param scroll the edit buffer
     * @param styling the styling
     */
    public PlainScreenText(
            Context context,
            TextualScroll<Textual> scroll,
            Translate<Textual, StyledText> styling) {
        this.context = context;
        this.scroll = scroll;
        this.styling = styling;
        this.translator = translator(context, new FxLayoutBuilder(), styling);
    }


    public ScreenText asWrapped(double width) {
        return new WrapScreenText(context, scroll, styling, width);
    }


    @Override
    public List<TextLine> lines() {
        if (lines.isEmpty()) {
            for (Textual textual : scroll.texts()) {
                lines.add(translator.applyTo(textual));
            }
        }
        List<TextLine> ret = (rollup > 0)
            ? lines.subList(rollup, lines.size())
            : lines;
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

        if (n <= 0) {
            return 0;
        }

        List<Textual> added = scroll.prev(n);
        int size = added.size();
        if (size == 0) {
            return 0;
        }

        if (size >= scroll.pageSize()) {
            // if N exceeds the number of page lines, clear and add all.
            // forward scrolling can skip re-parsing syntax.
            added.subList(scroll.pageSize(), size).clear();
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

        if (n <= 0) {
            return 0;
        }

        List<Textual> added = scroll.next(n);
        int size = added.size();
        if (size < n) {
            rollup = Math.min(rollup + (n - size), lines.size() - scroll.pageSize() / 2);
        }
        if (size == 0) {
            return 0;
        }

        // delete rows to avoid inadvertently increasing the list size.
        lines.subList(0, Math.min(size, lines.size())).clear();

        List<TextLine> list = added.stream().map(translator::applyTo).collect(Collectors.toList());
        if (size > scroll.pageSize()) {
            list.subList(0, size - scroll.pageSize()).clear();
        }

        // add lines added by scrolling to the end.
        lines.addAll(list);

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
        return scroll.pageSize();
    }

    /**
     * Build the translator.
     * @return the translator
     */
    private Translate<Textual, TextLine> translator(
            Context ctx,
            LineLayout layout,
            Translate<Textual, StyledText> styling) {
        return styling.compound(FxSpanTranslate.of(
                    ctx.preference().fontName(),
                    ctx.preference().fontSize(),
                    ctx.preference().fgColor()))
            .compound(LayoutTranslate.of(layout));
    }

}
