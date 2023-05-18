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
package com.mammb.code.editor2.model.core;

import com.mammb.code.editor2.model.core.impl.PointTextRecord;

/**
 * PointText.
 * @author Naotsugu Kobayashi
 */
public interface PointText extends Textual {

    /**
     * Get the offset point.
     * @return the offset point.
     */
    OffsetPoint point();

    /**
     * Get the offset of tail.
     * @return the offset of tail.
     */
    default int tailOffset() {
        return point().offset() + text().length();
    }

    /**
     * Get the coed point offset of tail.
     * @return the coed point offset of tail.
     */
    default int tailCpOffset() {
        return point().cpOffset() + Character.codePointCount(text(), 0, text().length());
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
    default int compareOffsetRangeTo(int offset) {
        if (offset < point().offset()) {
            return -1;
        } else if (tailOffset() <= offset) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Create a new PointText
     * @param point the offset point
     * @param text the text string
     * @return a new PointText
     */
    static PointText of(OffsetPoint point, String text) {
        return new PointTextRecord(point, text);
    }

}
