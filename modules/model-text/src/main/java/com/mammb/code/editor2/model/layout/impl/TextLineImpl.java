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
package com.mammb.code.editor2.model.layout.impl;

import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.text.OffsetPoint;
import java.util.List;

/**
 * TextLineRecord.
 * @author Naotsugu Kobayashi
 */
public class TextLineImpl implements TextLine {

    /** The offsetPoint. */
    private final OffsetPoint point;
    /** The char length. */
    private final int length;
    /** The height of line. */
    private final double height;
    /** The text runs. */
    private final List<TextRun> runs;

    /**
     * Constructor.
     * @param point the offsetPoint
     * @param length the char length
     * @param height the height of line
     * @param textRuns the text runs
     */
    public TextLineImpl(OffsetPoint point, int length, double height, List<TextRun> textRuns) {
        this.point = point;
        this.length = length;
        this.height = height;
        this.runs = textRuns.stream().map(r -> TextRun.of(this, r)).toList();
    }

    @Override
    public OffsetPoint point() {
        return point;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public List<TextRun> runs() {
        return runs;
    }

    @Override
    public double height() {
        return height;
    }

}
