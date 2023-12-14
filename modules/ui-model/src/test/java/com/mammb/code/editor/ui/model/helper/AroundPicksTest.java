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
import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.model.text.OffsetPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link AroundPicks}.
 * @author Naotsugu Kobayashi
 */
class AroundPicksTest {

    @Test void category() {
                               //0123456789
        var textLine = textLine("this is text");

        assertArrayEquals(new long[] { 0, 4 }, AroundPicks.category(textLine, 0));
        assertArrayEquals(new long[] { 0, 4 }, AroundPicks.category(textLine, 1));
        assertArrayEquals(new long[] { 0, 4 }, AroundPicks.category(textLine, 2));
        assertArrayEquals(new long[] { 0, 4 }, AroundPicks.category(textLine, 3));

        assertArrayEquals(new long[] { 4, 5 }, AroundPicks.category(textLine, 4));

        assertArrayEquals(new long[] { 5, 7 }, AroundPicks.category(textLine, 5));
        assertArrayEquals(new long[] { 5, 7 }, AroundPicks.category(textLine, 6));

        assertArrayEquals(new long[] { 7, 8 }, AroundPicks.category(textLine, 7));

        assertArrayEquals(new long[] { 8, 11 }, AroundPicks.category(textLine, 8));
        assertArrayEquals(new long[] { 8, 11 }, AroundPicks.category(textLine, 9));
        assertArrayEquals(new long[] { 8, 11 }, AroundPicks.category(textLine, 10));
        assertArrayEquals(new long[] { 8, 11 }, AroundPicks.category(textLine, 11));
    }

    private TextLine textLine(String text) {
        return new TextLine() {
            @Override public OffsetPoint point() {
                return OffsetPoint.zero;
            }
            @Override public int lineIndex() {
                return 0;
            }
            @Override public int lineSize() {
                return 1;
            }
            @Override public int length() {
                return text.length();
            }
            @Override public double width() {
                return 0;
            }
            @Override public double height() {
                return 0;
            }
            @Override public List<TextRun> runs() {
                return null;
            }
            @Override public long xToOffset(double x) {
                return (long) x;
            }
            @Override public char charAt(long offset) {
                return text.charAt((int) offset);
            }
        };
    }

}
