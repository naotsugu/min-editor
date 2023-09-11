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
package com.mammb.code.editor.ui.pane;

import com.mammb.code.editor.javafx.layout.FxFontMetrics;
import com.mammb.code.editor.model.layout.FontMetrics;
import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.stream.IntStream;

/**
 * Gutter.
 * @author Naotsugu Kobayashi
 */
public class Gutter {

    private Font font;
    private Color color;
    private double chWidth;
    private double width;
    private boolean widthChanged;

    public Gutter(Context ctx) {
        this.font = Font.font(ctx.preference().fontName(), ctx.preference().fontSize());
        this.chWidth = numberCharacterWidth(font);
        this.width = Math.ceil(chWidth * 5);
        this.widthChanged = false;
        this.color = ctx.preference().colorScheme().isDark()
            ? Color.web(ctx.preference().fgColor()).darker().darker()
            : Color.web(ctx.preference().fgColor()).brighter().brighter();
    }


    public void draw(GraphicsContext gc, TextRun run, double top, double lineHeight) {

        if (run.textLine().lineIndex() > 0) {
            gc.clearRect(0, top, width - 0.5, lineHeight);
            return;
        }

        String num = String.valueOf(run.source().point().row() + 1);
        growWidthIf(num);

        gc.clearRect(0, top, width - 0.5, lineHeight);

        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFont(font);
        gc.setFill(color);
        gc.fillText(num, width - chWidth, top + run.baseline());
        gc.setTextAlign(TextAlignment.LEFT);

    }

    /**
     * Get the width of gutter.
     * @return the width of gutter
     */
    public double width() {
        return width;
    }

    public boolean checkWidthChanged() {
        if (widthChanged) {
            widthChanged = false;
            return true;
        } else {
            return false;
        }
    }


    public void setFont(Font font) {
        this.font = font;
        this.chWidth = numberCharacterWidth(font);
        this.widthChanged = true;
    }


    private void growWidthIf(String num) {
        double w = Math.ceil((num.length() + 2) * chWidth);
        if (w > width) {
            width = w;
            widthChanged = true;
        }
    }

    /**
     * Get the maximum unit width of a number character when drawn in the specified font.
     * @param font the specified font
     * @return the unit width
     */
    private static double numberCharacterWidth(Font font) {
        FontMetrics<Font> fontMetrics = new FxFontMetrics(font);
        return IntStream.rangeClosed('0', '9')
            .mapToDouble(c -> fontMetrics.getCharWidth((char) c))
            .max().orElse(0.0);
    }

}
