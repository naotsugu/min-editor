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

import com.mammb.code.editor.model.buffer.Metrics;
import com.mammb.code.editor.model.text.OffsetPoint;

/**
 * OffsetAnchor.
 * @author Naotsugu Kobayashi
 */
public class OffsetAnchor {

    private int span = 50_000;

    /** The high-water mark. */
    private long hwm = 0;

    /** The array. */
    private FlattenStructArray array;


    /**
     * Constructor.
     */
    public OffsetAnchor() {
        array = new FlattenStructArray(3);
        put(0, 0, 0);
    }


    public void put(int row, long chOffset, long cpOffset) {
        if (cpOffset > hwm + span) {
            array.add(row, chOffset, cpOffset);
            hwm = cpOffset;
        }
    }


    public void put(Metrics metrics) {
        put(metrics.lfCount(), metrics.chCount(), metrics.cpCount());
    }


    public OffsetPoint closestAnchorPoint(long row) {
        int index = array.binarySearch(0, row);
        long[] values = array.get(index);
        return OffsetPoint.of((int) values[0], (int) values[1], (int) values[2]);
    }


    public void edited(long cpOffset,
            int rowDelta, long chOffsetDelta, long cpOffsetDelta) {
        int index = array.binarySearch(2, cpOffset);
        if (array.get(index)[2] <= cpOffset) {
            index++;
        }
        if (index >= array.length()) {
            return;
        }
        array.plusValues(index, rowDelta, chOffsetDelta, cpOffsetDelta);
    }

}
