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
package com.mammb.code.editor.ui;

import com.mammb.code.editor.core.Draw;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.Rgba;
import com.mammb.code.editor.core.Theme;
import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.Text;
import java.util.ArrayList;
import java.util.List;

/**
 * The Draw implementation.
 * @author Naotsugu Kobayashi
 */
public class DrawImpl implements Draw {

    /** The graphics context. */
    private final GraphicsDraw gd;
    /** The font metrics. */
    private FontMetrics fontMetrics;

    /**
     * Constructor.
     * @param gd the graphics context
     */
    public DrawImpl(GraphicsDraw gd) {
        this.gd = gd;
        this.fontMetrics = gd.fontMetrics();
    }

    @Override
    public void clear() {
        gd.clear();
    }

    @Override
    public void text(Text sourceText, double x, double y, double w, List<Style> styles) {
        var text = formatTab(sourceText);
        Rgba textColor = Theme.current.fgColor();
        Rgba bgColor = null;
        Rgba underColor = null;
        Rgba aroundColor = null;
        for (Style style : styles) {
            switch (style) {
                case Style.TextColor s -> textColor = s.color();
                case Style.BgColor s -> bgColor = s.color();
                case Style.UnderColor s -> underColor = s.color();
                case Style.AroundSq s -> aroundColor = s.color();
            }
        }
        if (bgColor != null) {
            gd.fillRect(bgColor, x + 0.5, y + 0.5, w - 1, fontMetrics.getLineHeight() - 1);
        }
        if (underColor != null) {
            gd.strokeLine(underColor, 1.5, x, y + fontMetrics.getLineHeight() - 1, x + w, y + fontMetrics.getLineHeight() - 1);
        }
        if (aroundColor != null) {
            gd.strokeRect(aroundColor, 0.5, x, y, w, fontMetrics.getLineHeight());
        }

        gd.fillText(textColor, text, x, y + fontMetrics.getAscent());
    }

    @Override
    public void rect(double x, double y, double w, double h) {
        gd.fillRect(Theme.current.uiBaseColor(), x, y, w, h);
    }

    @Override
    public void caret(double x, double y) {
        gd.strokeLine(Rgba.ORANGE, 1.5, x - 1, y, x - 1, y + fontMetrics.getLineHeight());
    }

    @Override
    public void select(double x1, double y1, double x2, double y2, double l, double r) {
        double lineHeight = fontMetrics().getLineHeight();
        if (y1 == y2) {
            gd.fillRect(Theme.current.paleHighlightColor(), Math.min(x1, x2), y1, Math.abs(x2 - x1), lineHeight);
            gd.strokeRect(Theme.current.faintColor(), 0.5, Math.min(x1, x2) - 0.25, y1 - 0.25, Math.abs(x2 - x1) + 0.5, lineHeight + 0.5);
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
        gd.fillPolygon(Theme.current.paleHighlightColor(), x, y, 8);
        gd.strokePolygon(Theme.current.faintColor(), 0.5, x, y, 8);
    }

    @Override
    public void underline(double x1, double y1, double x2, double y2, double wrapWidth) {
        double height = fontMetrics().getAscent();
        if (y1 == y2) {
            gd.strokeLine(Rgba.LIGHTGRAY, 1, x1, y1 + height, x2, y1 + height);
        } else {
            // if line wrapped
            for (double y = y1; y <= y2; y += fontMetrics.getLineHeight()) {
                double xs = (y == y1) ? x1 : 0;
                double xe = (y == y2) ? x2 : wrapWidth;
                gd.strokeLine(Rgba.LIGHTGRAY, 1, xs, y + height, xe, y + height);
            }
        }
    }

    @Override
    public void hLine(double x, double y, double w) {
        gd.strokeLine(Theme.current.cautionColor().opaque(), 2, x, y, x + w, y);
    }

    @Override
    public void increaseFontSize(double sizeDelta) {
        gd.increaseFontSize(sizeDelta);
        fontMetrics = gd.fontMetrics();
    }

    @Override
    public FontMetrics fontMetrics() {
        return fontMetrics;
    }

    @Override
    public void line(Line... lines) {
        for (Line line : lines) {
            gd.strokeLine(line.color(), 1, line.x1(), line.y1(), line.x2(), line.y2());
        }
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

    protected GraphicsDraw gd() {
        return gd;
    }

}
