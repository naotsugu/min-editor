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
package com.mammb.code.editor.model.layout;

import com.mammb.code.editor.model.layout.impl.TextLineImpl;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TextLine.
 * @author Naotsugu Kobayashi
 */
public interface TextLine extends Textual {

    double DEFAULT_MARGIN_TOP = 1;
    double DEFAULT_MARGIN_BOTTOM = 1;

    @Override
    default String text() {
        return runs().stream()
            .map(TextRun::text)
            .collect(Collectors.joining());
    }

    @Override
    OffsetPoint point();

    /**
     * Get the line index.
     * Always zero if not wrapped.
     * <pre>
     *     ...
     *     | a | b | c |  lineIndex:0  lineSize: 2
     *     | d |          lineIndex:1  lineSize: 2
     * </pre>
     * @return the line index
     */
    int lineIndex();

    /**
     * Get the line size.
     * Always zero if not wrapped.
     * <pre>
     *     ...
     *     | a | b | c |  lineIndex:0  lineSize: 2
     *     | d |          lineIndex:1  lineSize: 2
     * </pre>
     * @return the line size
     */
    int lineSize();

    /**
     * Get the char length.
     * @return the char length
     */
    int length();

    /**
     * Get the width.
     * @return the width
     */
    double width();

    /**
     * Get the height.
     * @return the height
     */
    double height();

    /**
     * Get the margin top.
     * @return the margin top
     */
    default double marginTop() {
        return DEFAULT_MARGIN_TOP;
    }

    /**
     * Get the margin bottom.
     * @return the margin bottom
     */
    default double marginBottom() {
        return DEFAULT_MARGIN_BOTTOM;
    }

    /**
     * Get the leading height(height + marginTop + marginBottom).
     * @return the leading height(height + marginTop + marginBottom)
     */
    default double leadingHeight() {
        return height() + marginTop() + marginBottom();
    }

    /**
     * Get the text runs.
     * @return the text runs
     */
    List<TextRun> runs();

    /**
     * Get the code point offset corresponding to the specified offset.
     * @param offset the specified offset
     * @return the code point offset corresponding to the specified offset
     */
    default OffsetPoint offsetPoint(long offset) {
        TextRun run = textRunAt(offset);
        return OffsetPoint.of(point().row(), offset, run.cpOffset(offset));
    }

    /**
     * Get the x position from the specified x offset.
     * @param offset the specified char offset(total)
     * @return the x position from the specified x offset
     */
    default double offsetToX(long offset) {
        TextRun run;
        if (offset == tailOffset() && endMarkCount() == 0) {
            List<TextRun> runs = runs();
            run = runs.get(runs.size() - 1);
        } else {
            run = textRunAt(offset);
        }
        return run.offsetToX().apply(Math.toIntExact(offset - run.offset()));
    }

    /**
     * Get the char at offset position.
     * @param offset the char (total) offset
     * @return the char at offset position
     */
    default char charAt(long offset) {
        TextRun run = textRunAt(offset);
        int index = Math.toIntExact(offset - run.source().point().offset());
        return run.source().text().charAt(index);
    }

    /**
     * Get the char string at offset position.
     * @param offset the char (total) offset
     * @return the char string at offset position
     */
    default String charStringAt(long offset) {
        char ch = charAt(offset);
        if (ch == '\r' && offset < tailOffset() - 1 && charAt(offset + 1) == '\n') {
            return "\r\n";
        } else if (ch == '\n' && offset > point().offset() && charAt(offset - 1) == '\r') {
            return "\r\n";
        }
        return Character.isHighSurrogate(ch)
            ? String.valueOf(new char[] {ch, charAt(offset + 1)})
            : Character.isLowSurrogate(ch)
            ? String.valueOf(new char[] {charAt(offset - 1), ch})
            : String.valueOf(ch);
    }

    /**
     * Get the text run at offset position.
     * @param offset the char (total) offset
     * @return the text run at offset position
     */
    default TextRun textRunAt(long offset) {
        List<TextRun> runs = runs();
        if (containsTailOn(offset)) {
            return runs.get(runs.size() - 1);
        }
        if (!contains(offset)) {
            throw new IndexOutOfBoundsException(
                "start:%d end:%d offset:%d".formatted(offset(), tailOffset(), offset));
        }
        for (TextRun run : runs) {
            if (run.length() == 0) continue;
            long runStart = run.source().point().offset() + run.start();
            long runEnd = runStart + run.length();
            if (runStart <= offset && offset < runEnd) {
                return run;
            }
        }
        throw new IllegalStateException("internal error:" + runs());
    }


    /**
     * Get whether the given (total) offset contains on this line.
     * @param offset the char (total) offset
     * @return {@code true}, if the given (total) offset contains on this line
     */
    default boolean contains(long offset) {
        return offset() <= offset && offset < tailOffset();
    }


    /**
     * Get whether the given (total) offset located on tail of this line.
     * @param offset the char (total) offset
     * @return {@code true}, if the given (total) offset located on tail of this line
     */
    default boolean containsTailOn(long offset) {
        return tailOffset() == offset && isBottomLine();
    }

    default boolean isBottomLine() {
        return lineIndex() == (lineSize() - 1) && endMarkCount() == 0;
    }


    /**
     * Get the char offset(total) from the specified x position.
     * @param x the specified x position
     * @return the char offset(total) from the specified x position
     */
    default long xToOffset(double x) {
        long offset = offset();
        if (length() == 0) {
            return offset; // end of file
        }
        for (TextRun run : runs()) {
            double runStart = run.layout().x();
            double runEnd = runStart + run.layout().width();
            // if the text line is a newline only, its width is zero.
            // zero position is the offset to the left of LF
            if (runStart <= x && x < runEnd || runStart == runEnd) {
                offset += run.xToOffset().apply(x - run.layout().x());
                return offset;
            }
            offset += run.length();
        }
        return Math.max(offset - endMarkCount(), 0);
    }


    /**
     * Create a new TextLine.
     * @param point the OffsetPoint
     * @param lineIndex the line index
     * @param lineSize the line size
     * @param length the char length
     * @param width the width of line
     * @param height the height of line
     * @param textRuns the text runs
     * @return a created TextLine
     */
    static TextLine of(
            OffsetPoint point,
            int lineIndex,
            int lineSize,
            int length,
            double width,
            double height,
            List<TextRun> textRuns) {
        return new TextLineImpl(point, lineIndex, lineSize, length, width, height, textRuns);
    }

}
