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
package com.mammb.code.editor.model.text.impl;

import com.mammb.code.editor.model.text.OffsetPoint;

/**
 * The implementation of {@link OffsetPoint}.
 *
 * @param row the number of row(zero based)
 * @param offset the offset of content(char base)
 * @param cpOffset the code point offset of content
 * @author Naotsugu Kobayashi
 */
public record OffsetPointRecord(
        int row,
        int offset,
        int cpOffset) implements OffsetPoint {

    /**
     * Plus the offset.
     * @param that the offset
     * @return the offset
     */
    @Override
    public OffsetPointRecord plus(OffsetPoint that) {
        return new OffsetPointRecord(row + that.row(), offset + that.offset(), cpOffset + that.cpOffset());
    }


    /**
     * Minus the offset.
     * @param that the offset
     * @return the offset
     */
    @Override
    public OffsetPoint minus(OffsetPoint that) {
        return new OffsetPointRecord(row - that.row(), offset - that.offset(), cpOffset - that.cpOffset());
    }


    /**
     * Plus offset.
     * @param str the text string
     * @return the new offset point
     */
    @Override
    public OffsetPointRecord plus(String str) {
        return new OffsetPointRecord(
            row + countRow(str),
            offset + str.length(),
            cpOffset + Character.codePointCount(str, 0, str.length()));
    }


    /**
     * Minus offset.
     * @param str the text string
     * @return the new offset point
     */
    @Override
    public OffsetPointRecord minus(String str) {
        return new OffsetPointRecord(
            row - countRow(str),
            offset - str.length(),
            cpOffset - Character.codePointCount(str, 0, str.length()));
    }


    /**
     * Count the number of line feed in the specified char sequence.
     * @param cs the specified char sequence
     * @return the number of line feed
     */
    private static int countRow(CharSequence cs) {
        return (cs == null) ? 0 : (int) cs.chars().filter(c -> c == '\n').count();
    }

}
