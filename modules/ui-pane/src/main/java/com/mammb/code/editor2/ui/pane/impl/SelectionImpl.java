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
package com.mammb.code.editor2.ui.pane.impl;

import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.ui.pane.Global;
import com.mammb.code.editor2.ui.pane.Selection;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * SelectionImpl.
 * @author Naotsugu Kobayashi
 */
public class SelectionImpl implements Selection {

    /** The selection open offset. */
    private OffsetPoint start;

    /** The selection close offset. */
    private OffsetPoint end;

    /** The selection dragging. */
    private boolean dragging = false;


    @Override
    public void start(OffsetPoint offset) {
        start = end = offset;
        dragging = false;
    }

    @Override
    public void to(OffsetPoint toOffset) {
        end = toOffset;
    }

    @Override
    public void startDragging(OffsetPoint offset) {
        start = end = offset;
        dragging = true;
    }

    @Override
    public void endDragging() {
        dragging = false;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void clear() {
        start = end = null;
        dragging = false;
    }

    @Override
    public OffsetPoint startOffset() {
        return start;
    }

    @Override
    public OffsetPoint endOffset() {
        return end;
    }

    @Override
    public boolean started() {
        return start != null;
    }

    @Override
    public void draw(GraphicsContext gc, TextRun run, double top, double left) {

        if (!started()) {
            throw new IllegalStateException();
        }

        int runStart = run.offset();
        int runEnd = runStart + run.length();

        if (max().offset() >= runStart && min().offset() < runEnd) {

            final String text = run.text();

            if (runEnd <= max().offset() &&
                ((text.length() == 1 && text.charAt(0) == '\n') ||
                 (text.length() == 2 && text.charAt(0) == '\r' && text.charAt(1) == '\n'))) {

                gc.setFill(Color.LIGHTBLUE);
                gc.fillRect(left - 0.5, top, Global.numberCharacterWidth(gc.getFont()), run.textLine().height());

            } else {

                double x1 = run.offsetToX().apply(Math.max(min().offset(), runStart) - runStart);
                double x2 = run.offsetToX().apply(Math.min(max().offset(), runEnd) - runStart);

                gc.setFill(Color.LIGHTBLUE);
                gc.fillRect(x1 + left, top, x2 - x1, run.textLine().height());

            }
        }

    }

}
