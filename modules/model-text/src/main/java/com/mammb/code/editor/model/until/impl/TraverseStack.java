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
package com.mammb.code.editor.model.until.impl;

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.until.Traverse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * TraverseStack.
 * @author Naotsugu Kobayashi
 */
public class TraverseStack implements Traverse {

    /** The bytes. */
    private byte[] bytes;
    /** The byte length. */
    private int bytesLength;
    /** The count of rows. */
    private int rowCount;
    /** The count of character. */
    private int chCount;
    /** The count of code point. */
    private int cpCount;


    /**
     * Constructor.
     */
    private TraverseStack(byte[] bytes, int rowCount, int chCount, int cpCount) {
        this.bytes = bytes;
        this.bytesLength = 0;
        this.rowCount = rowCount;
        this.chCount = chCount;
        this.cpCount = cpCount;
    }


    /**
     * Create a new TraverseStack.
     * @param initialCapacity the initial capacity
     * @return a new TraverseStack
     */
    public static TraverseStack of(int initialCapacity) {
        return new TraverseStack(new byte[initialCapacity], 0, 0, 0);
    }


    /**
     * Create a new TraverseStack.
     * @return a new TraverseStack
     */
    public static TraverseStack of() {
        return TraverseStack.of(128 * 4);
    }


    /**
     * Create a new traverse backward.
     * @param base the base offset point
     */
    public static Traverse of(OffsetPoint base) {
        return new TraverseStack(new byte[128 * 4],
            base.row(), base.offset(), base.cpOffset());
    }


    @Override
    public void accept(byte[] utf8Bytes) {

        if (utf8Bytes.length > 4) {
            throw new IllegalArgumentException();
        }

        cpCount++;
        chCount += Bytes.lengthByteAsUtf16(utf8Bytes[0]);
        if (utf8Bytes[0] == '\n') {
            rowCount++;
        }

        byte[] arr = bytes;
        if (arr.length < bytesLength + utf8Bytes.length) {
            arr = grow(bytesLength + utf8Bytes.length);
        }
        for (byte value : utf8Bytes) {
            arr[bytesLength++] = value;
        }
    }


    @Override
    public OffsetPoint asOffsetPoint() {
        return OffsetPoint.of(rowCount, chCount, cpCount);
    }


    /**
     * Get the result of traverse as String
     * @return the string
     */
    public String asString() {
        return new String(Arrays.copyOf(bytes, bytesLength), StandardCharsets.UTF_8);
    }


    /**
     * Clear.
     */
    public void clear() {
        bytesLength = 0;
        rowCount = 0;
        chCount = 0;
        cpCount = 0;
    }


    /**
     * Grow this array.
     * @param minCapacity the growth capacity
     * @return the grown array
     */
    private byte[] grow(int minCapacity) {
        int oldCapacity = bytes.length;
        int newCapacity = Math.clamp(oldCapacity >> 1, minCapacity, Integer.MAX_VALUE - 8);
        return bytes = (bytesLength == 0)
            ? new byte[newCapacity]
            : Arrays.copyOf(bytes, newCapacity);
    }

}
