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
     * The position y.
     * @return the position y
     */
    double y();

    /**
     * Get the start char index at the source span.
     * @return the start char index at the source span
     */
    int start();

    /**
     * Get the char length of this text run.
     * @return the char length of this text run
     */
    int length();

    /**
     * Get the baseline height.
     * @return the baseline height.
     */
    default double baseline() {
        return layout().y() - y();
    }

    /**
     * Get the end char index at the source span.
     * @return the start char index at the source span
     */
    default int end() {
        return start() + length();
    }

    /**
     * Get the text.
     * @return the text
     */
    default String text() {
        return source().text().substring(start(), end());
    }

    /**
     * The offset to x function.
     * @return the offset to x function
     */
    Function<Integer, Float> offsetToX();

    /**
     * The x to offset function.
     * @return the x to offset function
     */
    Function<Double, Integer> xToOffset();

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
     * Get the offset char index.
     * @return the offset char index
     */
    default int offset() {
        return source().point().offset() + start();
    }

    /**
     * Get the code point offset corresponding to the specified offset.
     * @param offset the specified offset
     * @return the code point offset corresponding to the specified offset
     */
    default int cpOffset(int offset) {
        return source().point().cpOffset() +
            Character.codePointCount(source().text(), 0, offset - offset());
    }

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
     * @param y the position y
     * @param start the start char index at the source span
     * @param length the char length of this text run
     * @param source the source span
     * @param offsetToX the offset to x function
     * @param xToOffset the x to offset function
     * @return a created TextRun
     */
    static TextRun of(Layout layout, double y, int start, int length, Span source,
                      Function<Integer, Float> offsetToX, Function<Double, Integer> xToOffset) {
        return new TextRunRecord(layout, y, start, length, null, source, offsetToX, xToOffset);
    }

    /**
     * Create a new TextRun.
     * @param textLine the textLine to which this run belongs
     * @param textRun the TextRun
     * @return a created TextRun
     */
    static TextRun of(TextLine textLine, TextRun textRun) {
        return new TextRunRecord(textRun.layout(), textRun.y(), textRun.start(), textRun.length(), textLine, textRun.source(), textRun.offsetToX(), textRun.xToOffset());
    }

}
