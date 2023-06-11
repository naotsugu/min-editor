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
package com.mammb.code.editor2.model.style.impl;

import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.PointText;
import com.mammb.code.editor2.model.style.StyleSpan;
import com.mammb.code.editor2.model.style.StyledText;

import java.util.ArrayList;
import java.util.List;

/**
 * StyledText.
 * @param pointText the point text
 * @param styles the styles
 * @author Naotsugu Kobayashi
 */
public record StyledTextRecord(
        PointText pointText,
        List<StyleSpan> styles) implements StyledText {

    /**
     * Create a new empty styled text.
     * @param pointText the point text
     */
    public StyledTextRecord(PointText pointText) {
        this(pointText, new ArrayList<>());
    }

    @Override
    public OffsetPoint point() {
        return pointText.point();
    }

    @Override
    public StyledText subText(int start, int length) {
        return StyledSubText.of(this, start, length);
    }

    @Override
    public String text() {
        return pointText.text();
    }

    @Override
    public void putStyle(StyleSpan style) {
        styles.add(style);
    }

}
