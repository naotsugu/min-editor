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
import com.mammb.code.editor2.model.shaped.ShapedText;
import com.mammb.code.editor2.model.style.Style;
import com.mammb.code.editor2.model.style.StyleSpan;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.javafx.scene.text.FxTextSpan;
import com.mammb.code.javafx.scene.text.TextLayoutShim;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

/**
 * FxShapedText.
 * @author Naotsugu Kobayashi
 */
public class FxShapedText implements ShapedText<Font, Color> {

    private final TextLayoutShim textLayout;

    private final TreeMap<Integer, StyledText> sources;

    private OffsetPoint baseOrigin;
    private List<FxShapedLine> shapedLines;

    public FxShapedText() {
        textLayout = new TextLayoutShim();
        sources = new TreeMap<>();
    }

    public void add(List<StyledText> texts) {
        if (baseOrigin == null) {
            baseOrigin = texts.get(0).point();
        }
        texts.forEach(text -> sources.put(text.point().offset() - baseOrigin.offset(), text));

    }


    public void layout() {

        FxTextSpan[] spans = sources.values().stream()
            .map(StyledText::spans)
            .flatMap(Collection::stream)
            .map(this::asTextSpan)
            .toArray(FxTextSpan[]::new);
        textLayout.setContent(spans);

        int offset = 0;
        List<FxShapedLine> shapedLines = new ArrayList<>();
        for (TextLine<Font> line : textLayout.getLines()) {
            shapedLines.add(new FxShapedLine(baseOrigin.offset(), sources.floorEntry(offset).getValue(), line));
            offset += line.length();
        }
        this.shapedLines = shapedLines;
    }


    private FxTextSpan asTextSpan(StyledText styledText) {
        String fontName = "System";
        double fontSize = -1;
        for (StyleSpan ss : styledText.styles()) {
            if (ss.style() instanceof Style.FontFamily ff) {
                fontName = ff.name();
            } else if (ss.style() instanceof Style.FontSize fs) {
                fontSize = fs.size();
            }
        }
        return FxTextSpan.of(styledText.text(), Font.font(fontName, fontSize));
    }


    public void clear() {
        sources.clear();
        baseOrigin = null;
        shapedLines = null;
    }

}
