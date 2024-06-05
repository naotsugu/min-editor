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
package com.mammb.code.editor.syntax.sql;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.Scope;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;
import com.mammb.code.editor.syntax.base.Trie;

import static com.mammb.code.editor.syntax.sql.Sql.SqlToken.KEYWORD;
import static com.mammb.code.editor.syntax.sql.Sql.SqlToken.LINE_COMMENT;
import static com.mammb.code.editor.syntax.sql.Sql.SqlToken.TEXT;
import static com.mammb.code.editor.syntax.sql.Sql.SqlToken.ANY;

/**
 * SqlLexer.
 * @author Naotsugu Kobayashi
 */
public class SqlLexer implements Lexer {

    /** The syntax keywords. */
    private static final Trie keywords = Sql.keywords();

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
        return "sql";
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
            case '-' -> readComment(source);
            case '\''  -> readText(source);
            default -> readKeywords(source, ch);
        };
    }


    /**
     * Read comment.
     * @param source the lexer source
     * @return the token
     */
    private Token readComment(LexerSource source) {
        long pos = source.offset() + source.position();
        char ch = source.peekChar();
        if (ch == '-') {
            source.commitPeekBefore();
            return Token.of(LINE_COMMENT, Scope.INLINE_START, pos, 2);
        } else {
            source.rollbackPeek();
            return Token.any(source);
        }
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
     * Read keywords.
     * @param source the lexer source
     * @return the token
     */
    private Token readKeywords(LexerSource source, int cp) {

        long pos = source.offset() + source.position();
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toChars(cp));

        for (;;) {
            char ch = source.peekChar();
            if (!(('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z'))) {
                String str = sb.toString().toLowerCase();
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
