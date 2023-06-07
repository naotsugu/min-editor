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
import com.mammb.code.editor.model.layout.EmbedSpan;
import com.mammb.code.editor.model.layout.FontSpan;
import com.mammb.code.editor.model.layout.Span;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.scene.text.TextSpan;
import javafx.scene.text.Font;

import java.util.Map;

/**
 * FxTextSpan.
 * Adapter of Span to com.sun.javafx.scene.text.TextSpan
 * @author Naotsugu Kobayashi
 */
public class TextSpanImpl implements TextSpan {

    private final Span<?> peer;

    private final Map<Font, Object> nativeFonts;


    public TextSpanImpl(Span<?> span) {
        this(span, null);
    }


    public TextSpanImpl(Span<?> span, Map<Font, Object> nativeFonts) {
        this.peer = span;
        this.nativeFonts = nativeFonts;
    }


    @Override
    public String getText() {
        return peer.text();
    }


    @Override
    public Object getFont() {
        // TODO rewrite to Pattern Matching for switch
        if (peer instanceof FontSpan<?> fontSpan) {
            Object font = fontSpan.figure().font();
            if (font instanceof Font fxFont) {
                return (nativeFonts == null)
                    ? FontHelper.getNativeFont(fxFont)
                    : nativeFonts.computeIfAbsent(fxFont, FontHelper::getNativeFont);
            } else {
                return font;
            }
        } else {
            return null;
        }
    }


    @Override
    public RectBounds getBounds() {
        if (peer instanceof EmbedSpan embedSpan) {
            Bounds bounds = embedSpan.figure();
            return new RectBounds(
                bounds.minX(),
                bounds.minY(),
                bounds.maxX(),
                bounds.maxY());
        } else {
            return null;
        }
    }

}
