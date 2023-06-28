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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.function.Function;

public class Caret {

    /** The caret (char) offset */
    private int offset = 0;
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

    private Function<Integer, LayoutLine> offsetToLine;


    public Caret(Function<Integer, LayoutLine> offsetToLine) {
        this.offsetToLine = offsetToLine;
    }


    public void draw(GraphicsContext gc) {
        if (ensureLayout() == null) return;
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(x == 0 ? 4 : 2);
        gc.strokeLine(x, y, x, y + line.height());
        gc.restore();
    }


    public void markDirty() {
        dirty = true;
        line = null;
        x = y = 0;
    }


    public void right() {
        if (ensureLayout() == null) return;

        logicalX = 0;
        offset++;
        if (offset >= line.end()) {
            markDirty();
            return;
        }
        if (Character.isLowSurrogate(line.charAt(offset))) {
            offset++;
        }
        x = line.offsetToX(offset);
        logicalX = x;
    }


    public void left() {
        if (ensureLayout() == null) return;

        logicalX = 0;
        offset--;
        if (offset < line.point().offset()) {
            markDirty();
            return;
        }
        if (Character.isHighSurrogate(line.charAt(offset))) {
            offset--;
        }
        x = line.offsetToX(offset);
        logicalX = x;
    }


    public void up() {
        if (ensureLayout() == null) return;
        if (line.point().offset() == 0) {
            return;
        }
        LayoutLine prev = offsetToLine.apply(line.start() - 1);
        if (prev == null) return;

        line = prev;
        offset = line.xToOffset(logicalX);
        x = line.offsetToX(offset);
        y = prev.offsetY();
    }


    public void down() {
        if (ensureLayout() == null) return;
        // TODO return if the last line
        LayoutLine next = offsetToLine.apply(line.end());
        if (next == null) return;
        line = next;
        offset = line.xToOffset(logicalX);
        x = line.offsetToX(offset);
        y = next.offsetY();
    }


    private TextLine ensureLayout() {
        if (!dirty) return line;
        dirty = false;
        LayoutLine layoutLine = offsetToLine.apply(offset);
        if (layoutLine == null) return null;
        x = layoutLine.offsetToX(offset);
        y = layoutLine.offsetY();
        return line = layoutLine;
    }

}
