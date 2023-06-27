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
package com.mammb.code.editor2.model.layout;

import com.mammb.code.editor2.model.layout.impl.TextLineImpl;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TextLine.
 * @author Naotsugu Kobayashi
 */
public interface TextLine extends Textual {

    @Override
    default String text() {
        return runs().stream().map(TextRun::text).collect(Collectors.joining());
    }

    @Override
    OffsetPoint point();

    /**
     * Get the char length.
     * @return the char length
     */
    int length();

    /**
     * Get the height.
     * @return the height
     */
    double height();

    /**
     * Get the text runs.
     * @return the text runs
     */
    List<TextRun> runs();

    /**
     * Get the x position from the specified x offset.
     * @param offset the specified char offset(total)
     * @return the x position from the specified x offset
     */
    default double offsetToX(int offset) {
        int start = point().offset();
        int end   = start + length();
        if (start > offset || offset >= end) {
            throw new IndexOutOfBoundsException(
                "start:%d end:%d offset:%d".formatted(start, end, offset));
        }
        for (TextRun run : runs()) {
            if (run.length() == 0) continue;
            int runStart = run.source().point().offset() + run.start();
            int runEnd = runStart + run.length();
            if (runStart <= offset && offset < runEnd) {
                return run.offsetToX().apply(offset - run.offset());
            }
        }
        throw new IllegalStateException("internal error:" + runs());
    }

    /**
     * Get the char offset(total) from the specified x position.
     * @param x the specified x position
     * @return the char offset(total) from the specified x position
     */
    default int xToOffset(double x) {
        int offset = point().offset();
        for (TextRun run : runs()) {
            double runStart = run.layout().x();
            double runEnd   = runStart + run.layout().width();
            if (runStart <= x && x < runEnd) {
                offset += run.xToOffset().apply(x - run.layout().x());
                return offset;
            }
            offset += run.length();
        }
        return offset;
    }

    /**
     * Create a new TextLine.
     * @param point the OffsetPoint
     * @param length the char length
     * @param height the height
     * @param textRuns the text runs
     * @return a created TextLine
     */
    static TextLine of(OffsetPoint point, int length, double height, List<TextRun> textRuns) {
        return new TextLineImpl(point, length, height, textRuns);
    }

}
