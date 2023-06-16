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
package com.mammb.code.editor2.model.layout;

import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;

/**
 * Span.
 * @author Naotsugu Kobayashi
 */
public interface Span extends Textual {

    @Override
    String text();

    @Override
    OffsetPoint point();

    /**
     * Get the style of span.
     * @return the style of span
     */
    FontStyle style();

    /**
     * Create a new Span.
     * @param textual the text of span
     * @param fontStyle the style of span
     * @return a created Span
     */
    static Span of(Textual textual, FontStyle fontStyle) {
        record SpanRecord(String text, OffsetPoint point, FontStyle style) implements Span { }
        return new SpanRecord(textual.text(), textual.point(), fontStyle);
    }

}
