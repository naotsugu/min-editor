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
package com.mammb.code.editor.syntax.base;

import com.mammb.code.editor.model.style.Style;
import com.mammb.code.editor.model.style.StyleSpan;
import com.mammb.code.editor.model.style.StyledText;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import org.junit.jupiter.api.Test;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of {@link SyntaxTranslate}.
 * @author Naotsugu Kobayashi
 */
class SyntaxTranslateTest {

    @Test void applyTo() {
        TokenType FENCE = TokenType.build();
        Lexer lexer = lexer(
            Token.of(FENCE, Scope.BLOCK_START, 0, 4, "z"),
            Token.of(TokenType.EOL, Scope.INLINE_END, 4, 1)
        );
        SyntaxTranslate target = new SyntaxTranslate(lexer, "#808080");
        StyledText styledText = target.applyTo(Textual.of(OffsetPoint.zero, "```z\n"));
        assertEquals(1, styledText.styles().size());
        StyleSpan span = styledText.styles().get(0);
        assertEquals("z", (span.style() instanceof Style.Context ctx) ? ctx.name() : "");
        assertEquals(5, span.point());
        assertEquals(0, span.length());

        styledText = target.applyTo(Textual.of(OffsetPoint.of(1, 5, 5), ""));
        assertEquals(1, styledText.styles().size());
        span = styledText.styles().get(0);
        assertEquals("z", (span.style() instanceof Style.Context ctx) ? ctx.name() : "");
        assertEquals(0, span.point());
        assertEquals(0, span.length());
    }


    private Lexer lexer(Token... tokens) {
        return new Lexer() {
            Deque<Token> queue = new ArrayDeque<>(Arrays.asList(tokens));
            @Override public String name() { return "md"; }
            @Override public void setSource(LexerSource source, ScopeTree scope) { }
            @Override public Token nextToken() {
                return queue.isEmpty()
                    ? Token.empty(null)
                    : queue.poll();
            }
        };
    }
}
