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
package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.Pair;
import com.mammb.code.editor.core.Point;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Contents.
 * @author Naotsugu Kobayashi
 */
class Contents {

    /**
     * Combines text from the specified ranges within the content into a single string,
     * separating each range's text with a line separator.
     *
     * @param content the content from which text will be retrieved
     * @param range a list of ranges specifying the start and end positions to extract text from
     * @return a combined string of the text from the specified ranges, separated by system line separators
     */
    static String text(Content content, List<Point.Range> range) {
        return range.stream()
            .map(r -> content.getText(r.min(), r.max()))
            .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Counts the number of Unicode code points in the text from the specified range within the content.
     *
     * @param content the content from which text will be retrieved
     * @param range a list of ranges specifying the text area, with only the first range being processed
     *              if it meets the constraints
     * @return the count of code points in the specified range of text, or 0 if the range is invalid
     *         or exceeds the row limit
     */
    static Integer countCodePoints(Content content, List<Point.Range> range) {
        return range.stream().findFirst()
            .filter(r -> r.max().row() - r.min().row() < 1000) // limit 1000 rows
            .map(r -> content.getText(r.min(), r.max()))
            .map(text -> text.codePointCount(0, text.length()))
            .orElse(0);
    }

    /**
     * Retrieves the left and right text characters relative to the specified column position
     * in the row of the given content. The left text accounts for surrogate pairs if they exist.
     *
     * @param content the content to retrieve the text from
     * @param p the position represented as a point, where the row specifies the line
     *          and the column specifies the character index within that line
     * @return a pair containing the left and right text characters as strings; if no character is available
     *         on the left or right, an empty string is returned for that respective side
     */
    static Pair<String> lrTextAt(Content content, Point p) {
        var text = content.getText(p.row());

        String right = (p.col() >= 0 && p.col() < text.length()) ?
            Character.toString(Character.codePointAt(text, p.col())) : "";

        String left = "";
        int leftIndex = p.col() - 1;
        if (leftIndex >= 0 && leftIndex < text.length()) {
            char ch1 = text.charAt(leftIndex);
            if (Character.isLowSurrogate(ch1) && --leftIndex >= 0) {
                left = text.substring(leftIndex, leftIndex + 2);
            } else {
                left = Character.toString(ch1);
            }
        }
        return new Pair<>(left, right);
    }

}
