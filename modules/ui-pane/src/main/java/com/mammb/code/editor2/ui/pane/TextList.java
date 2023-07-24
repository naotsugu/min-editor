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
package com.mammb.code.editor2.ui.pane;

import com.mammb.code.editor2.model.layout.TextLine;
import java.util.List;
import java.util.Optional;

/**
 * TextList.
 * @author Naotsugu Kobayashi
 */
public interface TextList {

    /**
     * Get the text lines.
     * @return the text lines
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
     * Scrolls to the specified position.
     * @param row the number of row
     * @param offset the char offset
     * @return {@code true} if scrolled.
     */
    boolean scrollAt(int row, int offset);

    /**
     * Get the size of text lines capacity.
     * @return the size of text lines capacity
     */
    int capacity();

    /**
     * Get the char offset at the specified position.
     * @param x the x position
     * @param y the y position
     * @return the char offset
     */
    default int at(double x, double y) {
        if (y <= 0) {
            TextLine head = head();
            return head == null ? 0 : head.start();
        }
        Optional<TextLine> line = at(y);
        if (line.isPresent()) {
            return line.get().xToOffset(x);
        } else {
            TextLine tail = tail();
            return tail == null ? 0 : tail.end();
        }
    }

    /**
     * Get the char offset of the word selection at the specified position.
     * @param x the x position
     * @param y the y position
     * @return the start and end offsets as an array, if word selected, otherwise the single offset.
     */
    default int[] atAround(double x, double y) {
        Optional<TextLine> maybeLine = at(y);
        if (maybeLine.isEmpty()) {
            return new int[] { at(x, y) };
        }
        TextLine line = maybeLine.get();

        int offset = line.xToOffset(x);
        int start = offset;
        int end = offset;
        int type = Character.getType(Character.toLowerCase(line.charAt(offset)));

        for (int i = offset + 1; i < line.end(); i++) {
            if (type != Character.getType(Character.toLowerCase(line.charAt(i)))) {
                end = i;
                break;
            }
        }
        for (int i = offset - 1; i >= line.start(); i--) {
            if (type != Character.getType(Character.toLowerCase(line.charAt(i)))) {
                break;
            } else {
                start = i;
            }
        }
        if (start != end) {
            return new int[] { start, end };
        } else {
            return new int[] { start };
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
     * Get the LayoutLine corresponding to the char offset.
     * @param offset the char offset
     * @return the LayoutLine
     */
    default LayoutLine layoutLine(int offset) {

        final List<TextLine> lines = lines();

        if (offset < lines.get(0).point().offset()) {
            return null;
        }
        double offsetY = 0;
        for (TextLine line : lines) {
            if (line.contains(offset) || line.containsTailOn(offset)) {
                return new LayoutLine(line, offsetY);
            }
            if (line.endMarkCount() > 0) {
                offsetY += line.leadingHeight();
            }
        }
        return null;
    }

    /**
     * Get the line at head.
     * @return the line at head
     */
    default TextLine head() {
        return lines().get(0);
    }

    /**
     * Get the line at tail.
     * @return the line at tail
     */
    default TextLine tail() {
        List<TextLine> lines = lines();
        return lines.isEmpty() ? null : lines.get(lines.size() - 1);
    }

}
