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

import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.ui.pane.Caret;
import com.mammb.code.editor2.ui.pane.Selection;
import com.mammb.code.editor2.ui.pane.TextList;
import java.util.List;
import java.util.Objects;

/**
 * Select behavior collection.
 * @author Naotsugu Kobayashi
 */
public class SelectBehaviors {

    /**
     * Select the specified range.
     * @param range the range to select
     * @param caret the caret
     * @param selection the selection
     */
    public static void select(OffsetPoint[] range, Caret caret, Selection selection) {
        select(range, selection);
        caret.at(range[1].offset(), true);
    }

    /**
     * Select the specified range.
     * @param range range to select
     * @param caret the caret
     * @param selection the selection
     */
    public static void select(int[] range, Caret caret, Selection selection) {
        select(range[0], range[1], caret, selection);
    }

    /**
     * Selects a range of specified offsets.
     * @param offset1 the offset
     * @param offset2 the offset
     * @param caret the caret
     * @param selection the selection
     */
    public static void select(int offset1, int offset2, Caret caret, Selection selection) {
        caret.at(offset1, true);
        selection.start(caret.offsetPoint());
        caret.at(offset2, true);
        selection.to(caret.offsetPoint());
    }

    /**
     * Selects a range of specified points.
     * @param range the range of point
     * @param selection the selection
     */
    public static void select(OffsetPoint[] range, Selection selection) {
        select(range[0], range[1], selection);
    }

    /**
     * Selects a range of specified points.
     * @param from the point at start
     * @param to the point at end
     * @param selection the selection
     */
    public static void select(OffsetPoint from, OffsetPoint to, Selection selection) {
        selection.start(Objects.requireNonNull(from));
        selection.to(Objects.requireNonNull(to));
    }

    /**
     * Select the current caret line
     * @param caret the caret
     * @param selection the selection
     * @param texts the texts
     */
    public static void selectCurrentLine(Caret caret, Selection selection, TextList texts) {
        SelectBehaviors.select(linePointRange(caret.offset(), texts), caret, selection);
    }

    /**
     * Select the current row
     * @param caret the caret
     * @param selection the selection
     * @param texts the texts
     */
    public static void selectCurrentRow(Caret caret, Selection selection, TextList texts) {
        if (selection.length() == 0 || selection.min().row() == selection.max().row()) {
            // if not selected or within a row selected, select current row
            SelectBehaviors.select(rowPointRange(caret.offset(), texts), caret, selection);
        } else {
            // if selected, reselect all rows in the selection
            OffsetPoint start = rowPointRange(selection.min().offset(), texts)[0];
            OffsetPoint end   = rowPointRange(selection.max().offset() - 1, texts)[1];
            OffsetPoint[] range = (selection.startOffset() == selection.min())
                ? new OffsetPoint[] { start, end }
                : new OffsetPoint[] { end, start };
            SelectBehaviors.select(range, caret, selection);
        }
    }

    /**
     * Get the point of the current caret row.
     * @param charOffset the char offset
     * @param texts the texts
     * @return the point of the current caret row
     */
    private static OffsetPoint[] rowPointRange(int charOffset, TextList texts) {
        List<TextLine> lines = texts.rowAt(charOffset);
        return new OffsetPoint[] {
            lines.get(0).point(),
            lines.get(lines.size() - 1).tailPoint()
        };
    }

    /**
     * Get the point of the current caret line.
     * @param charOffset the char offset
     * @param texts the texts
     * @return the point of the current caret line
     */
    private static OffsetPoint[] linePointRange(int charOffset, TextList texts) {
        var line = texts.lineAt(charOffset);
        return new OffsetPoint[] {
            line.point(),
            line.tailPoint()
        };
    }

}
