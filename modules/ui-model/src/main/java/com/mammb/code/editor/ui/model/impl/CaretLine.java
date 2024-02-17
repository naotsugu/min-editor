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
import com.mammb.code.editor.ui.model.LayoutLine;
import com.mammb.code.editor.model.text.OffsetPointer;
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


    /**
     * Constructor.
     * @param bar the caret bar
     * @param offsetToLine the offset to layout line function
     */
    public CaretLine(CaretBar bar, Function<Long, LayoutLine> offsetToLine) {
        this.bar = Objects.requireNonNull(bar);
        this.offsetToLine = offsetToLine;
    }


    /**
     * Create a new CaretLine.
     * @param offsetToLine the offset to layout line function
     * @return a new CaretLine
     */
    public static CaretLine of(Function<Long, LayoutLine> offsetToLine) {
        return new CaretLine(CaretBar.of(), offsetToLine);
    }


    /**
     * Create a new CaretLine.
     * @param offsetToLine the offset to layout line function
     * @return a new CaretLine
     */
    public static CaretLine moonOf(Function<Long, LayoutLine> offsetToLine) {
        return new CaretLine(CaretBar.lightOf(), offsetToLine);
    }


    public void draw(GraphicsContext gc, double margin, double hScrolled) {
        bar.draw(gc, margin, hScrolled);
    }


    public void at(long charOffset) {
        long offset = Math.max(0, charOffset);
        line = offsetToLine.apply(offset);
        bar.offsetAt(line, offset);
    }

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
            line = offsetToLine.apply(offset);
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


    public void left() {

        if (line == null) {
            return;
        }
        if (bar.offset() == 0) {
            bar.syncX();
            return;
        }
        if (bar.offset() == line.offset()) {
            line = offsetToLine.apply(bar.offset() - 1);
            bar.offsetAt(line, bar.offset() - line.endMarkCount());
            return;
        }

        long offset = bar.offset() - 1;
        if (Character.isHighSurrogate(line.charAt(offset))) {
            offset--;
        }
        bar.offsetAt(line, offset);
    }


    public void up() {
        if (line == null) {
            return;
        }
        if (line.offsetPoint().row() == 0 && line.lineIndex() == 0) {
            return;
        }
        line = offsetToLine.apply(line.offset() - 1);
        bar.slipOn(line);
    }


    public void down() {
        if (line == null) {
            return;
        }
        if (line.isBottomLine()) {
            return;
        }
        line = offsetToLine.apply(line.tailOffset());
        bar.slipOn(line);
    }


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
            if (Character.isWhitespace(line.charAt(bar.offset()))) {
                while (bar.offset() < line.tailOffset() &&
                    Character.isWhitespace(line.charAt(bar.offset()))) {
                    right();
                }
            }
        }
    }


    public void end() {
        at(line.tailOffset() - line.endMarkCount());
    }


    public void refresh() {
        long offset = bar.offset();
        line = offsetToLine.apply(offset);
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
            return Long.compare(bar.offset(), caretLine.bar.offset());
        } else {
            return Long.compare(bar.offset(), o.offsetPoint().offset());
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

    LayoutLine getLine() {
        return line;
    }

    CaretBar getBar() {
        return bar;
    }

}
