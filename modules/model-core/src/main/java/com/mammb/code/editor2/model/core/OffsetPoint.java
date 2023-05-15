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

/**
 * Represents a position in the content.
 *
 * <pre>
 *  1: |a|b|c|    OffsetPoint(row:0, offset:0, cpOffset:0)
 *  2: |d|e|f|    OffsetPoint(row:1, offset:3, cpOffset:3)
 *     |g|😀|     OffsetPoint(row:1, offset:6, cpOffset:6)
 *  3: |i|j|k|    OffsetPoint(row:2, offset:9, cpOffset:8)
 * </pre>
 *
 * @author Naotsugu Kobayashi
 */
public interface OffsetPoint {

    /** zero. */
    OffsetPoint zero = of(0, 0, 0);


    /**
     * Plus the offset.
     * @param that the offset
     * @return the offset
     */
    OffsetPoint plus(OffsetPoint that);


    /**
     * Plus offset.
     * @param str the text string
     * @return the new offset point
     */
    OffsetPoint plus(String str);


    /**
     * Minus offset.
     * @param str the text string
     * @return the new offset point
     */
    OffsetPoint minus(String str);


    /**
     * Get the number of row(zero based).
     * @return the number of row
     */
    int row();


    /**
     * Get the offset of content(char base).
     * @return the offset of content
     */
    int offset();


    /**
     * Get the code point offset of content.
     * @return the code point offset of content
     */
    int cpOffset();


    /**
     * Create a new OffsetPoint.
     * @param row the number of row(zero based)
     * @param offset the offset of content(char base)
     * @param cpOffset the code point offset of content
     * @return a new OffsetPoint
     */
    static OffsetPoint of(int row, int offset, int cpOffset) {
        return new com.mammb.code.editor2.model.core.impl.OffsetPoint(row, offset, cpOffset);
    }

}
