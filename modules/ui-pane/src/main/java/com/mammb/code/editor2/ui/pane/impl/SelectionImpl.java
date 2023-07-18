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
import com.mammb.code.editor2.ui.pane.Selection;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * SelectionImpl.
 * @author Naotsugu Kobayashi
 */
public class SelectionImpl implements Selection {

    private int start;
    private int end;

    public SelectionImpl() {
        start = end = -1;
    }

    @Override
    public void start(int offset) {
        start = end = offset;
    }

    @Override
    public void clear() {
        start = end = -1;
    }

    @Override
    public int startOffset() {
        return start;
    }

    @Override
    public int endOffset() {
        return end;
    }

    @Override
    public boolean started() {
        return start > -1;
    }

    @Override
    public void to(int toOffset) {
        end = toOffset;
    }

    public void draw(GraphicsContext gc, TextRun run, double top) {
        if (!started()) throw new IllegalStateException();
        int runStart = run.offset();
        int runEnd = runStart + run.length();
        if (max() >= runStart && min() < runEnd) {
            double left = run.offsetToX().apply(Math.max(min(), runStart) - runStart);
            double width = run.offsetToX().apply(Math.min(max(), runEnd) - runStart) - left;
            gc.setFill(Color.LIGHTBLUE);
            gc.fillRect(left, top, width, run.textLine().height());
        }
    }

}

