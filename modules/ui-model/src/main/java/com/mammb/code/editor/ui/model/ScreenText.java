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
package com.mammb.code.editor.ui.model;

import com.mammb.code.editor.model.buffer.Metrics;
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.ui.model.helper.AroundPicks;
import java.util.List;
import java.util.Optional;

/**
 * ScreenText.
 * @author Naotsugu Kobayashi
 */
public interface ScreenText {

    /**
     * Get the text lines.
     * @return the text lines, non-null
     */
    List<TextLine> lines();

    /**
     * Mark the TextList to dirty.
     */
    void markDirty();

    /**
     * Scroll prev.
     * @param n the number of line
     * @return the number of scrolled lines
     */
    int prev(int n);

    /**
     * Scroll next.
     * @param n the number of line
     * @return the number of scrolled lines
     */
    int next(int n);

    /**
     * Moves to the specified row.
     * @param row the specified row
     * @param metrics the Metrics
     * @return {@code true} if moved to the specified row
     */
    boolean move(int row, Metrics metrics);

    /**
     * Scrolls to the specified position.
     * @param row the number of row
     * @param offset the char offset
     * @return {@code true} if scrolled.
     */
    boolean scrollAtScreen(int row, long offset);

    /**
     * Get the size of text lines capacity.
     * @return the size of text lines capacity
     */
    int pageSize();

    /**
     * Set the page size.
     * @param size the page size
     */
    void setPageSize(int size);

    /**
     * Get the char offset at the specified position.
     * @param x the x position
     * @param y the y position
     * @return the char offset
     */
    default long at(double x, double y) {
        if (y <= 0) {
            TextLine head = head();
            return head == null ? 0 : head.offset();
        }
        Optional<TextLine> line = at(y);
        if (line.isPresent()) {
            return line.get().xToOffset(x);
        } else {
            TextLine tail = tail();
            return tail == null ? 0 : tail.tailOffset();
        }
    }

    /**
     * Get the TextLine corresponding to the y position.
     * @param y the y position
     * @return the TextLine
     */
    default Optional<TextLine> at(double y) {
        double offsetY = 0;
        for (TextLine line : lines()) {
            double top = offsetY;
            offsetY += line.leadingHeight();
            if (top <= y && y < offsetY) {
                return Optional.of(line);
            }
        }
        return Optional.empty();
    }


    /**
     * Get the char offset of the word selection at the specified position.
     * @param x the x position
     * @param y the y position
     * @return the start and end offsets as an array, if word selected, otherwise the single offset.
     */
    default long[] atAroundWord(double x, double y) {
        return at(y).map(line -> AroundPicks.category(line, x))
            .orElse(new long[] { at(x, y) });
    }

    /**
     * Get the char offset of the delimiter broken word selection at the specified position.
     * @param x the x position
     * @param y the y position
     * @return the start and end offsets as an array, if word selected, otherwise the single offset.
     */
    default long[] atAroundDelimiter(double x, double y) {
        return at(y).map(line -> AroundPicks.delimiter(line, x))
            .orElse(new long[] { at(x, y) });
    }

    /**
     * Get the line at head.
     * If rolled up, the first line on the screen.
     * @return the line at head.
     */
    TextLine head();


    default int headlinesIndex() {
        return head().offsetPoint().row() + head().lineIndex();
    }


    /**
     * Get the line at tail.
     * @return the line at tail
     */
    default TextLine tail() {
        List<TextLine> lines = lines();
        return lines.isEmpty() ? null : lines.getLast();
    }


    /**
     * Gets the line to which the specified offset contains.
     * @param offset the specified offset
     * @return the line to which the specified offset contains
     */
    default TextLine lineAt(long offset) {
        return lines().stream()
            .filter(l -> l.contains(offset))
            .findFirst().orElse(null);
    }


    /**
     * Gets the row to which the specified offset contains.
     * @param offset the specified offset
     * @return the row to which the specified offset contains
     */
    default List<TextLine> rowAt(long offset) {

        TextLine line = lineAt(offset);
        if (line == null) {
            return List.of();
        }

        final List<TextLine> lines = lines();
        int baseIndex = lines.indexOf(line);

        int start = baseIndex;
        while (line.lineIndex() > 0 && start > 0) {
            line = lines.get(--start);
        }

        int end = baseIndex;
        while (line.lineIndex() != (line.lineSize() - 1) && end + 1 < lines.size()) {
            line = lines.get(++end);
        }
        return lines.subList(start, end + 1);
    }


    /**
     * Get the LayoutLine corresponding to the char offset.
     * @param offset the char offset
     * @return the LayoutLine
     */
    default LayoutLine layoutLine(long offset) {

        final List<TextLine> lines = lines();

        if (lines.isEmpty() ||
            offset < lines.getFirst().offsetPoint().offset() ||
            offset > lines.getLast().tailOffset()) {
            return null;
        }

        double offsetY = 0;
        for (TextLine line : lines) {
            if (line.contains(offset) || line.containsTailOn(offset)) {
                return new LayoutLine(line, offsetY);
            }
            offsetY += line.leadingHeight();
        }
        return null;
    }

}
