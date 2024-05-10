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
package com.mammb.code.editor.syntax.toml;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;
import com.mammb.code.editor.syntax.base.*;

import static com.mammb.code.editor.syntax.toml.Toml.TomlToken.COMMENT;
import static com.mammb.code.editor.syntax.toml.Toml.TomlToken.TEXT;

/**
 * TomlLexer.
 * @author Naotsugu Kobayashi
 */
public class TomlLexer implements Lexer {

    /** The input string. */
    private LexerSource source;


    @Override
    public void setSource(LexerSource source, ScopeTree scope) {
        this.source = source;
    }


    @Override
    public String name() {
        return "toml";
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
            case '"', '\'' -> readText(source);
            case '#' -> readComment(source);
            case 0 -> Token.empty(source);
            default -> Token.any(source);
        };
    }

    /**
     * Read text.
     * @param source the lexer source
     * @return the token
     */
    private Token readText(LexerSource source) {
        char s = source.currentChar();
        int pos = source.position();
        for (;;) {
            char ch = source.peekChar();
            if (ch == 0) {
                source.rollbackPeek();
                return Token.any(source);
            } else if (ch == s) {
                source.commitPeek();
                return Token.of(TEXT, Scope.NEUTRAL, source.offset() + pos, source.position() + 1 - pos);
            }
        }
    }

    /**
     * Read comment.
     * @param source the lexer source
     * @return the token
     */
    private Token readComment(LexerSource source) {
        long pos = source.offset() + source.position();
        source.commitPeek();
        return Token.of(COMMENT, Scope.INLINE_START, pos, 1);
    }

}
