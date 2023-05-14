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
package com.mammb.code.editor2.model.buffer.impl;

import java.util.function.Predicate;

/**
 * Until utilities.
 * @author Naotsugu Kobayashi
 */
public class Until {

    /**
     * Create a new Until char length.
     * @return the Until char length
     */
    public static CharLen charLength(int length) {
        return new CharLen(length);
    }


    /**
     * Create a new Until LF.
     * @param count the line feed count
     * @return the Until LF predicate
     */
    public static LF lf(int count) {
        return new LF(count, true);
    }


    /**
     * Create a new Until LF.
     * @return the Until LF predicate
     */
    public static LF lf() {
        return new LF(1, true);
    }


    /**
     * Create a new Until LF.
     * @param count the line feed count
     * @return the Until LF predicate
     */
    public static LF lfInclusive(int count) {
        return new LF(count, false);
    }


    /**
     * Create a new Until LF.
     * @return the Until LF predicate
     */
    public static LF lfInclusive() {
        return new LF(1, false);
    }


    /** CharLen. */
    static class CharLen implements Predicate<byte[]> {

        /** The char count. */
        private int count;

        /**
         * Constructor.
         * @param count the line feed count
         */
        public CharLen(int count) {
            this.count = count;
        }

        @Override
        public boolean test(byte[] bytes) {
            count -= lengthByteAsUtf16(bytes[0]);
            return count < 0;
        }
    }


    /** LF. */
    static class LF implements Predicate<byte[]> {

        /** The line feed count. */
        private int count;

        /** The exclusive flag. */
        private boolean exclusive;

        /**
         * Constructor.
         * @param count the line feed count
         * @param exclusive the exclusive flag
         */
        public LF(int count, boolean exclusive) {
            if (count <= 0) throw new IllegalArgumentException();
            this.count = count;
            this.exclusive = exclusive;
        }

        @Override
        public boolean test(byte[] bytes) {
            if (bytes != null && bytes.length > 0 && bytes[0] == '\n') {
                count--;
            }
            boolean ret = exclusive && count <= 0;
            if (count <= 0) {
                exclusive = true;
            }
            return ret;
        }
    }


    /**
     * Get the number of bytes from the first byte of UTF-8 when expressed in UTF-16.
     * @param utf8FirstByte the first byte of UTF-8
     * @return the number of bytes when expressed in UTF-16
     */
    private static short lengthByteAsUtf16(byte utf8FirstByte) {
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
