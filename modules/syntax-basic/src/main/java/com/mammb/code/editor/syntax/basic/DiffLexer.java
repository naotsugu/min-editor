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
package com.mammb.code.editor.syntax.basic;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.Scope;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;
import com.mammb.code.editor.syntax.base.TokenType;

import static com.mammb.code.editor.syntax.basic.Diff.DiffToken.ADD;
import static com.mammb.code.editor.syntax.basic.Diff.DiffToken.DEL;
import static com.mammb.code.editor.syntax.basic.Diff.DiffToken.SUM;

/**
 * DiffLexer.
 * @author Naotsugu Kobayashi
 */
public class DiffLexer implements Lexer {

    /** The input string. */
    private LexerSource source;

    /** The scope. */
    private ScopeTree scope;



    @Override
    public void setSource(LexerSource source, ScopeTree scope) {
        this.source = source;
        this.scope = scope;
    }


    @Override
    public String name() {
        return "diff";
    }


    @Override
    public Token nextToken() {

        if (source == null) {
            return Token.empty(null);
        }

        char ch = source.readChar();
        return switch (ch) {
            case 0  -> Token.empty(source);
            case '\n', '\r' -> Token.lineEnd(source);
            default -> {
                var sb = new StringBuilder();
                sb.append(ch);
                long position = source.offset() + source.position();
                for (;;) {
                    char c = source.peekChar();
                    if (c == 0 || c == '\n' || c == '\r') {
                        source.rollbackPeek();
                        break;
                    }
                    source.commitPeek();
                    sb.append(c);
                }
                if (ch == '+' || ch == '>') {
                    yield Token.of(ADD, Scope.INLINE_START, position, sb.length());
                }
                if (ch == '-' || ch == '<') {
                    yield Token.of(DEL, Scope.INLINE_START, position, sb.length());
                }
                if (sb.indexOf("@@") == 0) {
                    yield Token.of(SUM, Scope.INLINE_START, position, sb.length());
                }
                yield Token.of(TokenType.ANY, Scope.NEUTRAL, position, sb.length());
            }
        };
    }

}
