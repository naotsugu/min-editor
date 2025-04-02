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
package com.mammb.code.editor.fx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import com.mammb.code.editor.core.Draw;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.Theme;
import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.SubText;
import com.mammb.code.editor.core.text.Text;

/**
 * The draw.
 * @author Naotsugu Kobayashi
 */
public class FxDraw implements Draw {

    /** The graphics context. */
    private final GraphicsContext gc;
    /** The font metrics. */
    private FontMetrics fontMetrics;
    /** The cache of color. */
    private final Map<String, Color> colors = new HashMap<>();

    /**
     * Constructor.
     * @param gc the graphics context
     * @param font the font
     */
    public FxDraw(GraphicsContext gc, Font font) {
        this.gc = gc;
        this.gc.setFont(font);
        this.fontMetrics = FxFontMetrics.of(font);
    }

    @Override
    public void clear() {
        Canvas canvas = gc.getCanvas();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @Override
    public void text(Text sourceText, double x, double y, double w, List<Style> styles) {
        var text = formatTab(sourceText);
        var bgColor = bgColor(styles);
        if (bgColor.isPresent()) {
            var bg = bgColor.get();
            gc.setFill(bgColor.get());
            gc.fillRect(x + 0.5, y + 0.5, w - 1, fontMetrics.getLineHeight() - 1);
            Color textColor = (bg.getBrightness() > 0.5)
                    ? textColor(styles).darker()
                    : textColor(styles).brighter();
            gc.setStroke(textColor);
            gc.setFill(textColor);
            gc.fillText(text, x, y + fontMetrics.getAscent());
        } else {
            Color textColor = textColor(styles);
            gc.setStroke(textColor);
            gc.setFill(textColor);
            gc.fillText(text, x, y + fontMetrics.getAscent());
        }
    }

    private Color textColor(List<Style> styles) {
        return color(styles.stream()
                .filter(Style.TextColor.class::isInstance)
                .map(Style.TextColor.class::cast)
                .findFirst()
                .map(Style.TextColor::colorString)
                .orElse(Theme.dark.fgColor()));
    }

    private Optional<Color> bgColor(List<Style> styles) {
        return styles.stream()
                .filter(Style.BgColor.class::isInstance)
                .map(Style.BgColor.class::cast)
                .findFirst()
                .map(Style.BgColor::colorString)
                .map(this::color);
    }

    @Override
    public void caret(double x, double y) {
        gc.setLineDashes(0);
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(1.5);
        gc.strokeLine(x - 1, y, x - 1, y + fontMetrics.getLineHeight());
    }

    @Override
    public void select(double x1, double y1, double x2, double y2, double l, double r) {
        double lineHeight = fontMetrics().getLineHeight();
        gc.setFill(color(Theme.dark.paleHighlightColor() + "AA"));
        if (y1 == y2) {
            gc.fillRect(Math.min(x1, x2), y1, Math.abs(x2 - x1), lineHeight);
            gc.setLineWidth(0.5);
            gc.setStroke(color(Theme.dark.faintColor()));
            gc.strokeRect(Math.min(x1, x2) - 0.25, y1 - 0.25, Math.abs(x2 - x1) + 0.5, lineHeight + 0.5);
            return;
        }
        //                    0:(x1, y1)
        //                     _______________________  1:(r, y1)
        // 6:(l, y1 + h) _____|                      |
        //               |   7:(x1, y1 + h)          |
        //               |                           |
        //               |     3:(x2, y2)  __________| 2:(r, y2)
        //               |________________|
        // 5:(l, y2 + h)                4:(x2, y2 + h)
        double[] x = new double[8];
        double[] y = new double[8];
        x[0] = x1; y[0]= y1;
        x[1] = r;  y[1]= y1;
        x[2] = r;  y[2]= y2;
        x[3] = x2; y[3]= y2;
        x[4] = x2; y[4]= y2 + lineHeight;
        x[5] = l;  y[5]= y2 + lineHeight;
        x[6] = l;  y[6]= y1 + lineHeight;
        x[7] = x1; y[7]= y1 + lineHeight;
        gc.fillPolygon(x, y, 8);
        gc.setLineWidth(0.5);
        gc.setStroke(color(Theme.dark.faintColor()));
        gc.strokePolygon(x, y, 8);
    }

    @Override
    public void underline(double x1, double y1, double x2, double y2, double wrapWidth) {
        double height = fontMetrics().getAscent();
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        if (y1 == y2) {
            gc.strokeLine(x1, y1 + height, x2, y1 + height);
        } else {
            // if line wrapped
            for (double y = y1; y <= y2; y += fontMetrics.getLineHeight()) {
                double xs = (y == y1) ? x1 : 0;
                double xe = (y == y2) ? x2 : wrapWidth;
                gc.strokeLine(xs, y + height, xe, y + height);
            }
        }
    }

    @Override
    public void hLine(double x, double y, double w) {
        gc.setStroke(color(Theme.dark.cautionColor()));
        gc.setLineWidth(2);
        gc.strokeLine(x, y, x + w, y);
    }

    @Override
    public void rect(double x, double y, double w, double h) {
        gc.setFill(color(Theme.dark.uiBaseColor()));
        gc.fillRect(x, y, w, h);
    }

    @Override
    public void increaseFontSize(double sizeDelta) {
        if (sizeDelta == 0) {
            return;
        }
        Font old = gc.getFont();
        double size = old.getSize() + sizeDelta;
        if (size < 6) return;
        Font font = Font.font(old.getFamily(), size);
        gc.setFont(font);
        fontMetrics = FxFontMetrics.of(font);
    }

    @Override
    public FontMetrics fontMetrics() {
        return fontMetrics;
    }

    @Override
    public void line(Line... lines) {
        gc.setLineWidth(1);
        for (Line line : lines) {
            gc.setStroke(color(line.color()));
            gc.strokeLine(line.x1(), line.y1(), line.x2(), line.y2());
        }
    }

    /**
     * Get the Color from the specified color string.
     * @param name the color string
     * @return the Color
     */
    private Color color(String name) {
        return colors.computeIfAbsent(name, Color::web);
    }

    /**
     * Format tabs.
     * @param source the source text
     * @return the formatted text
     */
    private String formatTab(Text source) {
        var tabs = new ArrayList<String>();
        var str = source.value();
        var advances = source.advances();
        for (int i = 0; i < str.length(); i++) {
            int index = str.indexOf("\t", i);
            if (index < 0) {
                break;
            }
            tabs.add(" ".repeat((int) (advances[index] / fontMetrics.standardCharWidth())));
            i = index;
        }
        for (String tab : tabs) {
            str = str.replaceFirst("\t", tab);
        }
        return str;
    }

}
