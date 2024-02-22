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
import com.mammb.code.editor.model.style.StyledText;

import java.util.ArrayList;
import java.util.List;

/**
 * StyledSubText.
 * @author Naotsugu Kobayashi
 */
public class StyledSubText implements StyledText {

    /** The source text. */
    private final StyledText styledText;

    /** The start index of sub text. */
    private final int start;

    /** The length of sub text. */
    private final int length;

    /** The cache of sub OffsetPoint. */
    private OffsetPoint subPoint;

    /** The cache of sub StyleSpan. */
    private List<StyleSpan> subStyles;


    /**
     * Constructor.
     * @param styledText the source text
     * @param start the start index of sub text
     * @param length the length of sub text
     */
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
    public OffsetPoint offsetPoint() {
        if (subPoint == null) {
            subPoint = OffsetPoint.of(
                styledText.offsetPoint().row(),
                styledText.offsetPoint().offset() + start,
                styledText.offsetPoint().cpOffset() + Character.codePointCount(styledText.text(), 0, start));
        }
        return subPoint;
    }

    @Override
    public String text() {
        return styledText.text().substring(start, start + length);
    }


    @Override
    public List<StyleSpan> styles() {
        if (subStyles == null) {
            List<StyleSpan> list = new ArrayList<>();
            for (StyleSpan styleSpan : styledText.styles()) {
                if (!styleSpan.inRange(start, length)) continue;
                int newEnd = Math.min(styleSpan.endExclusive() - start, length);
                int newStart = Math.max(styleSpan.point() - start, 0);
                StyleSpan sub = StyleSpan.of(
                    styleSpan.style(),
                    newStart,
                    newEnd - newStart);
                list.add(sub);
            }
            subStyles = list;
        }
        return subStyles;
    }

    @Override
    public boolean hol() {
        return start == 0;
    }


    @Override
    public StyledText subText(int start, int length) {
        return StyledSubText.of(this, start, length);
    }


    @Override
    public void putStyle(StyleSpan style) {
        styledText.putStyle(style);
        subStyles = null; // clear cache
    }

}
