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
package com.mammb.code.javafx.scene.text.impl;

import com.mammb.code.editor.model.layout.Bounds;
import com.mammb.code.editor.model.layout.Point;
import com.mammb.code.editor.model.layout.FontFace;
import com.mammb.code.editor.model.layout.GlyphRun;
import com.sun.javafx.font.PGFont;
import com.sun.javafx.scene.text.GlyphList;

/**
 * GlyphRunImpl.
 * @author Naotsugu Kobayashi
 */
public class GlyphRunImpl implements GlyphRun<PGFont> {

    private final GlyphList pear;

    private GlyphRunImpl(GlyphList pear) {
        this.pear = pear;
    }

    public static GlyphRun<PGFont> of(GlyphList pear) {
        return new GlyphRunImpl(pear);
    }

    @Override
    public float width() {
        return pear.getWidth();
    }

    @Override
    public float height() {
        return pear.getHeight();
    }

    @Override
    public Point location() {
        return new PointImpl(pear.getLocation());
    }

    @Override
    public String getText() {
        return pear.getTextSpan().getText();
    }

    @Override
    public FontFace<PGFont> getFont() {
        return NativeFontFace.of(pear.getTextSpan().getFont());
    }

    @Override
    public Bounds getBounds() {
        return BoundsRect.of(pear.getTextSpan().getBounds());
    }

}
