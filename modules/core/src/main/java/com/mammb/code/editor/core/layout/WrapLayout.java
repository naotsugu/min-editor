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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.Loc;
import com.mammb.code.editor.core.text.RowText;
import com.mammb.code.editor.core.text.SubText;
import com.mammb.code.editor.core.text.Text;

/**
 * The WrapLayout.
 * @author Naotsugu Kobayashi
 */
class WrapLayout implements ContentLayout {

    /** The number of characters in a line(width of screen). */
    private int charsInLine = 0;
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
    public void setCharsInLine(int n) {
        this.charsInLine = n;
        refresh(0);
    }

    @Override
    public int charsInLine() {
        return charsInLine;
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

        int fluctuations = (content.rows() - 1) - lines.getLast().row();

        int fromIndex = Collections.binarySearch(lines, new SubRange(startRow, 0, 0, 0, 0));
        int toIndex = Collections.binarySearch(lines, new SubRange(endRow, 0, 0, 0, 0));
        toIndex = toIndex < 0 ? lines.size() - 1 : toIndex;
        toIndex += lines.get(toIndex).subLines();
        lines.subList(fromIndex, Math.min(toIndex, lines.size())).clear();

        List<SubRange> renew = IntStream.rangeClosed(
                Math.clamp(startRow, 0, content.rows() - 1),
                Math.clamp(endRow + fluctuations, 0, content.rows() - 1))
            .mapToObj(this::subRanges)
            .flatMap(Collection::stream)
            .toList();
        lines.addAll(fromIndex, renew);

        if (fluctuations != 0) {
            // fix up the shifted row number
            for (int i = fromIndex + renew.size(); i < lines.size(); i++) {
                lines.get(i).plusRow(fluctuations);
            }
        }
        if (Objects.nonNull(System.getProperty("debug." + WrapLayout.class.getName()))) {
            var expects = new ArrayList<SubRange>();
            for (int i = 0; i < content.rows(); i++) {
                expects.addAll(subRanges(i));
            }
            if (!expects.equals(lines)) {
                System.out.println("!! " + startRow + " " + endRow);
                System.out.println(Arrays.deepToString(Thread.currentThread().getStackTrace()));
                System.out.println("lines");
                lines.forEach(System.out::println);
                System.out.println("expects");
                expects.forEach(System.out::println);
            }
        }
    }

    @Override
    public SubText text(int line) {
        if (line >= lines.size()) {
            return SubText.of(RowText.of(lines.getLast().row() + 1, "", fm), 0).getLast();
        }
        SubRange range = subRange(line);
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
        SubRange startRange = subRange(startLine);
        SubRange endRange   = subRange(endLine - 1);

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
        return rowTextAt(subRange(line).row());
    }

    @Override
    public RowText rowTextAt(int row) {
        return RowText.of(row, content.getText(row), fm);
    }

    private List<SubText> subTextsAt(int row) {
        return SubText.of(rowTextAt(row), charsInLine * fm.standardCharWidth());
    }

    @Override
    public double lineHeight() {
        return fm.getLineHeight();
    }

    @Override
    public int xToCol(int line, double x) {
        return text(line).indexTo(x) + subRange(line).fromIndex();
    }

    @Override
    public int xToCaretCol(int line, double x) {
        SubText subText = text(line);
        SubRange subRange = subRange(line);
        int len = subText.indexTo(x);
        if (subRange.isWrapped() &&
            subRange.length() == len &&
            x < subText.width() + fm.standardCharWidth()) {
            return subRange.fromIndex() + subText.indexLeft(len);
        } else {
            return subRange.fromIndex() + len;
        }
    }


    @Override
    public double xOnLayout(int line, int col) {
        SubText subText = text(line);
        return subText.widthTo(col - subText.fromIndex());
    }

    @Override
    public int homeColOnRow(int line) {
        return subRange(line).fromIndex();
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
        return lines.size();
    }

    @Override
    public int rowSize() {
        return content.rows();
    }

    @Override
    public int rowToFirstLine(int row) {
        if (row <= 0) return 0;
        row = Math.min(row, content.rows() - 1);
        int line = Collections.binarySearch(lines, new SubRange(row, 0, 0, 0, 0));
        return (line < 0)
            ? lines.size() - 1 - lines.getLast().subLine()
            : line;
    }

    @Override
    public int rowToLastLine(int row) {
        row = Math.min(row, content.rows() - 1);
        int first = rowToFirstLine(row);
        return first + subRange(first).subLines() - 1;
    }

    @Override
    public int lineToRow(int line) {
        if (line <= 0) return 0;
        var sub = subRange(line);
        return (sub == null) ? content.rows() - 1 : sub.row();
    }

    @Override
    public int rowToLine(int row, int col) {
        int line = rowToFirstLine(row);
        int subLines = subRange(line).subLines();
        for (int i = 0; i < subLines - 1; i++) {
            if (subRange(line + i).contains(row, col)) {
                return line + i;
            }
        }
        return line + (subLines - 1);
    }

    @Override
    public Optional<Loc> loc(int row, int col, int rangeLineStart, int rangeLineEnd) {
        for (int i = rangeLineStart; i < rangeLineEnd; i++) {
            // TODO optimize
            if (subRange(i).contains(row, col)) {
                return Optional.of(new Loc(xOnLayout(i, col), yOnLayout(i)));
            }
        }
        return Optional.empty();
    }

    @Override
    public Content content() {
        return content;
    }

    @Override
    public FontMetrics fontMetrics() {
        return fm;
    }

    List<SubRange> lines() {
        return lines;
    }

    private SubRange subRange(int line) {
        return lines.get(Math.clamp(line, 0, lines.size() - 1));
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

}
