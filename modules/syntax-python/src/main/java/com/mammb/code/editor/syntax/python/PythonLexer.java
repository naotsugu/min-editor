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
package com.mammb.code.editor.syntax.python;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.Scope;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;
import com.mammb.code.editor.syntax.base.Trie;

import static com.mammb.code.editor.syntax.python.Python.PythonToken.*;

/**
 * PythonLexer.
 * @author Naotsugu Kobayashi
 */
public class PythonLexer implements Lexer {

    /** The syntax keywords. */
    private static final Trie keywords = Python.keywords();

    /** The input string. */
    private LexerSource source;


    @Override
    public String name() {
        return "python";
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

        int ch = source.readChar();
        if (Character.isHighSurrogate((char) ch)) {
            ch = Character.toCodePoint((char) ch, source.currentChar());
        }

        return switch (ch) {
            case ' ', '\t'  -> Token.whitespace(source);
            case '\n', '\r' -> Token.lineEnd(source);
            case 0    -> Token.empty(source);
            case '#' -> readComment(source);
            default -> Python.isIdentifierStart(ch)
                ? readIdentifier(source, ch)
                : Token.any(source);
        };

    }


    /**
     * Read comment.
     * @param source the lexer source
     * @return the token
     */
    private Token readComment(LexerSource source) {
        long pos = source.offset() + source.position();
        source.commitPeek();
        return Token.of(LINE_COMMENT, Scope.INLINE_START, pos, 1);
    }


    /**
     * Read identifier.
     * @param source the lexer source
     * @return the token
     */
    private Token readIdentifier(LexerSource source, int cp) {

        long pos = source.offset() + source.position();
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toChars(cp));

        for (;;) {
            char ch = source.peekChar();
            if (!Python.isIdentifierPart(ch)) {
                String str = sb.toString();
                source.commitPeekBefore();
                if (keywords.match(str)) {
                    return Token.of(KEYWORD, Scope.NEUTRAL, pos, str.length());
                } else {
                    return Token.of(ANY, Scope.NEUTRAL, pos, str.length());
                }
            }
            sb.append(source.readChar());
        }

    }

}
