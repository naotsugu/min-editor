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
package com.mammb.code.editor.model.until.impl;

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.until.Traverse;

/**
 * TraverseBackward.
 * @author Naotsugu Kobayashi
 */
public class TraverseBackward implements Traverse {

    /** The count of rows. */
    private int rows;
    /** The count of character. */
    private long chCount;
    /** The count of code point. */
    private long cpCount;


    /**
     * Constructor.
     * @param rows the count of rows
     * @param chCount the count of character
     * @param cpCount the count of code point
     */
    private TraverseBackward(int rows, long chCount, long cpCount) {
        this.rows = rows;
        this.chCount = chCount;
        this.cpCount = cpCount;
    }


    /**
     * Create a new traverse backward.
     * @param base the base offset point
     */
    public static Traverse of(OffsetPoint base) {
        return new TraverseBackward(base.row(), base.offset(), base.cpOffset());
    }


    @Override
    public void accept(byte[] utf8Bytes) {
        if (utf8Bytes.length > 4) {
            throw new IllegalArgumentException();
        }
        cpCount--;
        chCount -= Bytes.lengthByteAsUtf16(utf8Bytes[0]);
        if (utf8Bytes[0] == '\n') {
            rows--;
        }
    }

    @Override
    public OffsetPoint asOffsetPoint() {
        return OffsetPoint.of(rows, chCount, cpCount);
    }

}
