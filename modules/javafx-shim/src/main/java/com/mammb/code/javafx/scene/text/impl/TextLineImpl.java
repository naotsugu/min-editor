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
import com.mammb.code.editor.model.layout.GlyphRun;
import com.mammb.code.editor.model.layout.TextLine;
import com.sun.javafx.scene.text.GlyphList;

/**
 * LayoutLine.
 * @author Naotsugu Kobayashi
 */
public class TextLineImpl implements TextLine {

    private final com.sun.javafx.scene.text.TextLine pear;

    public TextLineImpl(com.sun.javafx.scene.text.TextLine textLine) {
        pear = textLine;
    }

    public static TextLine of(com.sun.javafx.scene.text.TextLine textLine) {
        return new TextLineImpl(textLine);
    }

    @Override
    public GlyphRun[] runs() {
        GlyphList[] runs = pear.getRuns();
        GlyphRun[] ret = new GlyphRun[runs.length];
        for (int i = 0; i < runs.length; i++) {
            ret[i] = GlyphRunImpl.of(runs[i]);
        }
        return ret;
    }

    @Override
    public Bounds bounds() {
        return BoundsRect.of(pear.getBounds());
    }

    @Override
    public int startOffset() {
        return pear.getStart();
    }

    @Override
    public int length() {
        return pear.getLength();
    }
}
