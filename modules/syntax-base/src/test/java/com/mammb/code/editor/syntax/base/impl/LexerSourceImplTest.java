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
package com.mammb.code.editor.syntax.base.impl;

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Text for {@link LexerSourceImpl}.
 * @author Naotsugu Kobayashi
 */
class LexerSourceImplTest {

    @Test
    void testReadChar() {
        var source = LexerSourceImpl.of(Textual.of(OffsetPoint.zero, "abc"));
        assertEquals('a', source.readChar());
        assertEquals('b', source.readChar());
        assertEquals('c', source.readChar());
        assertEquals(0, source.readChar());
    }

    @Test
    void testReadCharWide() {
        var source = LexerSourceImpl.of(Textual.of(OffsetPoint.zero, "あい"));
        assertEquals('あ', source.readChar());
        assertEquals('い', source.readChar());
        assertEquals(0, source.readChar());
    }

    @Test
    void testReadCharSurrogate() {
        var source = LexerSourceImpl.of(Textual.of(OffsetPoint.zero, "😊"));
        char h = source.readChar();
        char l = source.readChar();
        assertEquals("😊", new String(new char[] { h, l }));
        assertEquals(0, source.readChar());
    }

    @Test
    void testPeekChar() {
        var source = LexerSourceImpl.of(Textual.of(OffsetPoint.zero, "abc"));
        assertEquals('a', source.peekChar());
        assertEquals('b', source.peekChar());
        assertEquals('c', source.peekChar());
        assertEquals(0, source.peekChar());

        assertEquals('a', source.readChar());
        assertEquals('b', source.peekChar());
        source.commitPeek();
        assertEquals('c', source.readChar());
    }

}
