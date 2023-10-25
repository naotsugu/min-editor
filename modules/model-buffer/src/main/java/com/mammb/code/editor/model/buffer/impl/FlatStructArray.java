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

    /** The number of elements in a unit. */
    private final int unit;

    /** The internal flatten array. */
    private long[] array;

    /** The length of element. */
    private int length;

    /** The value modified stack. */
    private long[] valueModifiedStack;


    /**
     * Constructor.
     * @param unit the number of elements in a unit
     * @param initial the initial capacity
     */
    public FlatStructArray(int unit, int initial) {
        if (unit <= 0) {
            throw new IllegalArgumentException();
        }
        this.unit = unit;
        this.array = new long[initial * unit];
        this.length = 0;
        this.valueModifiedStack = new long[unit];
    }


    /**
     * Constructor.
     * @param unit the number of elements in a unit
     */
    public FlatStructArray(int unit) {
        this(unit, 5);
    }


    /**
     * Set the elements.
     * @param index the index to set element
     * @param values the element values
     */
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


    /**
     * Add an element at the end.
     * @param values the element values
     */
    public void add(long... values) {
        set(length, values);
    }


    /**
     * Get the elements at the specified position in this array.
     * @param index the index of the element
     * @return the elements
     */
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
                valueModifiedStack[j] += Math.abs(deltas[j]);
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

        low = Math.clamp(low, 0, length - 1);
        high = Math.clamp(high, 0, length - 1);

        return (Math.abs(get(low)[offset] - key) <= Math.abs(get(high)[offset] - key))
            ? low : high;
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
