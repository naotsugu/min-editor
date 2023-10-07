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
package com.mammb.code.editor.ui.model.impl;

import com.mammb.code.editor.javafx.layout.FxFonts;
import com.mammb.code.editor.model.buffer.TextBuffer;
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.ui.control.ScrollBar;
import com.mammb.code.editor.ui.model.Caret;
import com.mammb.code.editor.ui.model.Rect;
import com.mammb.code.editor.ui.model.ScreenText;
import com.mammb.code.editor.ui.model.screen.PlainScreenText;
import com.mammb.code.editor.ui.model.screen.WrapScreenText;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.scene.text.Font;

/**
 * ScreenScroll.
 * @author Naotsugu Kobayashi
 */
public class ScreenScroll {

    /** The Context. */
    private final Context context;
    /** The text buffer. */
    private final TextBuffer<Textual> buffer;
    /** The gutter. */
    private final Gutter gutter;
    /** The text list. */
    private ScreenText texts;

    /** The vertical scroll. */
    private ScrollBar<Integer> vScroll;
    /** The horizontal scroll. */
    private ScrollBar<Double> hScroll;

    /** The screen width. */
    private double width;
    /** The screen height. */
    private double height;
    /** The max width. */
    private double maxWidth = 0;


    public ScreenScroll(
            Context context,
            TextBuffer<Textual> buffer,
            ScreenText texts,
            Gutter gutter,
            double width, double height,
            ScrollBar<Integer> vScroll,
            ScrollBar<Double> hScroll) {
        this.context = context;
        this.buffer = buffer;
        this.texts = texts;
        this.gutter = gutter;
        this.vScroll = vScroll;
        this.hScroll = hScroll;
        this.width = width;
        this.height = height;
        init();
    }


    public void bindTexts(ScreenText texts) {
        this.texts = texts;
        init();
    }

    public void init() {
        adjustVScroll();
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());

        adjustHScroll();
        hScroll.setValue(0.0);

        if (texts instanceof PlainScreenText) {
            maxWidth = texts.lines().stream().mapToDouble(TextLine::width).max().orElse(width - gutter.width());
        }
    }

    public Rect textArea() {
        return new Rect(gutter.width(), 0, width - gutter.width(), height);
    }

    public void resetMaxWidth() {
        maxWidth = width - gutter.width();
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public double maxWidth() {
        return maxWidth;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        this.buffer.setPageSize(screenRowSize(height));
        texts.markDirty();
        if (texts instanceof WrapScreenText wrap) {
            wrap.setWrapWidth(textArea().w());
        }
        adjustVScroll();
        adjustHScroll();
    }
    public double hScrollValue() {
        return hScroll.getValue();
    }

    private double textLeft() {
        return gutter.width() - hScroll.getValue();
    }

    public void adjustVScroll() {
        int lines = buffer.metrics().rowCount();
        if (texts instanceof WrapScreenText w) {
            lines += w.wrappedSize() - buffer.pageSize();
        }
        int adjustedMax = Math.max(0, lines - buffer.pageSize());
        double ratio = (double) adjustedMax / lines; // reduction ratio
        vScroll.setMax(adjustedMax);
        vScroll.setVisibleAmount((int) Math.floor(buffer.pageSize() * ratio));
    }


    public void adjustHScroll() {
        if (texts instanceof PlainScreenText) {
            double w = Math.max(0, width - gutter.width());
            double adjustedMax = Math.max(0, maxWidth - w);
            double ratio = adjustedMax / maxWidth; // reduction ratio
            hScroll.setMax(adjustedMax);
            hScroll.setVisibleAmount(w * ratio);
        } else {
            hScroll.setMax(width - gutter.width());
            hScroll.setVisibleAmount(width - gutter.width());
        }
    }

    public void updateMaxWith(double candidateWidth) {
        if (candidateWidth > maxWidth) {
            maxWidth = candidateWidth;
        }
    }

    int screenRowSize(double height) {
        return screenRowSize(height, context);
    }

    static int screenRowSize(double height, Context context) {
        Font font = Font.font(context.preference().fontName(), context.preference().fontSize());
        double lineHeight = FxFonts.lineHeight(font) + TextLine.DEFAULT_MARGIN_TOP + TextLine.DEFAULT_MARGIN_BOTTOM;
        return (int) Math.ceil(height / lineHeight);
    }

    public void vScrollToCaret(Caret caret) {
        boolean scrolled = texts.scrollAt(caret.row(), caret.offset());
        if (!scrolled) return;
        texts.markDirty();
        caret.markDirty();
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());
    }
    public void hScrollToCaret(Caret caret) {
        if (texts instanceof WrapScreenText) return;
        adjustHScroll();
        double gap = width / 10;
        if (caret.x() <= hScroll.getValue()) {
            hScroll.setValue(Math.max(0, caret.x() - gap));
            return;
        }
        double right = hScroll.getValue() + (width - gutter.width());
        if (caret.x() + gap >= right) {
            double delta = caret.x() + gap - right;
            hScroll.setValue(hScroll.getValue() + delta);
        }
    }

}
