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

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link Bytes}.
 * @author Naotsugu Kobayashi
 */
class BytesTest {

    @Test void lengthByteAsUtf16() {
        assertEquals(1, Bytes.lengthByteAsUtf16("a".getBytes(StandardCharsets.UTF_8)[0]));
        assertEquals(1, Bytes.lengthByteAsUtf16("Ω".getBytes(StandardCharsets.UTF_8)[0]));
        assertEquals(1, Bytes.lengthByteAsUtf16("あ".getBytes(StandardCharsets.UTF_8)[0]));
        assertEquals(2, Bytes.lengthByteAsUtf16("😊".getBytes(StandardCharsets.UTF_8)[0]));
    }

    @Test void continuationByteCount() {
        assertEquals(0, Bytes.continuationByteCount("a".getBytes(StandardCharsets.UTF_8)[0]));
        assertEquals(1, Bytes.continuationByteCount("Ω".getBytes(StandardCharsets.UTF_8)[0]));
        assertEquals(2, Bytes.continuationByteCount("あ".getBytes(StandardCharsets.UTF_8)[0]));
        assertEquals(3, Bytes.continuationByteCount("😊".getBytes(StandardCharsets.UTF_8)[0]));
    }

}
