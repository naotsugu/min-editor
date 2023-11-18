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
package com.mammb.code.editor.ui.model.helper;

import com.mammb.code.editor.model.layout.TextLine;

/**
 * ScreenText.
 * @author Naotsugu Kobayashi
 */
public class AroundPicks {

    public static long[] category(TextLine line, double x) {
        long offset = line.xToOffset(x);
        long start = offset;
        long end = offset;
        int type = Character.getType(Character.toLowerCase(line.charAt(offset)));

        for (long i = offset + 1; i < line.tailOffset(); i++) {
            if (type != Character.getType(Character.toLowerCase(line.charAt(i)))) {
                end = i;
                break;
            }
        }
        for (long i = offset - 1; i >= line.offset(); i--) {
            if (type != Character.getType(Character.toLowerCase(line.charAt(i)))) {
                break;
            } else {
                start = i;
            }
        }
        if (start != end) {
            return new long[] { start, end };
        } else {
            return new long[] { start };
        }

    }
}
