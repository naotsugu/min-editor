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

import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.text.OffsetPoint;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Caret.
 * @author Naotsugu Kobayashi
 */
public class Caret {

    /** The caret width. */
    private double width = 2;

    /** The caret (char) offset. */
    private int offset = 0;
    /** The caret row. */
    private int row = 0;
    /** The logical caret position x. */
    private double logicalX = 0;

    /** The text line. */
    private TextLine line = null;
    /** The caret position x. */
    private double x = 0;
    /** The caret position y. */
    private double y = 0;
    /** dirty?. */
    private boolean dirty = true;

    /** The offset to layout line function. */
    private Function<Integer, LayoutLine> offsetToLine;

    boolean drawn;

    /**
     * Constructor.
     * @param offsetToLine the offset to layout line function
     */
    public Caret(Function<Integer, LayoutLine> offsetToLine) {
        this.offsetToLine = offsetToLine;
    }


    /**
     * Draw caret,
     * @param gc the graphics context
     */
    public void draw(GraphicsContext gc) {
        if (ensureLayout() == null) return;
        double x1 = Math.max(width, x);
        double y1 = y + 1;
        double x2 = x1;
        double y2 = y + line.height() - 1;
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(width);
        gc.strokeLine(x1, y1, x2, y2);
        drawn = true;
    }


    /**
     * Clear.
     * @param gc the graphics context
     */
    public EditorModel.Rect clear(GraphicsContext gc) {
        if (ensureLayout() == null) return null;
        double dx = Math.max(x - width / 2, 0);
        double dy = y;
        double dw = width + 1;
        double dh = line.height();
        gc.clearRect(dx, dy, dw, dh);
        drawn = false;
        return new EditorModel.Rect(dx, dy, dw, dh);
    }


    /**
     * Mark the caret to dirty.
     */
    public void markDirty() {
        dirty = true;
        line = null;
        x = y = 0;
    }


    public void at(int charOffset, boolean syncLogicalX) {
        offset = charOffset;
        LayoutLine layoutLine = offsetToLine.apply(offset);
        if (layoutLine == null) {
            x = y = 0;
            row = 0;
            dirty = true;
        } else {
            x = (layoutLine.length() == 0) ? 0 : layoutLine.offsetToX(offset);
            y = layoutLine.offsetY();
            line = layoutLine;
            row = layoutLine.point().row();
            dirty = false;
            if (syncLogicalX) {
                logicalX = x;
            }
        }
    }


    /**
     * Move the caret to the right.
     */
    public void right() {

        if (ensureLayout() == null) return;

        if (offset == line.end() && line.endMarkCount() == 0) {
            // | a | b | $ |
            // | c||  offset:4  line.end():4  line.endMarkCount():0
            logicalX = x;
            return;
        }

        if (offset == line.end() - line.endMarkCount()) {
            // | a | b|| $ |      offset:2  line.end():3  line.endMarkCount():1
            // | a | b|| $ | $ |  offset:2  line.end():4  line.endMarkCount():2
            offset += line.endMarkCount();
            row++;
            logicalX = 0;
            markDirty();
            return;
        }

        offset++;
        if (offset < line.end() && Character.isLowSurrogate(line.charAt(offset))) {
            // | a|| b |  offset:1  line.end():2
            // | a | b||  offset:2  line.end():2
            offset++;
        }
        x = line.offsetToX(offset);
        logicalX = x;
    }


    /**
     * Move the caret to the left.
     */
    public void left() {
        if (offset == 0) return;
        if (ensureLayout() == null) return;

        offset--;
        if (offset < line.point().offset()) {
            row--;
            logicalX = 0;
            markDirty();
            ensureLayout();
            return;
        }
        if (Character.isHighSurrogate(line.charAt(offset))) {
            offset--;
        }
        x = line.offsetToX(offset);
        logicalX = x;
    }


    /**
     * Move the caret to the up.
     */
    public void up() {
        if (ensureLayout() == null) return;
        if (line.point().offset() == 0) {
            return;
        }
        LayoutLine prev = offsetToLine.apply(line.start() - 1);
        if (prev == null) return;

        line = prev;
        offset = line.xToOffset(logicalX);
        row = line.point().row();
        x = line.offsetToX(offset);
        y = prev.offsetY();
    }


    /**
     * Move the caret to the down.
     */
    public void down() {

        if (ensureLayout() == null) return;
        if (line.endMarkCount() == 0) return;

        LayoutLine next = offsetToLine.apply(line.end());
        if (next == null) return;
        line = next;
        offset = line.xToOffset(logicalX);
        row = line.point().row();
        x = line.offsetToX(offset);
        y = next.offsetY();
    }


    public OffsetPoint offsetPoint() {
        LayoutLine layoutLine = offsetToLine.apply(offset);
        if (layoutLine == null) return OffsetPoint.zero;
        return layoutLine.offsetPoint(offset);
    }

    public int offset() {
        return offset;
    }
    public int row() { return row; }
    public double x() {
        ensureLayout();
        return x;
    }
    public double y() {
        ensureLayout();
        return y;
    }
    public double y2() {
        ensureLayout();
        return y + line.height();
    }

    public boolean drawn() {
        return drawn;
    }

    private TextLine ensureLayout() {
        if (!dirty) return line;
        dirty = false;
        LayoutLine layoutLine = offsetToLine.apply(offset);
if (layoutLine == null) {
    Arrays.stream(Thread.currentThread().getStackTrace()).forEach(System.out::println);
}
        if (layoutLine == null) {
            x = y = 0;
        } else {
            x = layoutLine.offsetToX(offset);
            y = layoutLine.offsetY();
        }
        return line = layoutLine;
    }

}
