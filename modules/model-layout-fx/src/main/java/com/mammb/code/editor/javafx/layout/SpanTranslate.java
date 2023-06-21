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

import com.mammb.code.editor2.model.layout.Span;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.text.Translate;
import javafx.scene.text.Font;

/**
 * SpanTranslate.
 * @author Naotsugu Kobayashi
 */
public class SpanTranslate implements Translate<StyledText, Span> {

    /** The font style. */
    private FxFontStyle fontStyle;

    /**
     * Constructor.
     * @param font the font
     */
    private SpanTranslate(Font font) {
        this.fontStyle = FxFontStyle.of(font);
    }

    /**
     * Create a new translation.
     * @param font the font
     * @return a new translation
     */
    public static Translate<StyledText, Span> of(Font font) {
        return new SpanTranslate(font);
    }

    /**
     * Create a new translation.
     * @return a new translation
     */
    public static Translate<StyledText, Span> of() {
        return of(Font.font(20));
    }

    @Override
    public Span applyTo(StyledText input) {
        return Span.of(input, fontStyle);
    }

}
