/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.mammb.code.editor.model.until.impl;

/**
 * Utilities of bytes.
 * @author Naotsugu Kobayashi
 */
public class Bytes {

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
