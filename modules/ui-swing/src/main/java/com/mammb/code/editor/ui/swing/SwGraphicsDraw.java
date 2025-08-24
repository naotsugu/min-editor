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
package com.mammb.code.editor.ui.swing;

import com.mammb.code.editor.core.Rgba;
import com.mammb.code.editor.ui.GraphicsDraw;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The swing graphics draw.
 * @author Naotsugu Kobayashi
 */
public class SwGraphicsDraw implements GraphicsDraw {

    /** The graphics. */
    private Graphics g;
    /** The cache of color. */
    private final Map<Rgba, Color> colors = new HashMap<>();

    @Override
    public void clearRect() {
        clearRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
    }

    @Override
    public void clearRect(double x, double y, double w, double h) {
        g.clearRect((int) x, (int) y, (int) w, (int) h);
    }

    @Override
    public void fillRect(Rgba color, double x, double y, double w, double h) {
        g.setColor(color(color));
        g.fillRect((int) x, (int) y, (int) w, (int) h);
    }

    @Override
    public void strokeLine(Rgba color, double lineWidth, double x1, double y1, double x2, double y2) {
        g.setColor(color(color));
        if (lineWidth == 1) {
            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        } else {
            Graphics2D g2d = (Graphics2D) g;
            Stroke org = g2d.getStroke();
            g2d.setStroke(new BasicStroke((float) lineWidth));
            g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            g2d.setStroke(org);
        }
    }

    @Override
    public void strokeRect(Rgba color, double lineWidth, double x, double y, double w, double h) {
        g.setColor(color(color));
        g.drawRect((int) x, (int) y, (int) w, (int) h);
    }

    @Override
    public void fillText(Rgba color, String text, double x, double y) {
        g.setColor(color(color));
        g.drawString(text, (int) x, (int) y);
    }

    @Override
    public void strokePolygon(Rgba color, double lineWidth, double[] xPoints, double[] yPoints, int nPoints) {
        final int[] xInts = new int[nPoints];
        final int[] yInts = new int[nPoints];
        for (int i = 0; i < nPoints; i++) {
            xInts[i] = (int) xPoints[i];
            yInts[i] = (int) yPoints[i];
        }
        g.setColor(color(color));
        if (lineWidth == 1) {
            g.drawPolyline(xInts, yInts, nPoints);
        } else {
            Graphics2D g2d = (Graphics2D) g;
            Stroke org = g2d.getStroke();
            g2d.setStroke(new BasicStroke((float) lineWidth));
            g2d.drawPolyline(xInts, yInts, nPoints);
            g2d.setStroke(org);
        }
    }

    @Override
    public void fillPolygon(Rgba color, double[] xPoints, double[] yPoints, int nPoints) {
        final int[] xInts = new int[nPoints];
        final int[] yInts = new int[nPoints];
        for (int i = 0; i < nPoints; i++) {
            xInts[i] = (int) xPoints[i];
            yInts[i] = (int) yPoints[i];
        }
        g.setColor(color(color));
        g.fillPolygon(xInts, yInts, nPoints);
    }

    @Override
    public void increaseFontSize(double sizeDelta) {
        if (sizeDelta == 0) {
            return;
        }
        Font old = g.getFont();
        int size = old.getSize() + (int) sizeDelta;
        if (size < 6) return;
        Font font = new Font(old.getFamily(), old.getStyle(), size);
        g.setFont(font);
    }

    private Color color(Rgba color) {
        return colors.computeIfAbsent(color, c -> c.as(a -> new Color(a[0], a[1], a[2], a[3])));
    }

}
