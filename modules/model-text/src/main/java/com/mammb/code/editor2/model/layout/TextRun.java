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

import com.mammb.code.editor2.model.layout.impl.TextRunRecord;
import java.util.function.Function;

/**
 * TextRun.
 * @author Naotsugu Kobayashi
 */
public interface TextRun {

    /**
     * Get the layout.
     * @return the layout
     */
    Layout layout();

    /**
     * Get the text.
     * @return the text
     */
    String text();

    /**
     * The offset to x function.
     * @return the offset to x function
     */
    Function<Integer, Float> offsetToX();

    /**
     * Get the textLine to which this run belongs.
     * @return the textLine to which this run belongs
     */
    TextLine textLine();

    /**
     * Get the source span.
     * Many-to-one relationship
     * @return the source span
     */
    Span source();

    /**
     * Get the style.
     * @return the style
     */
    default FontStyle<?, ?> style() {
        return source().style();
    }

    /**
     * Create a new TextRun.
     * @param layout the layout
     * @param text the text
     * @param source the source span
     * @param offsetToX the offset to x function
     * @return a created TextRun
     */
    static TextRun of(Layout layout, String text, Span source, Function<Integer, Float> offsetToX) {
        return new TextRunRecord(layout, text, null, source, offsetToX);
    }

    /**
     * Create a new TextRun.
     * @param textLine the textLine to which this run belongs
     * @param textRun the TextRun
     * @return a created TextRun
     */
    static TextRun of(TextLine textLine, TextRun textRun) {
        return new TextRunRecord(textRun.layout(), textRun.text(), textLine, textRun.source(), textRun.offsetToX());
    }

}
