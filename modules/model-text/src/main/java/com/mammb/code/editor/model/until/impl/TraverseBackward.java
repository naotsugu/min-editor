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

/**
 * TraverseBackward.
 * @author Naotsugu Kobayashi
 */
public class TraverseBackward implements Traverse {

    /** The count of rows. */
    private int rows;
    /** The count of character. */
    private int chCount;
    /** The count of code point. */
    private int cpCount;


    /**
     * Constructor.
     * @param rows the count of rows
     * @param chCount the count of character
     * @param cpCount the count of code point
     */
    private TraverseBackward(int rows, int chCount, int cpCount) {
        this.rows = rows;
        this.chCount = chCount;
        this.cpCount = cpCount;
    }


    /**
     * Create a new traverse backward.
     * @param base the base offset point
     */
    public TraverseBackward(OffsetPoint base) {
        this(base.row(), base.offset(), base.cpOffset());
    }


    @Override
    public void accept(byte[] utf8Bytes) {
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
