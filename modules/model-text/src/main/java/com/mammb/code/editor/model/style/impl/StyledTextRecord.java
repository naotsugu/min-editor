/*
 * Copyright 2023-2024 the original author or authors.
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
package com.mammb.code.editor.model.style.impl;

import com.mammb.code.editor.model.style.StyleSpan;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.model.style.StyledText;

import java.util.ArrayList;
import java.util.List;

/**
 * StyledText.
 * @param textual the point text
 * @param styles the styles
 * @author Naotsugu Kobayashi
 */
public record StyledTextRecord(
        Textual textual,
        List<StyleSpan> styles) implements StyledText {

    /**
     * Create a new empty styled text.
     * @param textual the point text
     */
    public StyledTextRecord(Textual textual) {
        this(textual, new ArrayList<>());
    }


    @Override
    public OffsetPoint offsetPoint() {
        return textual.offsetPoint();
    }


    @Override
    public boolean hol() {
        return true;
    }


    @Override
    public StyledText subText(int start, int length) {
        return StyledSubText.of(this, start, length);
    }


    @Override
    public String text() {
        return textual.text();
    }


    @Override
    public void putStyle(StyleSpan style) {
        if (style.point() + style.length() <= textual.length()) {
            styles.add(style);
        } else {
            throw new IndexOutOfBoundsException(
                STR."out of bounds:\{style}[\{textual.text()}, \{textual.length()}]");
        }
    }

}
