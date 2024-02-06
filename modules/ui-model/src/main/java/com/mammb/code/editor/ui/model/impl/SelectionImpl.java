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

import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.OffsetPointRange;
import com.mammb.code.editor.ui.model.CaretMulti;
import com.mammb.code.editor.ui.model.Selection;
import com.mammb.code.editor.ui.model.SelectionRange;
import com.mammb.code.editor.ui.model.draw.Draws;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;

/**
 * SelectionImpl.
 * @author Naotsugu Kobayashi
 */
public class SelectionImpl implements Selection {

    /** The selection mode. */
    enum Mode { EMPTY, CARET, FIXED, OPENED }

    /** The color. */
    private static final Color color = new Color(0.6784314F, 0.84705883F, 0.9019608F, 0.3);

    /** The selection range supplier. */
    private SelectionRange selectionRange = SelectionRange.empty;

    /** The selection mode. */
    private Mode mode = Mode.EMPTY;


    @Override
    public void selectOn(OffsetPoint start, OffsetPoint end) {
        selectionRange = SelectionRange.of(start, end);
        mode = Mode.FIXED;
    }

    @Override
    public void selectOn(CaretMulti caret) {
        if (mode != Mode.CARET) {
            selectionRange = caret.selectionRange();
            mode = Mode.CARET;
        }
    }

    @Override
    public void selectOn(OffsetPoint point) {
        if (isOpened()) {
            ((SelectionRange.OpenSelectionRange) selectionRange).to(point);
        } else {
            selectionRange = SelectionRange.openOf(point);
            mode = Mode.OPENED;
        }
    }

    @Override
    public void closeOn(OffsetPoint point) {
        if (isOpened()) {
            ((SelectionRange.OpenSelectionRange) selectionRange).to(point);
            mode = Mode.FIXED;
        }
    }

    @Override
    public void selectOff() {
        selectionRange = SelectionRange.empty;
        mode = Mode.EMPTY;
    }

    @Override
    public boolean hasSelection() {
        if (mode == Mode.EMPTY) {
            return false;
        }
        return length() > 0;
    }

    @Override
    public boolean isOpened() {
        return mode == Mode.OPENED;
    }

    @Override
    public long length() {
        return getRanges().stream().mapToLong(OffsetPointRange::length).sum();
    }

    @Override
    public void draw(GraphicsContext gc, TextRun run, double offsetY, double left) {
        if (mode != Mode.EMPTY) {
            getRanges().forEach(r -> Draws.selection(gc, run, offsetY, left, r, color));
        }
    }

    @Override
    public List<OffsetPointRange> getRanges() {
        return selectionRange.getRanges();
    }

}
