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
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.ui.control.ScrollBar;
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
    /** The gutter. */
    private final Gutter gutter;
    /** The vertical scroll. */
    private final ScrollBar<Integer> vScroll;
    /** The horizontal scroll. */
    private final ScrollBar<Double> hScroll;
    /** The screen width. */
    private double width;
    /** The screen height. */
    private double height;
    /** The max width. */
    private double maxWidth = 0;
    private int pageSize;


    public ScreenScroll(
            Context context,
            ScrollBar<Integer> vScroll,
            ScrollBar<Double> hScroll,
            double width, double height,
            double maxWidth) {
        this.context = context;
        this.vScroll = vScroll;
        this.hScroll = hScroll;
        this.gutter = new GutterImpl(context);
        this.width = width;
        this.height = height;
        this.maxWidth = Math.max(maxWidth, width - gutter.width());
        this.pageSize = screenRowSize(height, context);
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        this.pageSize = screenRowSize(height, context);
        updateMaxWith(width - gutter.width());
    }

    public void initScroll(ScreenText texts, int rowCount) {
        adjustVScroll(texts, rowCount);
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());
        adjustHScroll(texts);
        hScroll.setValue(0.0);
    }

    public void adjustVScroll(ScreenText texts, int rowCount) {
        int lines = rowCount;
        if (texts instanceof WrapScreenText w) {
            lines += w.wrappedSize() - pageSize;
        }
        int adjustedMax = Math.max(0, lines - pageSize);
        double ratio = (double) adjustedMax / lines; // reduction ratio
        vScroll.setMax(adjustedMax);
        vScroll.setVisibleAmount((int) Math.floor(pageSize * ratio));
    }

    public void adjustHScroll(ScreenText texts) {
        if (texts instanceof PlainScreenText) {
            double w = Math.max(0, textAreaWidth());
            double adjustedMax = Math.max(0, maxWidth - w);
            double ratio = adjustedMax / maxWidth; // reduction ratio
            hScroll.setMax(adjustedMax);
            hScroll.setVisibleAmount(w * ratio);
        } else {
            hScroll.setMax(textAreaWidth());
            hScroll.setVisibleAmount(textAreaWidth());
        }
    }

    public void vScrolled(int value) {
        vScroll.setValue(value);
    }
    public void hScrolled(double value) {
        hScroll().setValue(Math.max(0, value));
    }


    public void setMaxWidth(double maxWidth) {
        this.maxWidth = Math.max(maxWidth, width - gutter.width());
    }

    public void resetMaxWidth() {
        maxWidth = width - gutter.width();
    }

    public void updateMaxWith(double candidateWidth) {
        if (candidateWidth > maxWidth) {
            maxWidth = candidateWidth;
        }
    }

    public double textLeft() {
        return gutter().width() - hScroll().getValue();
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

    public int pageSize() {
        return pageSize;
    }

    public Gutter gutter() {
        return gutter;
    }

    public ScrollBar<Integer> vScroll() {
        return vScroll;
    }

    public ScrollBar<Double> hScroll() {
        return hScroll;
    }

    public Rect textArea() {
        return new Rect(gutter.width(), 0, textAreaWidth(), height);
    }

    public double textAreaWidth() {
        return width - gutter.width();
    }


    static int screenRowSize(double height, Context context) {
        Font font = Font.font(context.preference().fontName(), context.preference().fontSize());
        double lineHeight = FxFonts.lineHeight(font) + TextLine.DEFAULT_MARGIN_TOP + TextLine.DEFAULT_MARGIN_BOTTOM;
        return (int) Math.ceil(height / lineHeight);
    }

}
