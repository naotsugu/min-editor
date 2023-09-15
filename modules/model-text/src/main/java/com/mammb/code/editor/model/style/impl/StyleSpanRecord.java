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
package com.mammb.code.editor.model.style.impl;

import com.mammb.code.editor.model.style.Style;
import com.mammb.code.editor.model.style.StyleSpan;

import java.util.Objects;

/**
 * StyleSpan.
 * @param style the style
 * @param point the point of start
 * @param length the length of style
 * @author Naotsugu Kobayashi
 */
public record StyleSpanRecord(Style style, int point, int length) implements StyleSpan {

    public StyleSpanRecord {
        if (length < 0)
            throw new IllegalArgumentException();
        if (length == 0 && !(style instanceof Style.Boundary))
            throw new IllegalArgumentException();
        Objects.requireNonNull(style);
    }


    /**
     * Gets whether the specified index is included in the range of this span.
     * @param index the specified index
     * @return {@code true} if the specified index is included in the range of this span
     */
    public boolean inRange(int index) {
        if (style instanceof Style.Boundary) {
            return point == index;
        }
        return point <= index && index < point + length;
    }


    /**
     * Gets whether the specified range is included in the range of this span.
     * @param thatPoint the point of start
     * @param thatLength the length
     * @return {@code true} if the specified range is included in the range of this span
     */
    public boolean inRange(int thatPoint, int thatLength) {
        if (style instanceof Style.Boundary) {
            return thatPoint <= point && point <= thatPoint + thatLength;
            // return thatPoint <= point && point < thatPoint + thatLength;
        }
        final int thisStart = point;
        final int thisEnd   = point + length - 1;
        final int thatStart = thatPoint;
        final int thatEnd   = thatPoint + thatLength - 1;
        return thisStart <= thatEnd && thisEnd >= thatStart;
    }


    /**
     * Get the ending index, exclusive.
     * @return the ending index, exclusive
     */
    public int endExclusive() {
        return point + length;
    }

}
