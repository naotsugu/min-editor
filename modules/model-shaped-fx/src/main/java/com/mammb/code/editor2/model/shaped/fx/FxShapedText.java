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

import com.mammb.code.editor2.model.shaped.ShapedText;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.javafx.scene.text.TextLayoutShim;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * FxShapedText.
 * @author Naotsugu Kobayashi
 */
public class FxShapedText implements ShapedText<Font, Color> {

    private final TextLayoutShim textLayout;

    private final List<StyledText> sources;


    public FxShapedText() {
        textLayout = new TextLayoutShim();
        sources = new ArrayList<>();
    }

    public void add(List<StyledText> texts) {
        sources.addAll(texts);
    }

    public void layout() {
        //textLayout.setContent();
    }

}
