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

import com.mammb.code.editor.javafx.layout.FxFontMetrics;
import com.mammb.code.editor.javafx.layout.FxFontStyle;
import com.mammb.code.editor2.model.layout.TextRun;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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


    public Gutter() {
        var style = FxFontStyle.of();
        setFont(style.font());
        color = style.color();
        width = Math.ceil(chWidth * 5);
        widthChanged = false;
    }


    public void draw(GraphicsContext gc, TextRun run, double top, double lineHeight) {

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
        this.chWidth = characterWidth(font);
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
    private static double characterWidth(Font font) {
        double w = 0;
        var metrics = new FxFontMetrics(font);
        for (char c = '0'; c <= '9'; c++) {
            double s = metrics.getCharWidth(c);
            if (s > w) w = s;
        }
        return w;
    }

}
