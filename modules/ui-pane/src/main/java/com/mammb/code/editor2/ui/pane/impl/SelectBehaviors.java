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

import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.ui.pane.Caret;
import com.mammb.code.editor2.ui.pane.Selection;
import com.mammb.code.editor2.ui.pane.TextList;

/**
 * Select behavior collection.
 * @author Naotsugu Kobayashi
 */
public class SelectBehaviors {

    /**
     * Select the specified range.
     * @param offsetRange range to select
     * @param caret the caret
     * @param selection the selection
     */
    public static void select(int[] offsetRange, Caret caret, Selection selection) {
        select(offsetRange[0], offsetRange[1], caret, selection);
    }

    /**
     * Selects a range of specified offsets.
     * @param offset1 the offset
     * @param offset2 the offset
     * @param caret the caret
     * @param selection the selection
     */
    public static void select(int offset1, int offset2, Caret caret, Selection selection) {
        int start = Math.min(offset1, offset2);
        int end   = Math.max(offset1, offset2);
        caret.at(start, true);
        selection.start(caret.offsetPoint());
        caret.at(end, true);
        selection.to(caret.offsetPoint());
    }

    /**
     * Select the current caret row
     * @param caret the caret
     * @param selection the selection
     * @param texts the texts
     */
    public static void selectCurrentRow(Caret caret, Selection selection, TextList texts) {
        if (selection.length() <= 0) {
            SelectBehaviors.select(rowOffsetRange(caret, texts), caret, selection);
        } else {
            OffsetPoint min = selection.min();
            OffsetPoint max = selection.max();
            caret.at(min.offset(), true);
            int start = rowOffsetRange(caret, texts)[0];
            caret.at(max.offset(), true);
            int end = rowOffsetRange(caret, texts)[1];
            SelectBehaviors.select(start, end, caret, selection);
        }
    }

    /**
     * Get the offset of the current caret row.
     * @param caret the caret
     * @param texts the texts
     * @return the offset of the current caret row
     */
    private static int[] rowOffsetRange(Caret caret, TextList texts) {
        var lines = texts.rowAt(caret.offset());
        return new int[] {
            lines.get(0).offset(),
            lines.get(lines.size() - 1).tailOffset()
        };
    }

    /**
     * Get the offset of the current caret line.
     * @param caret the caret
     * @param texts the texts
     * @return the offset of the current caret line
     */
    private static int[] lineOffsetRange(Caret caret, TextList texts) {
        var line = texts.lineAt(caret.offset());
        return new int[] {
            line.offset(),
            line.tailOffset()
        };
    }

}
