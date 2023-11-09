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
 * FlatElementsArray.
 *
 * <pre>
 *  struct { long row; long offset; long cpOffset; } elms[2];
 *
 *  array[0]  elms[0].row
 *  array[1]  elms[0].offset
 *  array[2]  elms[0].cpOffset
 *  array[3]  elms[1].row
 *  array[4]  elms[1].offset
 *  array[5]  elms[1].cpOffset
 *
 *  numberOfElements:3
 *  length:2
 *
 *  set(1, 10, 20, 30)
 *   array[3] = 10
 *   array[4] = 20
 *   array[5] = 30
 *
 * </pre>
 *
 * @author Naotsugu Kobayashi
 */
public class FlattenStructArray {

    /** The number of elements in a unit. */
    private final int numberOfElements;

    /** The internal flatten array. */
    private long[] array;

    /** The length of element. */
    private int length;

    /** The value modified stack. */
    private final long[] valueModifiedStack;


    /**
     * Constructor.
     * @param numberOfElements the number of elements in a unit
     * @param initialCapacity the initial capacity
     */
    public FlattenStructArray(int numberOfElements, int initialCapacity) {
        if (numberOfElements <= 0) {
            throw new IllegalArgumentException();
        }
        this.numberOfElements = numberOfElements;
        this.array = new long[initialCapacity * numberOfElements];
        this.length = 0;
        this.valueModifiedStack = new long[numberOfElements];
    }


    /**
     * Constructor.
     * @param unit the number of elements in a unit
     */
    public FlattenStructArray(int unit) {
        this(unit, 5);
    }


    /**
     * Set the elements.
     * @param index the index to set element
     * @param values the element values
     */
    public void set(int index, long... values) {
        if (values.length != numberOfElements) {
            throw new IllegalArgumentException();
        }
        long[] arr = array;
        if (arr.length < (index + 1) * numberOfElements) {
            arr = grow((index + 1) * numberOfElements);
        }
        int physicalIndex = index * numberOfElements;
        for (long value : values) {
            arr[physicalIndex++] = value;
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
        long[] ret = new long[numberOfElements];
        int physicalIndex = index * numberOfElements;
        for (int i = 0; i < ret.length; i++) {
            ret[i] = array[physicalIndex++];
        }
        return ret;
    }


    /**
     * Plus values to all elements after the specified index.
     * @param fromIndex the specified index
     * @param deltas the amount to be added
     */
    public void plusValues(int fromIndex, long... deltas) {

        if (fromIndex >= length) {
            throw new IndexOutOfBoundsException();
        }
        if (deltas.length != numberOfElements) {
            throw new IllegalArgumentException();
        }

        int physicalIndex = fromIndex * numberOfElements;
        for (int i = physicalIndex; i < array.length; i += numberOfElements) {
            for (int j = 0; j < deltas.length; j++) {
                valueModifiedStack[j] += Math.abs(deltas[j]);
                array[i + j] += deltas[j];
            }
        }

    }


    /**
     * Get the index with the closest value to the specified element.
     * @param offset the offset of the value.
     * @param key the value to be compared
     * @return the index value
     */
    public int binarySearch(int offset, long key) {

        if (offset >= numberOfElements) {
            throw new IllegalArgumentException();
        }
        if (length == 0) {
            return -1;
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
     * Get the number of elements.
     * @return the number of elements
     */
    public int numberOfElements() {
        return numberOfElements;
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
        int remainder = newCapacity % numberOfElements;
        if (remainder != 0) {
            newCapacity += remainder;
        }
        return array = (length == 0)
            ? new long[newCapacity]
            : Arrays.copyOf(array, newCapacity);
    }

}
