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
package com.mammb.code.editor2.syntax.java;

import com.mammb.code.editor2.syntax.Lexer;
import com.mammb.code.editor2.syntax.LexerSource;
import com.mammb.code.editor2.syntax.Scope;
import com.mammb.code.editor2.syntax.Token;
import com.mammb.code.editor2.syntax.Trie;

/**
 * JavaLexer.
 * @author Naotsugu Kobayashi
 */
public class JavaLexer implements Lexer {

    /** The syntax keywords. */
    private static final Trie keywords = Java.keywords();

    /** The input string. */
    private LexerSource source;


    public JavaLexer(LexerSource source) {
        this.source = source;
    }


    @Override
    public String name() {
        return "java";
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
            default -> Character.isJavaIdentifierStart(ch)
                ? readIdentifier(source)
                : Token.any(source);
        };
    }


    /**
     * Read identifier.
     * @param source the lexer source
     * @return the token
     */
    private Token readIdentifier(LexerSource source) {

        int pos = source.position();
        StringBuilder sb = new StringBuilder();
        sb.append(source.currentChar());

        for (;;) {
            char ch = source.peekChar();
            if (!Character.isJavaIdentifierPart(ch)) {
                String str = sb.toString();
                source.commitPeekBefore();
                if (keywords.match(str)) {
                    return Token.of(Java.ToKenType.KEYWORD, Scope.NEUTRAL, pos, str.length());
                } else {
                    return Token.of(Java.ToKenType.ANY, Scope.NEUTRAL, pos, str.length());
                }
            }
            sb.append(source.readChar());
        }
    }

}
