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
package com.mammb.code.editor2.model.shaped.fx;

import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor2.model.core.OffsetPoint;
import com.mammb.code.editor2.model.shaped.ShapedLine;
import com.mammb.code.editor2.model.style.StyledText;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.Arrays;
import java.util.List;

/**
 * ShapedLineImpl.
 * @author Naotsugu Kobayashi
 */
public class FxShapedLine implements ShapedLine<Font, Color> {

    private final StyledText styledText;
    private final TextLine textLine;
    private final List<FxShapedRun> runs;

    public FxShapedLine(int baseOrigin, StyledText styledText, TextLine textLine) {
        int start = textLine.startOffset() - (styledText.point().offset() - baseOrigin);
        this.styledText = styledText.subText(start, start + textLine.length());
        this.textLine = textLine;
        this.runs = Arrays.stream(textLine.runs())
            .map(run -> new FxShapedRun(this.styledText, run)).toList();
    }

    @Override
    public List<FxShapedRun> runs() {
        return runs;
    }

    @Override
    public OffsetPoint point() {
        return styledText.point();
    }

    @Override
    public String text() {
        return styledText.text();
    }

}
