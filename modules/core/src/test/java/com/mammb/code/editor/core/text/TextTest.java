/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.core.text;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link Text}.
 * @author Naotsugu Kobayashi
 */
class TextTest {

    @Test
    void isEndWithLf() {
        assertEquals(false, Text.of(0, "",      new double[] { 0 }, 1).isEndWithLf());
        assertEquals(false, Text.of(0, "a",     new double[] { 0 }, 1).isEndWithLf());
        assertEquals(true,  Text.of(0, "\n",    new double[] { 0 }, 1).isEndWithLf());
        assertEquals(true,  Text.of(0, "\r\n",  new double[] { 0 }, 1).isEndWithLf());
        assertEquals(true,  Text.of(0, "a\n",   new double[] { 0 }, 1).isEndWithLf());
        assertEquals(true,  Text.of(0, "a\r\n", new double[] { 0 }, 1).isEndWithLf());
    }

    @Test
    void isEndWithCrLf() {
        assertEquals(false, Text.of(0, "",      new double[] { 0 }, 1).isEndWithCrLf());
        assertEquals(false, Text.of(0, "a",     new double[] { 0 }, 1).isEndWithCrLf());
        assertEquals(false, Text.of(0, "\n",    new double[] { 0 }, 1).isEndWithCrLf());
        assertEquals(true,  Text.of(0, "\r\n",  new double[] { 0 }, 1).isEndWithCrLf());
        assertEquals(false, Text.of(0, "a\n",   new double[] { 0 }, 1).isEndWithCrLf());
        assertEquals(true,  Text.of(0, "a\r\n", new double[] { 0 }, 1).isEndWithCrLf());
    }

    @Test
    void textLength() {
        assertEquals(0, Text.of(0, "",      new double[] { 0 }, 1).textLength());
        assertEquals(1, Text.of(0, "a",     new double[] { 0 }, 1).textLength());
        assertEquals(3, Text.of(0, "a𠀋",   new double[] { 0 }, 1).textLength());
        assertEquals(0, Text.of(0, "\n",    new double[] { 0 }, 1).textLength());
        assertEquals(0, Text.of(0, "\r\n",  new double[] { 0 }, 1).textLength());
        assertEquals(1, Text.of(0, "a\n",   new double[] { 0 }, 1).textLength());
        assertEquals(1, Text.of(0, "a\r\n", new double[] { 0 }, 1).textLength());
    }

    @Test
    void isSurrogate() {
        var text = Text.of(0, "a𠀋b", new double[] { 0 }, 1);
        assertEquals(false, text.isSurrogate(0));
        assertEquals(true,  text.isSurrogate(1));
        assertEquals(true,  text.isSurrogate(2));
        assertEquals(false, text.isSurrogate(3));
    }

    @Test
    void isHighSurrogate() {
        var text = Text.of(0, "a𠀋b", new double[] { 0 }, 1);
        assertEquals(false, text.isHighSurrogate(0));
        assertEquals(true,  text.isHighSurrogate(1));
        assertEquals(false, text.isHighSurrogate(2));
        assertEquals(false, text.isHighSurrogate(3));
    }

    @Test
    void isLowSurrogate() {
        var text = Text.of(0, "a𠀋b", new double[] { 0 }, 1);
        assertEquals(false, text.isLowSurrogate(0));
        assertEquals(false, text.isLowSurrogate(1));
        assertEquals(true,  text.isLowSurrogate(2));
        assertEquals(false, text.isLowSurrogate(3));
    }

    @Test
    void indexRight() {
        var text = Text.of(0, "a𠀋b", new double[] { 0 }, 1);
        assertEquals( 1, text.indexRight(0));
        assertEquals( 3, text.indexRight(1));
        assertEquals( 4, text.indexRight(3));
        assertEquals(-1, text.indexRight(4));
    }

    @Test
    void indexRightBound() {
        var text = Text.of(0, "aa.bb", new double[] { 0 }, 1);
        assertEquals( 2, text.indexRightBound(0));
        assertEquals( 2, text.indexRightBound(1));
        assertEquals( 3, text.indexRightBound(2));
        assertEquals( 5, text.indexRightBound(3));
        assertEquals( 5, text.indexRightBound(4));
        assertEquals(-1, text.indexRightBound(5));
    }

    @Test
    void indexLeft() {
        var text = Text.of(0, "a𠀋b", new double[] { 0 }, 1);
        assertEquals(3, text.indexLeft(4));
        assertEquals(1, text.indexLeft(3));
        assertEquals(0, text.indexLeft(1));
        assertEquals(0, text.indexLeft(0));
    }

    @Test
    void indexLeftBound() {
        var text = Text.of(0, "aa.bb", new double[] { 0 }, 1);
        assertEquals(3, text.indexLeftBound(5));
        assertEquals(3, text.indexLeftBound(4));
        assertEquals(2, text.indexLeftBound(3));
        assertEquals(0, text.indexLeftBound(2));
        assertEquals(0, text.indexLeftBound(1));
        assertEquals(0, text.indexLeftBound(0));
    }

    @Test
    void widthTo() {
        var text = Text.of(0, "", new double[] { 1, 2, 3 }, 1);
        assertEquals(0, text.widthTo(0));
        assertEquals(1, text.widthTo(1));
        assertEquals(3, text.widthTo(2));
        assertEquals(6, text.widthTo(3));
        assertEquals(6, text.widthTo(4));
    }

    @Test
    void indexTo() {
        var text = Text.of(0, "abc", new double[] { 1, 2, 3 }, 1);
        assertEquals(0, text.indexTo(0));
        assertEquals(1, text.indexTo(1));
        assertEquals(1, text.indexTo(2));
        assertEquals(2, text.indexTo(3));
        assertEquals(2, text.indexTo(4));
        assertEquals(2, text.indexTo(5));
        assertEquals(3, text.indexTo(6));
    }

    @Test
    void isEmpty() {
        var text = Text.of(0, "", new double[] { 0 }, 1);
        assertEquals(true, text.isEmpty());
        text = Text.of(0, "a", new double[] { 0 }, 1);
        assertEquals(false, text.isEmpty());
    }

    @Test
    void words() {
        var text = Text.of(0, "ab c.d", new double[] { 1, 1, 1, 1, 1, 1 }, 1);
        var texts = text.words();
        assertEquals(5, texts.size());
        assertEquals("ab", texts.get(0).value());
        assertEquals(" ", texts.get(1).value());
        assertEquals("c", texts.get(2).value());
        assertEquals(".", texts.get(3).value());
        assertEquals("d", texts.get(4).value());
    }

}
