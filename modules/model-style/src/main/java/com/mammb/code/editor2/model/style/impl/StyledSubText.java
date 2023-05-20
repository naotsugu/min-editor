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

import com.mammb.code.editor2.model.core.OffsetPoint;
import com.mammb.code.editor2.model.style.StyleSpan;
import java.util.ArrayList;
import java.util.List;

/**
 * StyledSubText.
 * @author Naotsugu Kobayashi
 */
public class StyledSubText implements com.mammb.code.editor2.model.style.StyledText {

    /** The source text. */
    private StyledText styledText;

    /** The start index of sub text. */
    private int start;

    /** The length of sub text. */
    private int length;

    /** The cache of sub OffsetPoint */
    private OffsetPoint subPoint;


    private StyledSubText(StyledText styledText, int start, int length) {
        this.styledText = styledText;
        this.start = start;
        this.length = length;
    }

    /**
     * Create a sub StyledText.
     * @param styledText the source text
     * @param start the start index of sub text
     * @param length the length of sub text
     * @return a sub StyledText
     */
    public static StyledSubText of(StyledText styledText, int start, int length) {
        return new StyledSubText(styledText, start, length);
    }


    @Override
    public OffsetPoint point() {
        if (subPoint == null) {
            subPoint = OffsetPoint.of(
                styledText.point().row(),
                styledText.point().offset() + start,
                styledText.point().cpOffset() + Character.codePointCount(styledText.text(), 0, start));
        }
        return subPoint;
    }

    @Override
    public String text() {
        return styledText.text().substring(start, start + length);
    }

    @Override
    public List<StyleSpan> styles() {
        List<StyleSpan> ret = new ArrayList<>();
        for (StyleSpan styleSpan : styles()) {
            ret.add(styleSpan);
            // TODO split style
        }
        return ret;
    }

    @Override
    public void putStyle(StyleSpan style) {
        styledText.putStyle(style);
    }

}
