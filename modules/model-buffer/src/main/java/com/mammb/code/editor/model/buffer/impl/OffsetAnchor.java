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

/**
 * OffsetAnchor.
 * @author Naotsugu Kobayashi
 */
public class OffsetAnchor {

    private FlatStructArray array;


    public OffsetAnchor() {
        array = new FlatStructArray(3);
    }

    public void put(int row, long chOffset, long cpOffset) {
        array.add(row, chOffset, cpOffset);
    }

    public void put(OffsetPoint point) {
        array.add(point.row(), point.offset(), point.cpOffset());
    }

    public OffsetPoint nearest(long row) {
        int index = array.binarySearch(0, row);
        long[] values = array.get(index);
        return OffsetPoint.of((int) values[0], (int) values[1], (int) values[2]);
    }


}
