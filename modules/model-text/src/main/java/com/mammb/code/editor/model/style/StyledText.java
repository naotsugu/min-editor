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
package com.mammb.code.editor.model.style;

import com.mammb.code.editor.model.style.impl.StyledTextRecord;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * StyledText.
 * @author Naotsugu Kobayashi
 */
public interface StyledText extends Textual, Styled {

    @Override
    String text();

    @Override
    OffsetPoint point();

    @Override
    List<StyleSpan> styles();

    /**
     * Head of line?
     * @return
     */
    boolean hol();

    /**
     * Get the sub StyledText.
     * @param start the start index
     * @param length the length
     * @return the sub StyledText
     */
    StyledText subText(int start, int length);

    /**
     * Put the style.
     * @param style the style
     */
    void putStyle(StyleSpan style);


    default List<StyledText> spans() {

        if (styles().isEmpty() || length() == 0) {
            return List.of(this);
        }

        Set<Integer> indices = new TreeSet<>();
        for (StyleSpan style : styles()) {
            indices.add(style.point());
            if (style.length() > 0) {
                indices.add(style.point() + style.length());
            }
        }
        indices.add(length());
        List<StyledText> spans = new ArrayList<>();
        int prev = 0;
        for (int i : indices) {
            if (i == 0) continue;
            spans.add(subText(prev, i - prev));
            prev = i;
        }

        return spans;
    }


    static StyledText of(Textual textual) {
        return new StyledTextRecord(textual);
    }

}
