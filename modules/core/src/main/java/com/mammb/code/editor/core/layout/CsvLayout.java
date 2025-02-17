/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.core.layout;

import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.FontMetrics;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import com.mammb.code.editor.core.text.ColsText;

/**
 * The CSV Layout.
 * @author Naotsugu Kobayashi
 */
public class CsvLayout extends RowLayout {

    /** The screen column width list. */
    private final List<Double> colWidths = new ArrayList<>();
    /** dirty?. */
    private boolean dirty = false;

    /**
     * Constructor.
     * @param content the content
     * @param fm the font metrics
     */
    public CsvLayout(Content content, FontMetrics fm) {
        super(content, fm);
    }

    @Override
    public ColsText rowTextAt(int row) {

        var text = ColsText.csvOf(row, content().getText(row), fontMetrics());

        double[] rawWidths = text.rawWidths();
        for (int c = 0; c < rawWidths.length; c++) {
            if (c >= colWidths.size() - 1) {
                colWidths.add(0.0);
            }
            if (rawWidths[c] > colWidths.get(c)) {
                colWidths.set(c, rawWidths[c]);
                dirty = true;
            }
        }
        text.fixCols(colWidths);
        return text;
    }

    @Override
    public List<? extends ColsText> texts(int startLine, int endLine) {
        var texts = textsInternal(startLine, endLine);
        if (dirty) {
            texts = textsInternal(startLine, endLine);
            dirty = false;
        }
        return texts;
    }

    @Override
    public ColsText rowText(int line) {
        return rowTextAt(line);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }

    public List<? extends ColsText> textsInternal(int startRow, int endRow) {
        return IntStream.range(
            Math.clamp(startRow, 0, rowSize()),
            Math.clamp(endRow,   0, rowSize() + 1)
        ).mapToObj(this::rowText).toList();
    }

}
