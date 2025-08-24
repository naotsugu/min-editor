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
package com.mammb.code.editor.ui.fx;

import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.Rgba;
import com.mammb.code.editor.ui.GraphicsDraw;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * The fx graphics draw.
 * @author Naotsugu Kobayashi
 */
public class FxGraphicsDraw implements GraphicsDraw {

    /** The graphics context. */
    private final GraphicsContext gc;

    /** The cache of color. */
    private final Map<Rgba, Color> colors = new HashMap<>();

    /**
     * Constructor.
     * @param gc the graphics context
     */
    public FxGraphicsDraw(GraphicsContext gc) {
        this.gc = gc;
    }

    @Override
    public void clear() {
        clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    @Override
    public void clearRect(double x, double y, double w, double h) {
        gc.clearRect(x, y, w, h);
    }

    @Override
    public void fillRect(Rgba color, double x, double y, double w, double h) {
        gc.setFill(color(color));
        gc.fillRect(x, y, w, h);
    }

    @Override
    public void strokeLine(Rgba color, double lineWidth, double x1, double y1, double x2, double y2) {
        gc.setStroke(color(color));
        gc.setLineWidth(lineWidth);
        gc.strokeLine(x1, y1, x2, y2);
    }

    @Override
    public void strokeRect(Rgba color, double lineWidth, double x, double y, double w, double h) {
        gc.setStroke(color(color));
        gc.setLineWidth(lineWidth);
        gc.strokeRect(x, y, w, h);
    }

    @Override
    public void fillText(Rgba color, String text, double x, double y) {
        gc.setFill(color(color));
        gc.fillText(text, x, y);
    }

    @Override
    public void strokePolygon(Rgba color, double lineWidth, double[] xPoints, double[] yPoints, int nPoints) {
        gc.setLineWidth(lineWidth);
        gc.setStroke(color(color));
        gc.strokePolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void fillPolygon(Rgba color, double[] xPoints, double[] yPoints, int nPoints) {
        gc.setFill(color(color));
        gc.fillPolygon(xPoints, yPoints, nPoints);
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
    }

    @Override
    public FontMetrics fontMetrics() {
        return FxFontMetrics.of(gc.getFont(), gc.getFontSmoothingType());
    }

    private Color color(Rgba color) {
        return colors.computeIfAbsent(color, c -> c.as(
            a -> Color.rgb(a[0], a[1], a[2], (double) a[3] / (double) 255.0F)));
    }

}
