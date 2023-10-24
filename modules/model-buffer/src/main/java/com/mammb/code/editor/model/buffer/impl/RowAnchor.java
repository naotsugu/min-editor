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

import com.mammb.code.editor.model.text.OffsetPoint;
import java.util.Arrays;

/**
 * RowAnchor.
 * @author Naotsugu Kobayashi
 */
public class RowAnchor {

    /** The span of anchor. */
    private final int span = 1000;

    /** The row anchor flatten table. */
    private long[] table;

    /** The length of array. */
    private int length;


    public RowAnchor(int initialEntryCapacity) {
        if (initialEntryCapacity <= 0) {
            initialEntryCapacity = 5;
        }
        this.table = new long[initialEntryCapacity * 2];
        this.length = 0;
        push(0, 0, 0);
    }


    public void push(int row, long chOffset, long cpOffset) {
        if ((row % span) != 0) {
            throw new IllegalArgumentException("illegal row " + row);
        }
        // [0] row    0 chOffset    0 / (1000 / 2) = 0
        // [1] row    0 cpOffset
        // [2] row 1000 chOffset 1000 / (1000 / 2) = 2
        // [3] row 1000 cpOffset
        // [4] row 2000 chOffset 2000 / (1000 / 2) = 4
        // [5] row 2000 cpOffset
        if (length >= table.length) {
            grow();
        }
        int index = row / (span >> 1);
        table[index] = chOffset;
        table[index + 1] = cpOffset;
        length += 2;
    }


    public OffsetPoint nearest(int row) {
        int n = Math.round(((float) row) / span) * span;
        int index = n / (span >> 1);
        if (index + 1 >= length) {
            index = length - 2;
        }
        return OffsetPoint.of(
            (index / 2) * span,
            (int) table[index],
            (int) table[index + 1]
        );
    }


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
