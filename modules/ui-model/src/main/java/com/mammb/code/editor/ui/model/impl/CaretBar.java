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

import com.mammb.code.editor.ui.model.LayoutLine;
import com.mammb.code.editor.ui.model.Rect;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * CaretBar.
 * @author Naotsugu Kobayashi
 */
public class CaretBar implements Rect {

    /** The stroke color of caret. */
    private Color strokeColor;

    /** The caret (char) offset. */
    private long offset = 0;

    /** The virtual position x. */
    private double virtualX = 0;

    /** The caret rect x. */
    private double x = 0;
    /** The caret rect y. */
    private double y = 0;
    /** The caret rect width. */
    private double w = 2;
    /** The caret rect height. */
    private double h = 0;


    /**
     * Constructor.
     * @param strokeColor the stroke color of caret
     */
    private CaretBar(Color strokeColor) {
        this.strokeColor = strokeColor;
    }


    /**
     * Create a new CaretBar.
     * @return a new CaretBar
     */
    public static CaretBar of() {
        return new CaretBar(Color.ORANGE);
    }


    /**
     * Create a new CaretBar with light color.
     * @return a new CaretBar
     */
    public static CaretBar lightOf() {
        return new CaretBar(Color.color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.7));
    }


    /**
     * Draw this caret.
     * @param gc the GraphicsContext
     * @param margin the left position offset
     * @param hScrolled the size of horizontal scroll
     */
    public void draw(GraphicsContext gc, double margin, double hScrolled) {
        if (x - hScrolled < 0 || y < 0 || h <= 0) return;
        double dx = x - hScrolled + margin;
        gc.setLineDashes(0);
        gc.setStroke(strokeColor);
        gc.setLineWidth(w);
        gc.strokeLine(dx, top() + 1, dx, bottom() - 1);
    }


    /**
     * Moves to the specified offset.
     * @param line the row to which the offset belongs
     * @param offset the specified offset
     */
    public void offsetAt(LayoutLine line, long offset) {
        this.offset = offset;
        if (line == null) {
            x = y = h = virtualX = 0;
        } else {
            x = virtualX = line.offsetToX(offset);
            y = line.offsetY();
            h = line.leadingHeight();
        }
    }


    /**
     * Slip to the specified line with fixed virtual X position.
     * @param line the specified line
     */
    public void slipOn(LayoutLine line) {
        if (line == null) {
            x = y = h = 0;
        } else {
            offset = line.xToOffset(virtualX);
            x = line.offsetToX(offset);
            y = line.offsetY();
            h = line.leadingHeight();
        }
    }


    /**
     * Scroll vertically.
     * @param delta the delta position
     */
    public void vScroll(double delta) {
        y += delta;
    }


    /**
     * Sync x position.
     */
    public void syncX() {
        virtualX = x;
    }

    /**
     * Get the character offset.
     * @return the character offset
     */
    public long offset() {
        return offset;
    }

    /**
     * Get the caret virtual x.
     * @return the caret virtual x
     */
    public double virtualX() {
        return virtualX;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double width() {
        return w;
    }

    @Override
    public double height() {
        return h;
    }

}
