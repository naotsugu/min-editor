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
package com.mammb.code.editor.model.layout.impl;

import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.model.text.OffsetPoint;
import java.util.List;
import java.util.StringJoiner;

/**
 * TextLineRecord.
 * @author Naotsugu Kobayashi
 */
public class TextLineImpl implements TextLine {

    /** The offsetPoint. */
    private final OffsetPoint point;
    /** The line index. */
    private final int lineIndex;
    /** The line size. */
    private final int lineSize;
    /** The char length. */
    private final int length;
    /** The width of line. */
    private final double width;
    /** The height of line. */
    private final double height;
    /** The text runs. */
    private final List<TextRun> runs;

    /**
     * Constructor.
     * @param point the offsetPoint
     * @param lineIndex the line index
     * @param lineSize the line size
     * @param length the char length
     * @param width the width of line
     * @param height the height of line
     * @param textRuns the text runs
     */
    public TextLineImpl(OffsetPoint point, int lineIndex, int lineSize,
            int length, double width, double height, List<TextRun> textRuns) {
        this.point = point;
        this.lineIndex = lineIndex;
        this.lineSize = lineSize;
        this.length = length;
        this.width = width;
        this.height = height;
        this.runs = textRuns.stream().map(r -> TextRun.of(this, r)).toList();
    }

    @Override
    public OffsetPoint offsetPoint() {
        return point;
    }

    @Override
    public int lineIndex() {
        return lineIndex;
    }

    @Override
    public int lineSize() {
        return lineSize;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public double width() {
        return width;
    }

    @Override
    public List<TextRun> runs() {
        return runs;
    }

    @Override
    public double height() {
        return height;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TextLineImpl.class.getSimpleName() + "[", "]")
            .add("point=" + point)
            .add("lineIndex=" + lineIndex)
            .add("length=" + length)
            .add("width=" + width)
            .add("height=" + height)
            .toString();
    }
}
