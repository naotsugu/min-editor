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
package com.mammb.code.editor2.model.style;

import com.mammb.code.editor2.model.core.OffsetPoint;
import com.mammb.code.editor2.model.core.PointText;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * StyledText.
 * @author Naotsugu Kobayashi
 */
public interface StyledText extends PointText, Styled {

    @Override
    String text();

    @Override
    OffsetPoint point();

    @Override
    List<StyleSpan> styles();

    StyledText subText(int start, int length);

    void putStyle(StyleSpan style);

    default List<StyledText> spans() {

        Set<Integer> indices = new TreeSet<>();
        for (StyleSpan style : styles()) {
            indices.add(style.point());
            if (style.length() > 0) {
                indices.add(style.length() - 1);
            }
        }
        indices.add(text().length() - 1);

        List<StyledText> spans = new ArrayList<>();
        int prev = 0;
        for (int i : indices) {
            if (i == 0) continue;
            spans.add(subText(prev, i + 1));
            prev = i;
        }
        return spans;
    }

}
