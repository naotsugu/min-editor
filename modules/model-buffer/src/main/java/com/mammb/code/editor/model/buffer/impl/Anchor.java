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
package com.mammb.code.editor.model.buffer.impl;

import java.util.Arrays;

/**
 * Anchor.
 * @author Naotsugu Kobayashi
 */
public class Anchor {

    /** The span of anchor. */
    private final int span;

    /** The row anchor flatten table. */
    private long[] table;

    /** The length of array. */
    private int length;


    public Anchor(int initialEntryCapacity, int span) {
        if (initialEntryCapacity <= 0) {
            initialEntryCapacity = 5;
        }
        this.table = new long[initialEntryCapacity * 2];
        this.length = 0;
        this.span = span;
        push(0, 0, 0);
    }


    public void push(long anchorPoint, long value1, long value2) {
        if ((anchorPoint % span) != 0) {
            throw new IllegalArgumentException("illegal anchorPoint.[" + anchorPoint + "]");
        }
        // [0] anchorPoint    0  value1     0 / (1000 / 2) = 0
        // [1] anchorPoint    0  value2
        // [2] anchorPoint 1000  value1  1000 / (1000 / 2) = 2
        // [3] anchorPoint 1000  value2
        // [4] anchorPoint 2000  value1  2000 / (1000 / 2) = 4
        // [5] anchorPoint 2000  value2
        if (length >= table.length) {
            grow();
        }
        int index = Math.toIntExact(anchorPoint / (span >> 1));
        table[index] = value1;
        table[index + 1] = value2;
        length += 2;
    }


    public AnchorValue nearest(long point) {
        int n = Math.round(((float) point) / span) * span;
        int index = n / (span >> 1);
        if (index + 1 >= length) {
            index = length - 2;
        }
        return new AnchorValue(
            (index / 2) * span,
            table[index],
            table[index + 1]
        );
    }

    public record AnchorValue(long anchorPoint, long value1, long value2) {}


    /**
     * Get the span.
     * @return the span
     */
    public int span() {
        return span;
    }


    /**
     * Grow this array.
     * @return the grown array
     */
    private long[] grow() {
        int oldCapacity = table.length;
        int newCapacity = Math.clamp(oldCapacity >> 1, 10, Integer.MAX_VALUE - 8);
        if ((newCapacity & 1) != 0) {
            // fixup to even
            newCapacity += 1;
        }
        return table = (length == 0)
            ? new long[newCapacity]
            : Arrays.copyOf(table, newCapacity);
    }

}
