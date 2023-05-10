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
package com.mammb.code.javafx.text;

import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.scene.text.TextSpan;
import javafx.scene.text.Font;

/**
 * Span.
 * @author Naotsugu Kobayashi
 */
public class Span implements TextSpan {

    private String text;
    private Object nativeFont;

    public Span(String text) {
        this(text, Font.getDefault());
    }

    public Span(String text, Font font) {
        this.text = text;
        nativeFont = FontHelper.getNativeFont(font);
    }

    @Override public String getText() {
        return text;
    }

    @Override public Object getFont() {
        return nativeFont;
    }

    @Override public RectBounds getBounds() {
        return null;
    }

}
