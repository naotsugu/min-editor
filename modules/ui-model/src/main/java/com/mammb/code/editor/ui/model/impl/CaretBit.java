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
import com.mammb.code.editor.ui.model.Rect;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.function.Function;

/**
 * Caret implementation.
 * @author Naotsugu Kobayashi
 */
public class CaretBit {

    /** The caret state type. */
    private enum State { READY, DIRTY, PAGE_OUT; }

    /** The offset to layout line function. */
    private final Function<Long, LayoutLine> offsetToLine;

    /** The caret state. */
    private State state = State.DIRTY;

    /** The stroke color of caret. */
    private Color strokeColor = Color.ORANGE;

    /** The caret (char) offset. */
    private long offset = 0;

    /** The logical caret position x. */
    private double logicalX = 0;

    /** The text line. */
    private LayoutLine line = null;

    /** The caret rect. */
    private Rect rect = null;


    public CaretBit(Function<Long, LayoutLine> offsetToLine) {
        this.offsetToLine = offsetToLine;
    }


    public void draw(GraphicsContext gc, double margin, double hScrolled) {

        if (state == State.DIRTY) layout();

        if (state == State.PAGE_OUT || (rect.x() - hScrolled) < 0) {
            return;
        }

        double x = rect.x() - hScrolled + margin;
        gc.setLineDashes(0);
        gc.setStroke(strokeColor);
        gc.setLineWidth(rect.w());
        gc.strokeLine(x, rect.y() + 1, x, rect.y2() - 1);

    }


    public void markDirty() {
        state = State.DIRTY;
    }


    public void at(long charOffset, boolean syncLogicalX) {
        offset = charOffset;
        layout();
        if (syncLogicalX && state == State.READY) {
            logicalX = rect.x();
        }
    }


    public void right() {

        if (state == State.DIRTY) layout();

        if (state == State.PAGE_OUT) return;

        if (offset == line.tailOffset() && line.endMarkCount() == 0) {
            // | a | b | $ |
            // | c||  offset:4  line.tailOffset():4  line.endMarkCount():0
            logicalX = rect.x();
            return;
        }

        if (offset == line.tailOffset() - line.endMarkCount()) {
            // | a | b|| $ |      offset:2  line.tailOffset():3  line.endMarkCount():1
            // | a | b|| $ | $ |  offset:2  line.tailOffset():4  line.endMarkCount():2
            offset += line.endMarkCount();
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
        rect = lineToRect();
        logicalX = rect.x();

    }


    public void left() {

        if (offset == 0) {
            return;
        }

        if (state == State.DIRTY) layout();

        if (state == State.PAGE_OUT) return;

        offset--;
        if (offset < line.point().offset()) {
            if (offsetToLine.apply(offset).endMarkCount() > 1) {
                offset--;
            }
            layout();
            if (state == State.READY) {
                logicalX = rect.x();
            }
            return;
        }

        if (Character.isHighSurrogate(line.charAt(offset))) {
            offset--;
        }
        rect = lineToRect();
        logicalX = rect.x();

    }


    public void up() {

        if (offset == 0) {
            return;
        }

        if (state == State.DIRTY) layout();
        if (state == State.PAGE_OUT) return;

        LayoutLine prev = offsetToLine.apply(line.offset() - 1);
        if (prev != null) {
            line = prev;
            offset = line.xToOffset(logicalX);
            rect = lineToRect();
        }
    }


    public void down() {

        if (state == State.DIRTY) layout();
        if (state == State.PAGE_OUT) return;
        if (line.isBottomLine()) return;

        LayoutLine next = offsetToLine.apply(line.tailOffset());
        if (next != null) {
            line = next;
            offset = line.xToOffset(logicalX);
            rect = lineToRect();
        }
    }


    public OffsetPoint caretPoint() {
        if (offset == 0) {
            return OffsetPoint.zero;
        }
        if (state == State.DIRTY) {
            layout();
        }
        return (state == State.PAGE_OUT) ? null : line.offsetPoint(offset);

    }


    private void layout() {

        line = offsetToLine.apply(offset);

        if (line == null) {
            rect = null;
            state = State.PAGE_OUT;
        } else {
            rect = lineToRect();
            state = State.READY;
        }

    }

    private Rect lineToRect() {
        return new Rect(
            line.offsetToX(offset),
            line.offsetY(),
            2,
            line.leadingHeight()
        );
    }

}
