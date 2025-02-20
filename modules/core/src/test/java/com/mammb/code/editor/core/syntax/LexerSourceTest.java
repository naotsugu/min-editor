/*
 * Copyright 2022-2024 the original author or authors.
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
package com.mammb.code.editor.core.syntax;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link LexerSource}.
 * @author Naotsugu Kobayashi
 */
class LexerSourceTest {

    @Test
    void textPeek() {
        var lexerSource = LexerSource.of(1, "abc");
        assertEquals("a", lexerSource.peek().text());
        assertEquals("b", lexerSource.peek().text());
        assertEquals("c", lexerSource.peek().text());
        lexerSource.rollbackPeek();
        assertEquals("a", lexerSource.peek().text());
        assertEquals("b", lexerSource.peek().text());
        lexerSource.commitPeek();
        assertEquals("c", lexerSource.peek().text());
        lexerSource.rollbackPeek();
        assertEquals("c", lexerSource.peek().text());
    }

    @Test
    void textNext() {
        var lexerSource = LexerSource.of(1, "abc123");
        assertEquals("a", lexerSource.next().text());
        assertEquals("b", lexerSource.next().text());
        assertEquals("c", lexerSource.next(1).text());
        assertEquals("12", lexerSource.next(2).text());
        assertEquals("3", lexerSource.next(2).text());
    }

    @Test
    void textNextUntilWs() {
        var lexerSource = LexerSource.of(1, "abc 123");
        assertEquals("abc", lexerSource.nextUntilWs().text());
        lexerSource.next();
        assertEquals("123", lexerSource.nextUntilWs().text());
    }

    @Test
    void textNextRemaining() {
        var lexerSource = LexerSource.of(1, "abc123");
        assertEquals("abc", lexerSource.next(3).text());
        assertEquals("123", lexerSource.nextRemaining().text());
    }

    @Test
    void textMatch() {
        var lexerSource = LexerSource.of(1, "abc123");
        assertEquals(true, lexerSource.match("abc"));
        lexerSource.next();
        assertEquals(false, lexerSource.match("abc"));
        lexerSource.next(2);
        assertEquals(true, lexerSource.match("123"));
    }
}
