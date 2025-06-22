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

/**
 * The Contents.
 * @author Naotsugu Kobayashi
 */
class Contents {

    static Integer countCodePoints(Content content, List<Point.Range> range) {
        return range.stream().findFirst()
            .filter(r -> r.max().row() - r.min().row() < 1000) // limit 1000 rows
            .map(r -> content.getText(r.min(), r.max()))
            .map(text -> text.codePointCount(0, text.length()))
            .orElse(0);
    }

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
