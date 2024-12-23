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

import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.text.RowText;
import com.mammb.code.editor.core.text.SubText;
import com.mammb.code.editor.core.text.Text;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * The WrapLayout.
 * @author Naotsugu Kobayashi
 */
public class WrapLayout implements ContentLayout {

    /** The width of screen(number of characters per line). */
    private double screenWidth = 0;
    /** The content. */
    private final Content content;
    /** The font metrics. */
    private FontMetrics fm;
    /** The wrapped lines. */
    private final List<SubRange> lines = new ArrayList<>();

    /**
     * Constructor.
     * @param content the content
     * @param fm the wrapped lines
     */
    public WrapLayout(Content content, FontMetrics fm) {
        this.content = content;
        this.fm = fm;
    }

    @Override
    public void setScreenWidth(int width) {
        this.screenWidth = width;
        refresh(0);
    }

    @Override
    public void refresh(int line) {
        lines.subList(line, lines.size()).clear();
        int i = 0;
        if (!lines.isEmpty()) {
            var range = lines.getLast();
            if (range.subLine() != range.subLines()) {
                lines.subList(lines.size() - (range.subLine() + 1), lines.size()).clear();
                i = range.row();
            } else {
                i = range.row() + 1;
            }
        }
        for (; i < content.rows(); i++) {
            lines.addAll(subRanges(i));
        }
    }

    @Override
    public void refreshAt(int startRow, int endRow) {
        int start = rowToFirstLine(startRow);
        int end   = rowToFirstLine(endRow);
        lines.subList(start, end).clear();
        List<SubRange> newLines = IntStream.range(startRow, endRow)
                .mapToObj(this::subRanges)
                .flatMap(Collection::stream)
                .toList();
        var next = lines.get(start);
        int n = (next == null || newLines.isEmpty())
                ? 0
                : (newLines.getLast().row() + 1) - next.row();
        if (n != 0) {
            for (int i = start; i < lines.size(); i++) {
                lines.get(i).plusRow(n);
            }
        }
        lines.addAll(start, newLines);
    }

    private List<SubRange> subRanges(int row) {
        List<SubRange> list = new ArrayList<>();
        List<SubText> subTexts = subTextsAt(row);
        for (int i = 0; i < subTexts.size(); i++) {
            SubText subText = subTexts.get(i);
            list.add(new SubRange(row, i, subTexts.size(), subText.fromIndex(), subText.toIndex()));
        }
        return list;
    }

    @Override
    public SubText text(int line) {
        SubRange range = lines.get(line);
        List<SubText> subs = subTextsAt(range.row());
        return subs.get(range.subLine());
    }

    @Override
    public List<Text> texts(int startLine, int endLine) {
        if (startLine == endLine ||
            (startLine >= lines.size() && endLine >= lines.size()))  return List.of();
        if (startLine > endLine) {
            int tmp = startLine;
            startLine = endLine;
            endLine = tmp;
        }
        SubRange startRange = lines.get(Math.clamp(startLine, 0, lines.size() - 1));
        SubRange endRange   = lines.get(Math.clamp(endLine - 1, 0, lines.size() - 1));

        return IntStream.rangeClosed(startRange.row(), endRange.row()).mapToObj(i -> {
            var subs = subTextsAt(i);

            if (i == endRange.row() && subs.size() >= endRange.subLine() + 1) {
                subs.subList(endRange.subLine() + 1, subs.size()).clear();
            }
            if (i == startRange.row()) {
                subs.subList(0, startRange.subLine()).clear();
            }
            return subs;
        }).flatMap(Collection::stream).map(Text.class::cast).toList();
    }

    @Override
    public RowText rowText(int line) {
        return rowTextAt(lines.get(line).row());
    }

    @Override
    public RowText rowTextAt(int row) {
        return RowText.of(row, content.getText(row), fm);
    }

