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
import java.util.stream.Collectors;

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

        final List<StyleSpan> styles = styles();
        final String text = text();

        List<StyledText> spans = new ArrayList<>();

        int offset = 0;
        Set<Style> prev = Set.of();
        for (int i = 0; i < text.length(); i++) {
            int index = i;
            Set<Style> current = styles.stream()
                .filter(style -> style.inRange(index))
                .map(StyleSpan::style)
                .collect(Collectors.toSet());
            if (!prev.equals(current)) {
                prev = current;
                if (i > 0) {
                    spans.add(subText(offset, i));
                    offset = i;
                }
            }
        }
        spans.add(subText(offset, text.length()));

        return spans;
    }

}
