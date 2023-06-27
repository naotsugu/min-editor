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
package com.mammb.code.editor2.ui.pane;

import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.text.OffsetPoint;
import java.util.List;

/**
 * LayoutLine.
 * @param line the TextLine
 * @param offsetY the offset of position y
 * @author Naotsugu Kobayashi
 */
public record LayoutLine(TextLine line, double offsetY) implements TextLine {

    @Override
    public OffsetPoint point() {
        return line.point();
    }

    @Override
    public int length() {
        return line.length();
    }

    @Override
    public double height() {
        return line.height();
    }

    @Override
    public List<TextRun> runs() {
        return line.runs();
    }

    public List<LayoutRun> layoutRuns() {
        return line.runs().stream()
            .map(run -> new LayoutRun(run, offsetY)).toList();
    }

}
