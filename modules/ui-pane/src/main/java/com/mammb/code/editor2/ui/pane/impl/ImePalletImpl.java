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
package com.mammb.code.editor2.ui.pane.impl;

import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.ui.pane.ImePallet;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ImePalletImpl.
 * @author Naotsugu Kobayashi
 */
public class ImePalletImpl implements ImePallet {

    private OffsetPoint offsetPoint;

    private List<Run> runs = List.of();

    private static final double width = 1.0;

    public ImePalletImpl() {
    }

    @Override
    public void on(OffsetPoint offsetPoint) {
        this.offsetPoint = Objects.requireNonNull(offsetPoint);
    }

    @Override
    public void off() {
        offsetPoint = null;
        runs = List.of();
    }

    @Override
    public boolean enabled() {
        return offsetPoint != null;
    }

    @Override
    public void composed(TextBuffer<Textual> buffer, List<Run> runs) {
        this.runs = runs;
        buffer.push(Edit.insertFlush(offsetPoint, composedText()));
    }

    @Override
    public void drawCompose(GraphicsContext gc, TextRun textRun, double bottom, double sideBearing) {
        for (Run composedRun : runs) {
            int start = offsetPoint.offset() + composedRun.offset();
            int end = start + composedRun.length();
            if (start < textRun.tailOffset() && textRun.offset() <= end) {
                double x1 = textRun.offsetToX().apply(Math.max(start, textRun.offset()) - textRun.offset()) + sideBearing;
                double x2 = textRun.offsetToX().apply(Math.min(end, textRun.tailOffset()) - textRun.offset()) + sideBearing;
                double y = bottom - width - width;
                gc.setLineDashes(composedRun.type().ordinal());
                gc.setLineWidth(width);
                gc.strokeLine(x1, y, x2, y);
            }
        }
    }

    private String composedText() {
        return (runs == null) ? "" : runs.stream().map(Run::text).collect(Collectors.joining());
    }

}
