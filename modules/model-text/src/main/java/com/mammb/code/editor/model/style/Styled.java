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

import java.util.List;

/**
 * Styled.
 * @author Naotsugu Kobayashi
 */
public interface Styled {

    /**
     * Get the style span list.
     * @return the style span list
     */
    List<StyleSpan> styles();


    /**
     * Get the styles for a specified style.
     * @param filter specified style
     * @return the styles for a specified style
     */
    default List<StyleSpan> styles(Class<? extends Style> filter) {
        return styles().stream()
            .filter(s -> filter.isAssignableFrom(s.style().getClass()))
            .toList();
    }


    /**
     * Gets an array of starting positions for a specified style.
     * @param filter specified style
     * @return an array of starting positions for a specified style
     */
    default int[] points(Class<? extends Style> filter) {
        return styles().stream()
            .filter(s -> filter.isAssignableFrom(s.style().getClass()))
            .mapToInt(StyleSpan::point)
            .toArray();
    }

}
