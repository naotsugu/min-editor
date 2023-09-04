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
package com.mammb.code.editor.syntax.java;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.Scope;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;

/**
 * JavaDocLexer.
 * @author Naotsugu Kobayashi
 */
public class JavaDocLexer implements Lexer {

    /** The input string. */
    private LexerSource source;


    @Override
    public String name() {
        return "javadoc";
    }

    @Override
    public void setSource(LexerSource source, ScopeTree scope) {
        this.source = source;
    }

    @Override
    public Token nextToken() {

        if (source == null) {
            return Token.empty(null);
        }

        char ch = source.readChar();
        return switch (ch) {
            case ' ', '\t'  -> Token.whitespace(source);
            case '\n', '\r' -> Token.lineEnd(source);
            case 0 -> Token.empty(source);
            default -> Token.any(source);
        };
    }


    /**
     * Get an any token.
     * @param source the lexer source
     * @return an any token
     */
    static Token text(LexerSource source) {
        return Token.of(JavaDoc.JavaDocToken.TEXT, Scope.NEUTRAL, source.offset() + source.position(), 1);
    }

}
