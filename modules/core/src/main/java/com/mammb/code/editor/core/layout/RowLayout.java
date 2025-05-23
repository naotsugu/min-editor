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
package com.mammb.code.editor.core.layout;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.Loc;
import com.mammb.code.editor.core.text.RowText;
import com.mammb.code.editor.core.text.Text;

/**
 * The RowLayout.
 * @author Naotsugu Kobayashi
 */
class RowLayout implements ContentLayout {

    /** The content. */
    private final Content content;
    /** The font metrics. */
    private FontMetrics fm;

    /**
     * Constructor.
     * @param content the content
     * @param fm the font metrics
     */
    public RowLayout(Content content, FontMetrics fm) {
        this.content = content;
        this.fm = fm;
    }

    @Override
    public void setCharsInLine(int n) {
        // nothing to do
    }

    @Override
    public int charsInLine() {
        return 0;
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
    public Text text(int line) {
        return rowText(line);
    }

    @Override
    public List<? extends RowText> texts(int startLine, int endLine) {
        return IntStream.range(
                Math.clamp(startLine, 0, content.rows()),
                Math.clamp(endLine,   0, content.rows() + 1)
            ).mapToObj(this::rowText).toList();
    }

    @Override
    public RowText rowText(int line) {
        return rowTextAt(line);
    }

    @Override
    public RowText rowTextAt(int row) {
        return RowText.of(row, content.getText(row), fm);
    }

    @Override
    public double lineHeight() {
        return fm.getLineHeight();
    }

    @Override
    public int xToCol(int line, double x) {
        return text(line).indexTo(x);
    }

    @Override
    public int homeColOnRow(int line) {
        return 0;
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
    public int lineSize() {
        return content.rows();
    }

    @Override
    public int rowSize() {
        return content.rows();
    }

    @Override
    public int rowToFirstLine(int row) {
        return Math.clamp(row, 0, lineSize() - 1);
    }

    @Override
    public int rowToLastLine(int row) {
        return Math.clamp(row, 0, lineSize() - 1);
    }

    @Override
    public int lineToRow(int line) {
        return Math.clamp(line, 0, content.rows() - 1);
    }

    @Override
    public int rowToLine(int row, int col) {
        return Math.clamp(row, 0, lineSize() - 1);
    }

    @Override
    public Optional<Loc> loc(int row, int col, int rangeLineStart, int rangeLineEnd) {
        if (rangeLineStart <= row && row < rangeLineEnd) {
            return Optional.of(new Loc(xOnLayout(row, col), yOnLayout(row)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Content content() {
        return content;
    }

    @Override
    public FontMetrics fontMetrics() {
        return fm;
    }

}
