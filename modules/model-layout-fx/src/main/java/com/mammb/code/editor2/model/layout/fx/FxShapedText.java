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
package com.mammb.code.editor2.model.layout.fx;

import com.mammb.code.editor.model.layout.ShapedText;
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.javafx.scene.text.TextLayoutShim;
import com.mammb.code.javafx.scene.text.FxTextSpan;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * ShapedText.
 * @author Naotsugu Kobayashi
 */
public class FxShapedText implements ShapedText {

    private final TextLayoutShim textLayout;

    private final List<FxTextSpan> spans;

    private TextLine[] lines;

    public FxShapedText() {
        this.textLayout = new TextLayoutShim();
        this.spans = new ArrayList<>();
    }

    @Override
    public ShapedText add(String text, String fontName) {
        spans.add(FxTextSpan.of(text, Font.font(fontName)));
        return this;
    }


    @Override
    public void layout() {
        textLayout.setContent(spans.toArray(FxTextSpan[]::new));
        lines = textLayout.getLines();
    }

    @Override
    public void reset() {
        spans.clear();
    }

    @Override
    public void setWrapWidth(float wrapWidth) {
        if (wrapWidth >= 0) {
            textLayout.setWrapWidth(wrapWidth);
            if (lines != null) {
                layout();
            }
        }
    }

}
