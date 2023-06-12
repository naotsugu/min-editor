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
package com.mammb.code.editor.javafx.layout;

import com.mammb.code.editor2.model.layout.SpanStyle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * FxSpanStyle.
 * @author Naotsugu Kobayashi
 */
public interface FxSpanStyle extends SpanStyle {

    /**
     * Get the font.
     * @return the font
     */
    Font font();

    /**
     * Create a new Style.
     * @param font the font
     * @return a created Style
     */
    static FxSpanStyle of(Font font) {
        record SpanStyleRecord(Font font) implements FxSpanStyle { }
        return new SpanStyleRecord(font);
    }

    /**
     * Create a new Style.
     * @param font the font
     * @param color the color
     * @return a created Style
     */
    static FxSpanStyle of(Font font, Color color) {
        record SpanStyleRecord(Font font, Color color) implements FxSpanStyle { }
        return new SpanStyleRecord(font, color);
    }

    /**
     * Create a new Style.
     * @param font the font
     * @param color the color
     * @param bgColor the background color
     * @return a created Style
     */
    static FxSpanStyle of(Font font, Color color, Color bgColor) {
        record SpanStyleRecord(Font font, Color color, Color bgColor) implements FxSpanStyle { }
        return new SpanStyleRecord(font, color, bgColor);
    }

}
