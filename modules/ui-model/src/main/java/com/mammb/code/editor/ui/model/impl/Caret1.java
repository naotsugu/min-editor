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

import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.ui.model.Caret;
import com.mammb.code.editor.ui.model.LayoutLine;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.function.Function;

import static java.lang.System.Logger.Level.DEBUG;

/**
 * Caret implementation.
 * @author Naotsugu Kobayashi
 */
public class Caret1 implements Caret {

    /** logger. */
    private static final System.Logger log = System.getLogger(Caret.class.getName());

    /** The caret width. */
    private final double width = 2;
    /** The offset to layout line function. */
    private final Function<Long, LayoutLine> offsetToLine;

    /** The caret (char) offset. */
    private long offset = 0;
    /** The caret row. */
    private int row = 0;
    /** The logical caret position x. */
    private double logicalX = 0;

    /** The text line. */
    private LayoutLine line = null;
    /** The caret position x. */
    private double x = 0;
    /** The caret position y. */
    private double y = 0;
    /** dirty?. */
    private boolean dirty = true;


    /**
     * Constructor.
     * @param offsetToLine the offset to layout line function
     */
    public Caret1(Function<Long, LayoutLine> offsetToLine) {
        this.offsetToLine = offsetToLine;
    }


    @Override
    public void draw(GraphicsContext gc, double margin, double hScrolled) {
        if (ensureLayout() == null) return;
        if ((x - hScrolled) < 0) {
        } else {
            drawCaretAt(gc, x - hScrolled + margin, y, line.leadingHeight());
        }
    }


    private void drawCaretAt(GraphicsContext gc, double x, double top, double height) {
        gc.setLineDashes(0);
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(width);
        gc.strokeLine(x, top + 1, x, top + height - 1);
    }


    @Override
    public void markDirty() {
        dirty = true;
        line = null;
        x = y = 0;
    }


    @Override
    public void at(long charOffset, boolean syncLogicalX) {
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
            row = layoutLine.offsetPoint().row();
            dirty = false;
            if (syncLogicalX) {
                logicalX = x;
            }
        }
    }


    @Override
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


    @Override
    public void left() {

        if (offset == 0 || ensureLayout() == null) {
            return;
        }

        offset--;
        if (offset < line.offsetPoint().offset()) {
            row--;
            if (offsetToLine.apply(offset).endMarkCount() > 1) {
                offset--;
            }
            markDirty();
            ensureLayout();
            logicalX = x;
            return;
        }
        if (Character.isHighSurrogate(line.charAt(offset))) {
            offset--;
        }
        x = line.offsetToX(offset);
        logicalX = x;
    }


    @Override
    public void up() {
        if (ensureLayout() == null) return;
        if (line.offsetPoint().offset() == 0) {
            return;
        }
        LayoutLine prev = offsetToLine.apply(line.offset() - 1);
        if (prev == null) return;

        line = prev;
        offset = line.xToOffset(logicalX);
        row = line.offsetPoint().row();
        x = line.offsetToX(offset);
        y = prev.offsetY();
    }


    @Override
    public void down() {

        if (ensureLayout() == null || line.isBottomLine()) {
            return;
        }

        LayoutLine next = offsetToLine.apply(line.tailOffset());
        if (next == null) {
            return;
        }
        line = next;
        offset = line.xToOffset(logicalX);
        row = line.offsetPoint().row();
        x = line.offsetToX(offset);
        y = next.offsetY();
    }

    @Override
    public void home() {

    }

    @Override
    public void end() {

    }


    @Override
    public OffsetPoint offsetPoint() {
        if (offset == 0) {
            return OffsetPoint.zero;
        }
        LayoutLine line = offsetToLine.apply(offset);
        return (line == null) ? null : line.offsetPoint(offset);
    }


    @Override
    public long offset() {
        return offset;
    }


    @Override
    public int row() { return row; }


    @Override
    public double x() {
        ensureLayout();
        return x;
    }


    @Override
    public double y() {
        ensureLayout();
        return y;
    }


    @Override
    public double y2() {
        ensureLayout();
        return y + line.leadingHeight();
    }


    @Override
    public double height() {
        ensureLayout();
        return line.leadingHeight();
    }


    @Override
    public double width() {
        return width;
    }


    @Override
    public LayoutLine layoutLine() {
        ensureLayout();
        return line;
    }

    private TextLine ensureLayout() {
        if (!dirty) return line;
        dirty = false;
        LayoutLine layoutLine = offsetToLine.apply(offset);
        if (layoutLine == null) {
            log.log(DEBUG, "offset:{0}", offset);
            x = y = 0;
        } else {
            x = layoutLine.offsetToX(offset);
            y = layoutLine.offsetY();
        }
        return line = layoutLine;
    }

}
