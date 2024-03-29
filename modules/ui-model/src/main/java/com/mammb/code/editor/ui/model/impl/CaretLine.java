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

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.OffsetPointer;
import com.mammb.code.editor.ui.model.LayoutLine;
import javafx.scene.canvas.GraphicsContext;
import java.util.Objects;
import java.util.function.Function;

/**
 * CaretLine.
 * @author Naotsugu Kobayashi
 */
public class CaretLine implements OffsetPointer {

    /** The offset to layout line function. */
    private final Function<Long, LayoutLine> offsetToLine;

    /** The caret bar. */
    private final CaretBar bar;

    /** The text line. */
    private LayoutLine line;

    /** The row index. */
    private int row;


    /**
     * Constructor.
     * @param bar the caret bar
     * @param offsetToLine the offset to layout line function
     * @param row the row index
     */
    public CaretLine(CaretBar bar, Function<Long, LayoutLine> offsetToLine, int row) {
        this.bar = Objects.requireNonNull(bar);
        this.offsetToLine = offsetToLine;
        this.row = row;
    }


    /**
     * Create a new CaretLine.
     * @param offsetToLine the offset to layout line function
     * @return a new CaretLine
     */
    public static CaretLine of(Function<Long, LayoutLine> offsetToLine) {
        return new CaretLine(CaretBar.of(), offsetToLine, 0);
    }


    /**
     * Create a new CaretLine.
     * @param offsetToLine the offset to layout line function
     * @return a new CaretLine
     */
    public static CaretLine moonOf(Function<Long, LayoutLine> offsetToLine) {
        return new CaretLine(CaretBar.lightOf(), offsetToLine, 0);
    }


    /**
     * Draw the caret.
     * @param gc the graphics context
     * @param margin the left position offset
     * @param hScrolled the size of horizontal scroll
     */
    public void draw(GraphicsContext gc, double margin, double hScrolled) {
        bar.draw(gc, margin, hScrolled);
    }


    /**
     * Moves the caret to the specified offset position.
     * @param charOffset the specified offset position
     */
    public void at(long charOffset) {
        long offset = Math.max(0, charOffset);
        line = layoutLineAt(offset);
        bar.offsetAt(line, offset);
    }


    /**
     * Move the caret to the right.
     */
    public void right() {

        if (line == null) {
            return;
        }

        if (bar.offset() == line.tailOffset() && line.endMarkCount() == 0) {
            // | a | b | $ |
            // | c||  offset:4  line.tailOffset():4  line.endMarkCount():0
            bar.syncX();
            return;
        }

        if (bar.offset() == line.tailOffset() - line.endMarkCount()) {
            // | a | b|| $ |      offset:2  line.tailOffset():3  line.endMarkCount():1
            // | a | b|| $ | $ |  offset:2  line.tailOffset():4  line.endMarkCount():2
            long offset = bar.offset() + line.endMarkCount();
            line = layoutLineAt(offset);
            bar.offsetAt(line, offset);
            return;
        }

        long offset = bar.offset() + 1;
        if (offset < line.tailOffset() && Character.isLowSurrogate(line.charAt(offset))) {
            // | a|| b |  offset:1  line.tailOffset():2
            // | a | b||  offset:2  line.tailOffset():2
            offset++;
        }
        bar.offsetAt(line, offset);
    }


    /**
     * Move the caret to the left.
     */
    public void left() {

        if (line == null) {
            return;
        }
        if (bar.offset() == 0) {
            bar.syncX();
            return;
        }
        if (bar.offset() == line.offset()) {
            line = layoutLineAt(bar.offset() - 1);
            bar.offsetAt(line, bar.offset() - line.endMarkCount());
            return;
        }

        long offset = bar.offset() - 1;
        if (Character.isHighSurrogate(line.charAt(offset))) {
            offset--;
        }
        bar.offsetAt(line, offset);
    }


    /**
     * Move the caret one line up.
     */
    public void up() {
        if (line == null) {
            return;
        }
        if (line.offsetPoint().row() == 0 && line.lineIndex() == 0) {
            return;
        }
        line = layoutLineAt(line.offset() - 1);
        bar.slipOn(line);
    }


    /**
     * Move the caret one line down.
     */
    public void down() {
        if (line == null) {
            return;
        }
        if (line.isBottomLine()) {
            return;
        }
        line = layoutLineAt(line.tailOffset());
        bar.slipOn(line);
    }


    /**
     * Move the caret to the head of the line.
     */
    public void home() {
        boolean skipWhitespace = true;
        if (line.offset() != bar.offset()) {
            if (bar.offset() - line.offset() < 36 &&
                line.text().substring(0, (int) (bar.offset() - line.offset())).trim().isEmpty()) {
                skipWhitespace = false;
            }
            at(line.offset());
        }
        if (skipWhitespace) {
            if (isWhite(line.charAt(bar.offset()))) {
                while (bar.offset() < line.tailOffset() &&
                    isWhite(line.charAt(bar.offset()))) {
                    right();
                }
            }
        }
    }


    /**
     * Move the caret to the end of the line.
     */
    public void end() {
        at(line.tailOffset() - line.endMarkCount());
    }


    public void refresh() {
        long offset = bar.offset();
        line = layoutLineAt(offset);
        bar.offsetAt(line, offset);
    }


    public String charAt() {
        if (line == null || line.containsTailOn(bar.offset())) {
            return "";
        }
        return line.charStringAt(bar.offset());
    }


    public CaretLine cloneAt(long offset) {
        CaretLine clone = moonOf(offsetToLine);
        clone.at(offset);
        return clone;
    }


    @Override
    public OffsetPoint offsetPoint() {
        if (bar.offset() == 0) {
            return OffsetPoint.zero;
        }
        return (line == null) ? null : line.offsetPoint(bar.offset());
    }


    @Override
    public int compareTo(OffsetPointer o) {
        if (o instanceof CaretLine caretLine) {
            return Long.compare(offset(), caretLine.offset());
        } else {
            return Long.compare(offset(), o.offsetPoint().offset());
        }
    }


    /**
     * Set the hide flag.
     * @param hide the hide flag
     */
    public void setHide(boolean hide) {
        bar.setHide(hide);
    }


    public boolean isHide() {
        return bar.isHide();
    }


    /**
     * Get the row index at the caret.
     * @return the row index at the caret.
     */
    public int row() {
        return row;
    }

    /**
     * Get the character offset at the caret.
     * @return the character offset at the caret
     */
    public long offset() {
        return bar.offset();
    }

    /**
     * Gets whether the caret is outside the drawing area.
     * @return {@code true} if the caret is outside the drawing area
     */
    public boolean isOffScreen() {
        return line == null;
    }

    /**
     * Get the layout line.
     * Returns {@code null} if the specified offset is out of screen area.
     * @param offset the character offset
     * @return the layout line
     */
    private LayoutLine layoutLineAt(long offset) {
        LayoutLine line = offsetToLine.apply(offset);
        if (line != null) {
            row = line.offsetPoint().row();
        }
        return line;
    }

    /**
     * Gets whether the specified character is white space or not.
     * @param ch the specified character
     * @return {@code true} if the specified character is white space
     */
    private static boolean isWhite(char ch) {
        return Character.isSpaceChar(ch) || ch == '\t';
    }

    LayoutLine getLine() {
        return line;
    }

    CaretBar getBar() {
        return bar;
    }

}
