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
import com.mammb.code.editor.core.Loc;
import java.util.List;
import java.util.Optional;
import com.mammb.code.editor.core.text.ColsText;

/**
 * The CSV Layout.
 * @author Naotsugu Kobayashi
 */
public class CsvLayout implements ContentLayout {

    /** The content. */
    private final Content content;
    /** The font metrics. */
    private FontMetrics fm;

    /**
     * Constructor.
     * @param content the content
     * @param fm the font metrics
     */
    public CsvLayout(Content content, FontMetrics fm) {
        this.content = content;
        this.fm = fm;
    }

    @Override
    public int rowToFirstLine(int row) {
        return Math.clamp(row, 0, lineSize());
    }

    @Override
    public int rowToLastLine(int row) {
        return Math.clamp(row, 0, lineSize());
    }

    @Override
    public int rowToLine(int row, int col) {
        return Math.clamp(row, 0, lineSize());
    }

    @Override
    public int lineToRow(int line) {
        return Math.clamp(line, 0, content.rows());
    }

    @Override
    public int lineSize() {
        return content.rows();
    }

    @Override
    public int rowSize() {
        return content.rows();
    }

    @Override
    public ColsText text(int line) {
        return rowText(line);
    }

    @Override
    public ColsText rowTextAt(int row) {
        return null; // TODO
    }

    @Override
    public double lineHeight() {
        return fm.getLineHeight();
    }

    @Override
    public void setLineWidth(int width) {
        // nothing to do
    }

    @Override
    public int lineWidth() {
        return 0;
    }

    @Override
    public int homeColOnRow(int line) {
        return 0;
    }

    @Override
    public int xToCol(int line, double x) {
        return text(line).indexTo(x);
    }

    @Override
    public double standardCharWidth() {
        return fm.standardCharWidth();
    }

    @Override
    public int tabSize() {
        return fm.getTabSize();
    }

    @Override
    public void updateFontMetrics(FontMetrics fontMetrics) {
        fm = fontMetrics;
    }

    @Override
    public void refresh(int line) {
        // nothing to do
    }

    @Override
    public void refreshAt(int startRow, int endRow) {
        // nothing to do
    }

    @Override
    public List<ColsText> texts(int startLine, int endLine) {
        return List.of(); // TODO
    }

    @Override
    public ColsText rowText(int line) {
        return null; // TODO
    }

    @Override
    public Optional<Loc> loc(int row, int col, int rangeLineStart, int rangeLineEnd) {
        return Optional.empty(); // TODO
    }
}
