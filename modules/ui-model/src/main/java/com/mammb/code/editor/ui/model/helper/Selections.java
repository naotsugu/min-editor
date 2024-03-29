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
package com.mammb.code.editor.ui.model.helper;

import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.OffsetPointRange;
import com.mammb.code.editor.ui.model.Caret;
import com.mammb.code.editor.ui.model.ScreenText;
import com.mammb.code.editor.ui.model.Selection;

import java.util.List;
import java.util.Objects;

/**
 * Selection utilities.
 * @author Naotsugu Kobayashi
 */
public class Selections {

    /**
     * Select the specified range.
     * @param range the range to select
     * @param caret the caret
     * @param selection the selection
     */
    public static void select(OffsetPoint[] range, Caret caret, Selection selection) {
        selection.select(
            Objects.requireNonNull(range[0]),
            Objects.requireNonNull(range[1]));
        caret.at(range[1].offset(), true);
    }

    /**
     * Select the specified range.
     * @param range range to select
     * @param caret the caret
     * @param selection the selection
     */
    public static void select(long[] range, Caret caret, Selection selection) {
        select(range[0], range[1], caret, selection);
    }

    /**
     * Selects a range of specified offsets.
     * @param offset1 the offset
     * @param offset2 the offset
     * @param caret the caret
     * @param selection the selection
     */
    public static void select(long offset1, long offset2, Caret caret, Selection selection) {
        caret.at(offset1, true);
        selection.selectOn(caret);
        caret.at(offset2, true);
    }

    /**
     * Select the current caret line
     * @param caret the caret
     * @param selection the selection
     * @param texts the texts
     */
    public static void selectCurrentLine(Caret caret, Selection selection, ScreenText texts) {
        Selections.select(linePointRange(caret.offset(), texts), caret, selection);
    }

    /**
     * Select the current row
     * @param caret the caret
     * @param selection the selection
     * @param range the selection range
     * @param texts the texts
     */
    public static void selectCurrentRow(Caret caret, Selection selection, OffsetPointRange range, ScreenText texts) {
        if (range.length() == 0 || range.minOffsetPoint().row() == range.maxOffsetPoint().row()) {
            // if not selected or within a row selected, select current row
            Selections.select(rowPointRange(caret.offset(), texts), caret, selection);
        } else {
            // if selected, reselect all rows in the selection
            OffsetPoint start = rowPointRange(range.minOffsetPoint().offset(), texts)[0];
            OffsetPoint end   = rowPointRange(range.maxOffsetPoint().offset() - 1, texts)[1];
            OffsetPoint[] r = (range.startOffsetPoint() == range.minOffsetPoint())
                ? new OffsetPoint[] { start, end }
                : new OffsetPoint[] { end, start };
            Selections.select(r, caret, selection);
        }
    }

    // -- private -------------------------------------------------------------

    /**
     * Get the point of the current caret row.
     * @param charOffset the char offset
     * @param texts the texts
     * @return the point of the current caret row
     */
    private static OffsetPoint[] rowPointRange(long charOffset, ScreenText texts) {
        List<TextLine> lines = texts.rowAt(charOffset);
        return new OffsetPoint[] {
            lines.getFirst().offsetPoint(),
            lines.getLast().tailPoint()
        };
    }

    /**
     * Get the point of the current caret line.
     * @param charOffset the char offset
     * @param texts the texts
     * @return the point of the current caret line
     */
    private static OffsetPoint[] linePointRange(long charOffset, ScreenText texts) {
        var line = texts.lineAt(charOffset);
        return new OffsetPoint[] {
            line.offsetPoint(),
            line.tailPoint()
        };
    }

}
