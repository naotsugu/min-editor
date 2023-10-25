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
 * FlatStructArray.
 * @author Naotsugu Kobayashi
 */
public class FlatStructArray {

    /** The number of elements. */
    private final int unit;

    /** The internal flatten array. */
    private long[] array;

    /** The length of element. */
    private int length;

    private long[] modified;


    public FlatStructArray(int unit, int initial) {
        if (unit <= 0) {
            throw new IllegalArgumentException();
        }
        this.unit = unit;
        this.array = new long[initial * unit];
        this.length = 0;
        this.modified = new long[unit];
    }


    public FlatStructArray(int unit) {
        this(unit, 5);
    }


    public void set(int index, long... values) {
        if (values.length != unit) {
            throw new IllegalArgumentException();
        }
        if (array.length < (index + 1) * unit) {
            grow((index + 1) * unit);
        }
        int physicalIndex = index * unit;
        for (long value : values) {
            array[physicalIndex++] = value;
        }
        length = Math.max(length, index + 1);
    }


    public void add(long... values) {
        set(length, values);
    }


    public long[] get(int index) {
        if (index >= length) {
            throw new IndexOutOfBoundsException();
        }
        long[] ret = new long[unit];
        int physicalIndex = index * unit;
        for (int i = 0; i < ret.length; i++) {
            ret[i] = array[physicalIndex++];
        }
        return ret;
    }


    public void plusValuesAfter(int fromIndexExclude, long... deltas) {
        plusValues(fromIndexExclude + 1, deltas);
    }


    public void plusValues(int fromIndex, long... deltas) {

        if (fromIndex >= length) {
            throw new IndexOutOfBoundsException();
        }
        if (deltas.length != unit) {
            throw new IllegalArgumentException();
        }

        int physicalIndex = fromIndex * unit;
        for (int i = physicalIndex; i < array.length; i += unit) {
            for (int j = 0; j < deltas.length; j++) {
                modified[i] += Math.abs(deltas[j]);
                array[i + j] += deltas[j];
            }
        }

    }


    public int binarySearch(int offset, long key) {

        if (offset >= unit) {
            throw new IllegalArgumentException();
        }

        int low = 0;
        int high = length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            long midVal = get(mid)[offset];
            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return Math.clamp(low, 0, length - 1);
    }


    /**
     * Get the unit.
     * @return the unit
     */
    public int unit() {
        return unit;
    }


    /**
     * Get the length.
     * @return the length
     */
    public int length() {
        return length;
    }


    /**
     * Grow this array.
     * @param minCapacity the growth capacity
     * @return the grown array
     */
    private long[] grow(int minCapacity) {
        int oldCapacity = array.length;
        int newCapacity = Math.clamp(oldCapacity >> 1, minCapacity, Integer.MAX_VALUE - 8);
        int remainder = newCapacity % unit;
        if (remainder != 0) {
            newCapacity -= newCapacity;
        }
        return array = (length == 0)
            ? new long[newCapacity]
            : Arrays.copyOf(array, newCapacity);
    }
}
