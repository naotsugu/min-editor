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

import com.mammb.code.editor.model.layout.GlyphRun;
import com.mammb.code.editor2.model.shaped.ShapedRun;
import com.mammb.code.editor2.model.style.StyledText;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * FxShapedRun.
 * @author Naotsugu Kobayashi
 */
public class FxShapedRun implements ShapedRun<Font, Color> {

    private final GlyphRun<Font> glyphRun;

    public FxShapedRun(StyledText parent, GlyphRun<Font> glyphRun) {
        this.glyphRun = glyphRun;
    }

    @Override
    public String text() {
        return glyphRun.getText();
    }

    @Override
    public Font font() {
        return glyphRun.getFont();
    }

    @Override
    public Color color() {
        return null;
    }

    @Override
    public float x() {
        return glyphRun.location().x();
    }

    @Override
    public float y() {
        return glyphRun.location().y();
    }

    @Override
    public float width() {
        return glyphRun.width();
    }

    @Override
    public float height() {
        return glyphRun.height();
    }

    @Override
    public int length() {
        return glyphRun.getText().length();
    }

}
