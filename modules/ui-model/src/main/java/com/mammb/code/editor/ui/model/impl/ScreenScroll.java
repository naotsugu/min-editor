/*
 * Copyright 2023-2024 the original author or authors.
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
import com.mammb.code.editor.ui.model.Rect;
import com.mammb.code.editor.ui.model.ScrollBar;
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
    /** The page line size. */
    private int pageLineSize;
    /** The horizontal scroll enabled. */
    private boolean hScrollEnabled = true;


    /**
     * Constructor.
     * @param context the Context
     * @param vScroll the vertical scroll
     * @param hScroll the horizontal scroll
     * @param width the screen width
     * @param height the screen height
     */
    public ScreenScroll(
            Context context,
            ScrollBar<Integer> vScroll,
            ScrollBar<Double> hScroll,
            double width, double height) {
        this.context = context;
        this.vScroll = vScroll;
        this.hScroll = hScroll;
        this.gutter = new GutterImpl(context);
        this.width = width;
        this.height = height;
        this.maxWidth = width - gutter.width();
        this.pageLineSize = screenRowSize(height, context);
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        this.pageLineSize = screenRowSize(height, context);
        updateMaxWith(width - gutter.width());
    }

    public void initScroll(int headlinesIndex, int totalLines) {
        adjustVScroll(totalLines);
        vScroll.setValue(headlinesIndex);
        adjustHScroll();
        hScroll.setValue(0.0);
    }

    public void adjustVScroll(int totalLines) {
        int adjustedMax = Math.max(0, totalLines - pageLineSize);
        double ratio = (double) adjustedMax / totalLines; // reduction ratio
        vScroll.setMax(adjustedMax);
        vScroll.setVisibleAmount((int) Math.floor(pageLineSize * ratio));
    }

    public void adjustHScroll() {
        if (hScrollEnabled) {
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

    public void hScrollTo(double caretX) {
        if (!hScrollEnabled) return;
        adjustHScroll();
        double gap = width / 10;
        if (caretX <= hScrollValue()) {
            hScrolled(caretX - gap);
            return;
        }
        double right = (width - gutter.width()) + hScrollValue();
        if (caretX + gap >= right) {
            double delta = caretX + gap - right;
            hScrolled(hScrollValue() + delta);
        }
    }

    public void syncScroll(int headlinesIndex, int totalLines, double caretX) {
        adjustVScroll(totalLines);
        vScrolled(headlinesIndex);
        hScrollTo(caretX);
    }

    public void vScrolled(int value) {
        vScroll.setValue(value);
    }

    public void hScrolled(double value) {
        hScroll.setValue(hScrollEnabled ? Math.max(0, value) : 0);
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

    public void sethScrollEnabled(boolean hScrollEnabled) {
        this.hScrollEnabled = hScrollEnabled;
    }

    public double textLeft() {
        return gutter.width() - hScroll.getValue();
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public int pageLineSize() {
        return pageLineSize;
    }

    public Gutter gutter() {
        return gutter;
    }

    public double hScrollValue() {
        return hScroll.getValue();
    }

    public Rect textArea() {
        return new Rect(gutter.width(), 0, width - gutter.width(), height);
    }

    static int screenRowSize(double height, Context context) {
        Font font = Font.font(context.preference().fontName(), context.preference().fontSize());
        double lineHeight = FxFonts.lineHeight(font) + TextLine.DEFAULT_MARGIN_TOP + TextLine.DEFAULT_MARGIN_BOTTOM;
        return (int) Math.ceil(height / lineHeight);
    }

}
