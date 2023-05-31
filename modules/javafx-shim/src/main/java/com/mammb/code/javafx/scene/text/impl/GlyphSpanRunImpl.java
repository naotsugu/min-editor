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

import com.mammb.code.editor.model.layout.GlyphRun;
import com.mammb.code.editor.model.layout.GlyphSpanRun;
import com.mammb.code.editor.model.layout.Span;
import com.sun.javafx.scene.text.GlyphList;

/**
 * GlyphSpanRunImpl.
 * @author Naotsugu Kobayashi
 */
public class GlyphSpanRunImpl extends GlyphRunImpl implements GlyphSpanRun<FxFontSpan> {

    private GlyphSpanRunImpl(GlyphList pear) {
        super(pear);
    }

    public static GlyphRun of(GlyphList pear) {
        return new GlyphSpanRunImpl(pear);
    }

    @Override
    public Span<FxFontSpan> span() {
        return null;
    }

}
