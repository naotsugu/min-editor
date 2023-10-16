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

import java.util.function.Predicate;

/**
 * TraverseUntil.
 * @author Naotsugu Kobayashi
 */
public class TraverseUntil implements Predicate<byte[]> {

    /** The original line feed count. */
    private final int originalCount;
    /** The original exclusive flag. */
    private final boolean originalExclusive;
    /** The line feed count. */
    private int count;
    /** The exclusive flag. */
    private boolean exclusive;

    private int rows = 0;
    private int cpCount = 0;
    private int chCount = 0;


    /**
     * Constructor.
     * @param count the line feed count
     * @param exclusive the exclusive flag
     */
    private TraverseUntil(int count, boolean exclusive) {
        this.originalCount = count;
        this.originalExclusive = exclusive;
        reset();
    }


    public static TraverseUntil of(int count) {
        return new TraverseUntil(count, false);
    }


    public static TraverseUntil beforeOf(int count) {
        return new TraverseUntil(count, true);
    }

    @Override
    public boolean test(byte[] bytes) {

        if (bytes == null || bytes.length == 0) {
            return true;
        }

        if (bytes[0] == '\n') {
            count--;
        }
        cpCount++;

        boolean ret = exclusive && count <= 0;
        if (count <= 0) {
            exclusive = true;
        }
        if (!ret) {
            cpCount++;
            if (bytes[0] == '\n') {
                rows++;
            }
            chCount -= lengthByteAsUtf16(bytes[0]);
        }
        return ret;
    }

    public void reset() {
        count = originalCount;
        exclusive = originalExclusive;
        rows = 0;
        cpCount = 0;
        chCount = 0;
    }

    public int rows() {
        return rows;
    }

    public int cpCount() {
        return cpCount;
    }

    public int chCount() {
        return chCount;
    }

    /**
     * Get the number of bytes from the first byte of UTF-8 when expressed in UTF-16.
     * @param utf8FirstByte the first byte of UTF-8
     * @return the number of bytes when expressed in UTF-16
     */
    public static short lengthByteAsUtf16(byte utf8FirstByte) {
        if ((utf8FirstByte & 0x80) == 0x00) {
            return 1; // BMP
        } else if ((utf8FirstByte & 0xE0) == 0xC0) {
            return 1; // BMP
        } else if ((utf8FirstByte & 0xF0) == 0xE0) {
            return 1; // BMP
        } else if ((utf8FirstByte & 0xF8) == 0xF0) {
            return 2;
        } else {
            return 0;
        }
    }
}
