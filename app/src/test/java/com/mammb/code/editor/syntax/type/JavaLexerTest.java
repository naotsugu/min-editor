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
package com.mammb.code.editor.syntax.type;

import com.mammb.code.editor.syntax.LexerSource;
import com.mammb.code.editor.syntax.ScopeType;
import com.mammb.code.editor.syntax.Token;
import org.junit.jupiter.api.Test;
import static com.mammb.code.editor.syntax.type.JavaLexer.Type.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Text of {@link JavaLexer}.
 * @author Naotsugu Kobayashi
 */
class JavaLexerTest {

    @Test
    void nextToken() {
        var lexer = JavaLexer.of(LexerSource.of("public static void main(String... args) {"));
        assertEquals(new Token(KEYWORD, ScopeType.NEUTRAL, 0, 6), lexer.nextToken());
        assertEquals(new Token(SP, ScopeType.NEUTRAL, 6, 1), lexer.nextToken());
        assertEquals(new Token(KEYWORD, ScopeType.NEUTRAL, 7, 6), lexer.nextToken());
        assertEquals(new Token(SP, ScopeType.NEUTRAL, 13, 1), lexer.nextToken());
        assertEquals(new Token(KEYWORD, ScopeType.NEUTRAL, 14, 4), lexer.nextToken());
        assertEquals(new Token(SP, ScopeType.NEUTRAL, 18, 1), lexer.nextToken());
        assertEquals(new Token(ANY, ScopeType.NEUTRAL, 19, 4), lexer.nextToken());
    }

}

