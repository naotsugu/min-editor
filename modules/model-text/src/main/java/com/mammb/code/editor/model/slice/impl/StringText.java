/*
 * Copyright 2023-2024 the original author or authors.
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
package com.mammb.code.editor.model.slice.impl;

import com.mammb.code.editor.model.slice.RowSupplier;
import com.mammb.code.editor.model.until.Until;
import java.nio.charset.StandardCharsets;

/**
 * StringText.
 * @author Naotsugu Kobayashi
 */
public class StringText implements RowSupplier {

    /** The string. */
    private final String string;

    /** The codepoint count. */
    private final int codePointCount;


    /**
     * Constructor.
     * @param string the string.
     */
    public StringText(String string) {
        this.string = string;
        this.codePointCount = Character.codePointCount(string, 0, string.length());
    }


    @Override
    public String at(long cpOffset) {
        if (cpOffset < 0 || cpOffset > codePointCount - 1) {
            return null;
        }
        int[] codePoints = string.codePoints().skip(cpOffset).toArray();
        String tail = new String(codePoints, 0, codePoints.length);
        int to = tail.indexOf('\n');
        return (to > 0) ? tail.substring(0, to + 1) : tail;
    }


    @Override
    public String before(long cpOffset) {
        if (cpOffset <= 0 || cpOffset > codePointCount) {
            return null;
        }
        int[] codePoints = string.codePoints().limit(cpOffset).toArray();
        String head = new String(codePoints, 0, codePoints.length);
        int from = head.lastIndexOf('\n') + 1;
        if (from == head.length()) {
            from = head.substring(0, head.length() - 1).lastIndexOf('\n') + 1;
        }
        return (from <= 0) ? head : head.substring(from);
    }


    @Override
    public long offset(long startCpOffset, Until<byte[]> until) {
        int[] codePoints = string.codePoints().skip(startCpOffset).toArray();
        for (int i = 0; i < codePoints.length; i++) {
            if (until.test(Character.toString(codePoints[i])
                    .getBytes(StandardCharsets.UTF_8))) {
                return startCpOffset + i;
            }
        }
        return codePointCount;
    }


    @Override
    public long offsetBefore(long startCpOffsetLong, Until<byte[]> until) {
        int startCpOffset = (int) startCpOffsetLong;
        int[] codePoints = string.codePoints().skip(startCpOffset).toArray();
        for (int i = startCpOffset; i >= 0; i--) {
            if (until.test(Character.toString(codePoints[i])
                    .getBytes(StandardCharsets.UTF_8))) {
                return startCpOffset - i;
            }
        }
        return 0;
    }

    @Override
    public long cpLength() {
        return codePointCount;
    }

}
