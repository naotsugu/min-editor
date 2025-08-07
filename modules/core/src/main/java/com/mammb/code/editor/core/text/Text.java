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
package com.mammb.code.editor.core.text;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Text.
 * @author Naotsugu Kobayashi
 */
public interface Text {

    /**
     * Get the number of rows.
     * @return the number of rows
     */
    int row();

    /**
     * Get the text value.
     * @return the text value
     */
    String value();

    /**
     * Get the advances.
     * @return the advances
     */
    double[] advances();

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
     * Get the text length.
     * @return the text length
     */
    default int length() {
        return value().length();
    }

    /**
     * Gets whether this text is terminated by an LF.
     * @return {@code true} if this text is terminated by an LF
     */
    default boolean isEndWithLf() {
        int len = value().length();
        return (len > 0) && value().charAt(len - 1) == '\n';
    }

    /**
     * Get whether this text ends with a CRLF.
     * @return {@code true} if this text is terminated by an CRLF
     */
    default boolean isEndWithCrLf() {
        int len = value().length();
        return (len > 1) && value().charAt(len - 2) == '\r' && value().charAt(len - 1) == '\n';
    }

    /**
     * Get the number of characters excluding newline codes.
     * <pre>
     *     | a | b | c |\r |\n | -> textLength: 3
     *     | a | b | c |\n |     -> textLength: 3
     *     | a | b | c |         -> textLength: 3
     * </pre>
     * @return the number of characters excluding newline codes
     */
    default int textLength() {
        int len = value().length();
        return isEndWithCrLf() ? len - 2 : isEndWithLf() ? len - 1 : len;
    }

    /**
     * Gets whether the character at the specified index is Surrogate or not.
     * @param index the specified index
     * @return {@code true} if the character at the specified index is Surrogate
     */
    default boolean isSurrogate(int index) {
        if (0 > index || index >= value().length()) return false;
        return Character.isSurrogate(value().charAt(index));
    }

    /**
     * Gets whether the character at the specified index is High Surrogate or not.
     * @param index the specified index
     * @return {@code true} if the character at the specified index is High Surrogate
     */
    default boolean isHighSurrogate(int index) {
        if (0 > index || index >= value().length()) return false;
        return Character.isHighSurrogate(value().charAt(index));
    }

    /**
     * Get whether the character at the specified index is Low Surrogate or not.
     * @param index the specified index
     * @return {@code true} if the character at the specified index is Low Surrogate
     */
    default boolean isLowSurrogate(int index) {
        if (0 > index || index >= value().length()) return false;
        return Character.isLowSurrogate(value().charAt(index));
    }

    /**
     * Get the index moved to the right of the specified index.
     * Surrogate pair consideration.
     * @param index the base index
     * @return the index moved to the right, {@code -1} if the specified index is at the end of a line
     */
    default int indexRight(int index) {
        if (isEmpty() || index == value().length()) return -1;
        index += isHighSurrogate(index) ? 2 : 1;
        return (index > textLength()) ? -1 : index;
    }

    /**
     * Get the index moved to the right bound of the specified index.
     * <pre>
     *   | a | b | c | 1 | 2 |
     *       ^       |
     *       index   |
     *              right bound
     * </pre>
     * @param index the base index
     * @return the index moved to the right bound
     */
    default int indexRightBound(int index) {
        if (isEmpty() || index == value().length()) return -1;
        for (int i = index; i < value().length(); ) {
            int charType = Character.getType(Character.toLowerCase(value().charAt(i)));
            int nextIndex = indexRight(i);
            if (nextIndex <= i || nextIndex >= value().length()) {
                return nextIndex;
            }
            i = nextIndex;
            int nextType = Character.getType(Character.toLowerCase(value().charAt(i)));
            if (charType != nextType) {
                return i;
            }
        }
        return index;
    }

    /**
     * Get the index moved to the left of the specified index.
     * Surrogate pair consideration.
     * @param index the base index
     * @return the index moved to the left
     */
    default int indexLeft(int index) {
        if (index <= 0) return 0;
        index -= isLowSurrogate(index - 1) ? 2 : 1;
        return index;
    }

    /**
     * Get the index moved to the left bound of the specified index.
     * <pre>
     *   | a | b | 1 | 2 | 3 |
     *           |       ^
     *           |       index
     *           left bound
     * </pre>
     * @param index the base index
     * @return the index moved to the left bound
     */
    default int indexLeftBound(int index) {
        if (index <= 0) return 0;
        for (int i = index; i >= 0; ) {
            int charType = Character.getType(Character.toLowerCase(value().charAt(i - 1)));
            i = indexLeft(i);
            if (i == 0) {
                return i;
            }
            int nextType = Character.getType(Character.toLowerCase(value().charAt(i - 1)));
            if (charType != nextType) {
                return i;
            }
        }
        return index;
    }

    /**
     * Get the width of the string up to the specified index.
     * @param index the specified index
     * @return the width
     */
    default double widthTo(int index) {
        double[] ad = advances();
        return Arrays.stream(ad, 0, Math.clamp(index, 0, ad.length)).sum();
    }

    /**
     * Get the index corresponding to the specified width.
     * @param width the specified width
     * @return the index
     */
    default int indexTo(double width) {
        double[] ad = advances();
        double w = 0;
        for (int i = 0; i < ad.length; i++) {
            if (w + ad[i] > width) return i;
            w += ad[i];
        }
        return Math.min(ad.length, textLength());
    }

    /**
     * Checks if the underlying value is empty.
     * @return {@code true} if the value is empty
     */
    default boolean isEmpty() {
        return value().isEmpty();
    }

    /**
     * Get the word-separated text list.
     * @return the word-separated text list
     */
    default List<Text> words() {
        List<Text> ret = new ArrayList<>();
        var text = value();
        BreakIterator boundary = BreakIterator.getWordInstance();
        boundary.setText(text);
        int start = boundary.first();
        for (int end = boundary.next();
             end != BreakIterator.DONE;
             start = end, end = boundary.next()) {
            // dot splitting in addition to BreakIterator
            var bounded = text.substring(start, end);
            int stack = 0;
            for (String str : bounded.splitWithDelimiters("\\.|,", -1)) {
                ret.add(Text.of(
                    row(),
                    str,
                    Arrays.copyOfRange(advances(), start + stack, start + stack + str.length()),
                    height()));
                stack += str.length();
            }
        }
        return ret;
    }

    /**
     * Create a new Text instance.
     * @param row the number of rows
     * @param value the text value
     * @param advances the advances
     * @param height the height
     * @return a new Text instance
     */
    static Text of(int row, String value, double[] advances, double height) {
        record TextRecord(int row, String value, double[] advances, double width, double height) implements Text { }
        return new TextRecord(row, value, advances, Arrays.stream(advances).sum(), height);
    }

}
