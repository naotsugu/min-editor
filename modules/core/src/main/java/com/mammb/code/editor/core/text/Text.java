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
     * Get the number of row.
     * @return the number of row
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
        return Character.isSurrogate(value().charAt(index));
    }

    /**
     * Gets whether the character at the specified index is High Surrogate or not.
     * @param index the specified index
     * @return {@code true} if the character at the specified index is High Surrogate
     */
    default boolean isHighSurrogate(int index) {
        return Character.isHighSurrogate(value().charAt(index));
    }

    /**
     * Get whether the character at the specified index is Low Surrogate or not.
     * @param index the specified index
     * @return {@code true} if the character at the specified index is Low Surrogate
     */
    default boolean isLowSurrogate(int index) {
        return Character.isLowSurrogate(value().charAt(index));
    }

    /**
     * Get the index moved to the right of the specified index.
     * Surrogate pair consideration.
     * @param index the base index
     * @return the index moved to the right
     */
    default int indexRight(int index) {
        if (isEmpty() || index == value().length()) return index;
        index += isHighSurrogate(index) ? 2 : 1;
        return (index > textLength()) ? -1 : index;
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
     * Get the width of the string up to the specified index.
     * @param index the specified index
     * @return the width
     */
    default double widthTo(int index) {
        double[] ad = advances();
        return Arrays.stream(ad, 0, Math.min(index, ad.length)).sum();
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
     * Get whether this text is empty or not.
     * @return {@code true} if, and only if, {@link #length()} is {@code 0}.
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
            ret.add(Text.of(
                    row(),
                    text.substring(start, end),
                    Arrays.copyOfRange(advances(), start, end),
                    height()));
        }
        return ret;
    }

    /**
     * Create a new Text instance.
     * @param row the number of row
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
