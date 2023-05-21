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

import com.mammb.code.editor2.model.buffer.StyledTextBuffer;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.core.PointText;
import com.mammb.code.editor2.model.core.Translate;
import com.mammb.code.editor2.model.style.Style;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.style.impl.StyledSubText;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

/**
 * StyledBuffer.
 * @author Naotsugu Kobayashi
 */
public class StyledBuffer implements StyledTextBuffer {

    /** The pear slice. */
    private final TextBuffer<PointText> buffer;

    /** The styling translate. */
    private final Translate<PointText, StyledText> stylingTranslate;

    private int lineOffset = 0;

    /** The text rows. */
    private List<StyledText> rows = new ArrayList<>();

    /** The text lines(text wrapped). */
    private List<StyledText> lines = new ArrayList<>();


    public StyledBuffer(
            TextBuffer<PointText> buffer,
            Translate<PointText, StyledText> stylingTranslate) {
        this.buffer = buffer;
        this.stylingTranslate = stylingTranslate;
    }


    @Override
    public List<StyledText> texts() {
        return lines;
    }


    private void pullRows() {
        rows = buffer.texts().stream()
            .map(stylingTranslate::applyTo)
            .toList();
        lines = rows.stream()
            .map(this::breakToLine)
            .flatMap(Collection::stream)
            .toList();
    }


    private List<StyledText> breakToLine(StyledText row) {

        int[] breakPoints = row.points(Style.BreakPoint.class);
        if (breakPoints.length == 0) {
            return List.of(row);
        }

        List<StyledText> lines = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < breakPoints.length; i++) {
            var sub = StyledSubText.of(row, start, breakPoints[i] - start);
            lines.add(sub);
            start = breakPoints[i];
        }
        return lines;
    }

}
