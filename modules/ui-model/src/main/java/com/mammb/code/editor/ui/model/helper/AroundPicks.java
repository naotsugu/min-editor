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
package com.mammb.code.editor.ui.model.helper;

import com.mammb.code.editor.model.layout.TextLine;
import java.util.function.Function;

/**
 * AroundPicks.
 * @author Naotsugu Kobayashi
 */
public class AroundPicks {

    /**
     * Around pick by category.
     * @param line the TextLine
     * @param x the position of x
     * @return the around index
     */
    public static long[] category(TextLine line, double x) {
        return pick(line, x, ch -> Character.getType(Character.toLowerCase(ch)));
    }


    /**
     * Around pick by delimiter.
     * @param line the TextLine
     * @param x the position of x
     * @return the around index
     */
    public static long[] delimiter(TextLine line, double x) {
        return pick(line, x, ch -> {
            if (ch == '\\' || ch == '/' || ch ==  '.' || ch ==  '{' || ch ==  '}' ||
                ch == '(' || ch == ')' ||
                Character.isWhitespace(ch) || Character.isISOControl(ch)) return -1;
            return 0;
        });
    }


    static long[] pick(TextLine line, double x, Function<Character, Integer> toType) {

        long offset = line.xToOffset(x);
        long start = offset;
        long end = offset;
        int type = toType.apply(line.charAt(offset));

        for (long i = offset + 1; i < line.tailOffset(); i++) {
            end = i;
            char ch = line.charAt(i);
            if (type != toType.apply(ch)) {
                break;
            }
        }
        for (long i = offset - 1; i >= line.offset(); i--) {
            char ch = line.charAt(i);
            if (type != toType.apply(ch)) {
                break;
            }
            start = i;
        }

        if (start != end) {
            return new long[] { start, end };
        } else {
            return new long[] { start };
        }
    }


}
