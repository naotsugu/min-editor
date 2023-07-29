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
import java.lang.System.Logger;
import java.util.function.Function;

import static java.lang.System.Logger.Level.INFO;

/**
 * Caret.
 * @author Naotsugu Kobayashi
 */
public class Caret {

    /** logger. */
    private static final Logger log = System.getLogger(Caret.class.getName());

    /** The caret width. */
    private final double width = 2;
    /** The side bearing. */
    private final double sideBearing;
    /** The offset to layout line function. */
    private final Function<Integer, LayoutLine> offsetToLine;

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
    /** drawn. */
    private boolean drawn;


    /**
     * Constructor.
     * @param offsetToLine the offset to layout line function
     */
    public Caret(Function<Integer, LayoutLine> offsetToLine, double sideBearing) {
        this.offsetToLine = offsetToLine;
        this.sideBearing = sideBearing;
    }


    /**
     * Draw caret,
     * @param gc the graphics context
     */
    public void draw(GraphicsContext gc) {
        if (ensureLayout() == null) return;
        drawCaretAt(gc, x, y, line.height());
        drawn = true;
    }


    public void flushDraw(GraphicsContext gc) {
        drawCaretAt(gc, x, y, line.height());
    }

    private void drawCaretAt(GraphicsContext gc, double x, double top, double height) {
        gc.setLineDashes(0);
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(width);
        gc.strokeLine(x + sideBearing, top + 1, x + sideBearing, top + height - 1);
    }

    /**
     * Clear.
     * @param gc the graphics context
     */
    public EditorModel.Rect clear(GraphicsContext gc) {
        if (ensureLayout() == null) return null;
        double dx = x;
        double dy = y;
        double dw = width + sideBearing;
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
            x = (layoutLine.isEmpty()) ? 0 : layoutLine.offsetToX(offset);
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

        if (offset == line.tailOffset() && line.endMarkCount() == 0) {
            // | a | b | $ |
            // | c||  offset:4  line.tailOffset():4  line.endMarkCount():0
            logicalX = x;
            return;
        }

        if (offset == line.tailOffset() - line.endMarkCount()) {
            // | a | b|| $ |      offset:2  line.tailOffset():3  line.endMarkCount():1
            // | a | b|| $ | $ |  offset:2  line.tailOffset():4  line.endMarkCount():2
            offset += line.endMarkCount();
            row++;
            logicalX = 0;
            markDirty();
            return;
        }

        offset++;
        if (offset < line.tailOffset() && Character.isLowSurrogate(line.charAt(offset))) {
            // | a|| b |  offset:1  line.tailOffset():2
            // | a | b||  offset:2  line.tailOffset():2
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
        LayoutLine prev = offsetToLine.apply(line.offset() - 1);
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

        LayoutLine next = offsetToLine.apply(line.tailOffset());
        if (next == null) return;
        line = next;
        offset = line.xToOffset(logicalX);
        row = line.point().row();
        x = line.offsetToX(offset);
        y = next.offsetY();
    }


    /**
     * Get the offsetPoint at caret.
     * @return the offsetPoint at caret
     */
    public OffsetPoint offsetPoint() {
        LayoutLine layoutLine = offsetToLine.apply(offset);
        if (layoutLine == null) return OffsetPoint.zero;
        return layoutLine.offsetPoint(offset);
    }

    /**
     * Get the char offset.
     * @return the char offset
     */
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
    public double height() {
        ensureLayout();
        return line.height();
    }

    public double width() {
        return width;
    }

    public boolean drawn() {
        return drawn;
    }

    private TextLine ensureLayout() {
        if (!dirty) return line;
        dirty = false;
        LayoutLine layoutLine = offsetToLine.apply(offset);
        if (layoutLine == null) {
            log.log(INFO, "offset:{0}", offset);
            x = y = 0;
        } else {
            x = layoutLine.offsetToX(offset);
            y = layoutLine.offsetY();
        }
        return line = layoutLine;
    }

}
