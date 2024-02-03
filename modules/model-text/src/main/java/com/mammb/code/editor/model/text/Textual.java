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
package com.mammb.code.editor.model.text;

import com.mammb.code.editor.model.text.impl.TextualRecord;

/**
 * Textual.
 * @author Naotsugu Kobayashi
 */
public interface Textual extends OffsetPointer {

    /**
     * Get the text string.
     * @return the text string.
     */
    String text();

    @Override
    OffsetPoint offsetPoint();

    /**
     * Get the start char (total) offset.
     * @return the start char (total) offset.
     */
    default long offset() {
        return offsetPoint().offset();
    }

    /**
     * Get the char offset of tail(exclusive).
     * @return the offset of tail(exclusive).
     */
    default long tailOffset() {
        return offset() + length();
    }

    /**
     * Get the point of tail(exclusive).
     * @return the point of tail(exclusive)
     */
    default OffsetPoint tailPoint() {
        return OffsetPoint.of(offsetPoint().row(), tailOffset(), tailCpOffset());
    }

    /**
     * Get the point of last(for inclusive).
     * <pre>
     *     | 0 | 1 | 2 |   : 2 (tailPoint :3 )
     *     | 0 |           : 0 (tailPoint :1 )
     * </pre>
     * @return the point of last(for inclusive)
     */
    default OffsetPoint lastPoint() {
        var text = text();
        return (text == null || text.isEmpty()) ? tailPoint()
            : tailPoint().minus(String.valueOf(text.charAt(text.length() - 1)));
    }

    /**
     * Get the length of the text.
     * @return the length of the text
     */
    default int length() {
        var text = text();
        return (text == null) ? 0 : text.length();
    }

    /**
     * Get whether this textual is empty.
     * @return {@code true}, if this textual is empty
     */
    default boolean isEmpty() {
        return length() == 0;
    }

    /**
     * Get the coed point offset of tail.
     * @return the coed point offset of tail.
     */
    default long tailCpOffset() {
        return offsetPoint().cpOffset() + Character.codePointCount(text(), 0, length());
    }

    /**
     * Compare offset range.
     * <pre>
     *   -1 |   0    |  1
     *      |3|4|5|6|
     *
     *    point().offset():3
     *    text().length() :4
     *    tailOffset()    :7
     * </pre>
     * @param offset offset
     * @return the value {@code 0} if {@code point().offset() <= offset && offset < tailOffset()};
     *         a value less than {@code 0} if {@code offset < point().offset()}; and
     *         a value greater than {@code 0} if {@code tailOffset() <= offset}
     */
    default int compareOffsetRangeTo(long offset) {
        if (offset < offsetPoint().offset()) {
            return -1;
        } else if (tailOffset() <= offset) {
            return 1;
        } else {
            return 0;
        }
    }


    /**
     * Get the count of end mark.
     * @return the count of end mark
     */
    default int endMarkCount() {
        String text = text();
        if (text.endsWith("\r\n")) return 2;
        if (text.endsWith("\n")) return 1;
        return 0;
    }


    /**
     * Create a new PointText
     * @param point the offset point
     * @param text the text string
     * @return a new PointText
     */
    static Textual of(OffsetPoint point, String text) {
        return new TextualRecord(point, text);
    }

}