    private List<SubText> subTextsAt(int row) {
        return SubText.of(rowTextAt(row), screenWidth * fm.standardCharWidth());
    }

    @Override
    public double lineHeight() {
        return fm.getLineHeight();
    }

    @Override
    public int xToCol(int line, double x) {
        return text(line).indexTo(x) + lines.get(line).fromIndex();
    }

    @Override
    public double xOnLayout(int line, int col) {
        SubText subText = text(line);
        return subText.widthTo(col - subText.fromIndex());
    }

    @Override
    public int homeColOnRow(int line) {
        return lines.get(line).fromIndex();
    }

    @Override
    public double standardCharWidth() {
        return fm.standardCharWidth();
    }

    @Override
    public void updateFontMetrics(FontMetrics fontMetrics) {
        fm = fontMetrics;
    }

    @Override
    public int lineSize() {
        return lines.size();
    }

    @Override
    public int rowSize() {
        return content.rows();
    }

    @Override
    public int rowToFirstLine(int row) {
        if (row <= 0) return 0;
        int line = Collections.binarySearch(lines, new SubRange(row, 0, 0, 0, 0));
        return (line < 0) ? lines.size() : line;
    }

    @Override
    public int rowToLastLine(int row) {
        int first = rowToFirstLine(row);
        return first + lines.get(first).subLines - 1;
    }

    @Override
    public int lineToRow(int line) {
        if (line <= 0) return 0;
        var sub = lines.get(line);
        return (sub == null) ? content.rows() : sub.row();
    }

    @Override
    public int rowToLine(int row, int col) {
        int line = rowToFirstLine(row);
        for (int i = 0; i < lines.get(line).subLines; i++) {
            if (lines.get(line + i).contains(row, col)) {
                return line + i;
            }
        }
        return lines.size();
    }

    @Override
    public Optional<Loc> loc(int row, int col, int rangeLineStart, int rangeLineEnd) {
        for (int i = rangeLineStart; i < rangeLineEnd; i++) {
            // TODO optimize
            SubRange sub = lines.get(Math.clamp(i, 0, lines.size() - 1));
            if (sub.contains(row, col)) {
                return Optional.of(new Loc(xOnLayout(i, col), yOnLayout(i)));
            }
        }
        return Optional.empty();
    }

    static class SubRange implements Comparable<SubRange> {
        private int row, subLine, subLines, fromIndex, toIndex;
        public SubRange(int row, int subLine, int subLines, int fromIndex, int toIndex) {
            this.row = row;
            this.subLine = subLine;
            this.subLines = subLines;
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
        }
        public void plusRow(int n) { row += n; }
        public int row() { return row; }
        public int subLine() { return  subLine; }
        public int subLines() { return subLines; }
        public int fromIndex() { return fromIndex; }
        public int toIndex() { return toIndex; }
        public int length() { return toIndex - fromIndex; }
        boolean contains(int row, int col) {
            return this.row == row && (
                (this.fromIndex <= col && col < this.toIndex) ||
                (col == 0 && fromIndex == 0 && toIndex == 0) ||
                (col == this.toIndex && subLine == subLines - 1)
            );
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SubRange subRange = (SubRange) o;
            return row == subRange.row &&
                    subLine == subRange.subLine &&
                    subLines == subRange.subLines &&
                    fromIndex == subRange.fromIndex &&
                    toIndex == subRange.toIndex;
        }
        @Override
        public int hashCode() {
            return Objects.hash(row, subLine, subLines, fromIndex, toIndex);
        }
        @Override
        public int compareTo(SubRange that) {
            return Comparator.comparing(SubRange::row)
                    .thenComparing(SubRange::subLine)
                    .compare(this, that);
        }
    }

    /**
     * Get the content.
     * @return the content
     */
    Content getContent() {
        return content;
    }

    /**
     * Get the font metrics.
     * @return the font metrics
     */
    FontMetrics getFm() {
        return fm;
    }

}
