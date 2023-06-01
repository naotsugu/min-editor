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
package com.mammb.code.javafx.scene.text;

import com.mammb.code.editor.model.layout.FontFace;
import com.mammb.code.editor.model.layout.FontSpan;

import java.util.ArrayList;
import java.util.List;

/**
 * ShapedText.
 * @author Naotsugu Kobayashi
 */
public class ShapedText implements com.mammb.code.editor.model.layout.ShapedText {

    private final TextLayoutShim textLayout;

    private final List<TextSpan> spans;

    public ShapedText() {
        this.textLayout = new TextLayoutShim();
        this.spans = new ArrayList<>();
    }

    @Override
    public com.mammb.code.editor.model.layout.ShapedText add(String text, FontFace<?> fontFace) {
        return this;
    }

    @Override
    public com.mammb.code.editor.model.layout.ShapedText add(FontSpan<?> span) {
        //spans.add(span);
        return this;
    }


    @Override
    public void layout() {
        textLayout.setContent(spans.toArray(TextSpan[]::new));
    }

    @Override
    public void reset() {
        spans.clear();
    }

}
