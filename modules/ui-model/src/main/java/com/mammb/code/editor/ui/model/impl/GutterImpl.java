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
import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Gutter.
 * @author Naotsugu Kobayashi
 */
public class GutterImpl implements Gutter {

    /** The font. */
    private Font font;
    /** The text color. */
    private final Color color;
    /** The background color. */
    private final Color background;
    /** The background accent color. */
    private final Color backgroundAccent = Color.web("#C0C0C020");;
    /** The width of a character. */
    private double chWidth;
    /** The width of the gutter. */
    private double width;
    /** width changed?. */
    private boolean widthChanged;


    /**
     * Constructor.
     * @param ctx the context
     */
    public GutterImpl(Context ctx) {
        this.font = Font.font(ctx.preference().fontName(), ctx.preference().fontSize());
        this.chWidth = FxFonts.numberCharacterWidth(font);
        this.width = Math.ceil(chWidth * 5);
        this.widthChanged = false;
        this.color = ctx.preference().colorScheme().isDark()
            ? Color.web(ctx.preference().fgColor()).darker().darker()
            : Color.web(ctx.preference().fgColor()).brighter().brighter();
        this.background = Color.web(ctx.preference().bgColor());
    }


    @Override
    public void draw(GraphicsContext gc, TextRun run, double top, boolean accent) {

        if (run.textLine().lineIndex() > 0) {
            // 1 | aaa
            // 2 | bbb_bbb_bbb_bbb_
            //   | bbb               <- run.textLine().lineIndex() > 0
            fillBackground(gc, top, run.textLine().leadingHeight(), accent);
            return;
        }

        String num = String.valueOf(run.source().offsetPoint().row() + 1);
        growWidthIf(num);

        fillBackground(gc, top, run.textLine().leadingHeight(), accent);

        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFont(font);
        gc.setFill(color);
        gc.fillText(num, width - chWidth, top + run.layoutHeight());

        gc.setTextAlign(TextAlignment.LEFT);

    }


    @Override
    public double width() {
        return width;
    }


    @Override
    public boolean checkWidthChanged() {
        if (widthChanged) {
            widthChanged = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set the font
     * @param font the font
     */
    public void setFont(Font font) {
        this.font = font;
        this.chWidth = FxFonts.numberCharacterWidth(font);
        this.widthChanged = true;
    }


    /**
     * Fill gutter background.
     * @param gc the graphics context
     * @param top the position of top
     * @param lineHeight the line height
     * @param accent whether to display accents
     */
    private void fillBackground(GraphicsContext gc, double top, double lineHeight, boolean accent) {
        gc.setFill(background);
        gc.fillRect(0, top, width - 0.5, lineHeight);
        if (accent) {
            gc.setFill(backgroundAccent);
            gc.fillRect(0, top, width - 0.5, lineHeight);
        }
    }


    /**
     * Expand the width of the gutter.
     * @param num the line number
     */
    private void growWidthIf(String num) {
        double w = Math.ceil((num.length() + 2) * chWidth);
        if (w > width) {
            width = w;
            widthChanged = true;
        }
    }

}
