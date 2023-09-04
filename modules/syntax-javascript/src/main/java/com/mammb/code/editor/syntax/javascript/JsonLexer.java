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
package com.mammb.code.editor.syntax.javascript;

import com.mammb.code.editor.syntax.base.Lexer;
import com.mammb.code.editor.syntax.base.LexerSource;
import com.mammb.code.editor.syntax.base.Scope;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;

import static com.mammb.code.editor.syntax.javascript.Json.JsonToken.*;

/**
 * JsonLexer.
 * @author Naotsugu Kobayashi
 */
public class JsonLexer implements Lexer {

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
        return "json";
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
            case '/' -> readComment(source);
            case '"' -> readString(source);
            case 't' -> readTrue(source);
            case 'f' -> readFalse(source);
            case 'n' -> readNull(source);
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-' -> readNumber(source);
            case 0  -> Token.empty(source);
            default -> Token.any(source);
        };
    }


    /**
     * Read comment.
     * @param source the lexer source
     * @return the token
     */
    private Token readComment(LexerSource source) {
        int pos = source.position();
        char ch = source.peekChar();
        if (ch == '/') {
            source.commitPeek();
            return Token.of(LINE_COMMENT, Scope.INLINE_START, pos, 2);
        } else if (ch == '*') {
            source.commitPeek();
            return Token.of(COMMENT, Scope.BLOCK_START, pos, 2);
        } else {
            source.rollbackPeek();
            return Token.any(source);
        }
    }


    /**
     * Read the string.
     * @param source the lexer source
     * @return the token
     */
    private Token readString(LexerSource source) {
        int pos = source.position();
        char prev = 0;
        for (;;) {
            char ch = source.peekChar();
            if (ch < ' ' || (prev != '\\' && ch == '"')) {
                source.commitPeek();
                if (peekNextChar(source) == ':') {
                    return Token.of(KEY, Scope.NEUTRAL, pos, source.position() + 1 - pos);
                } else {
                    return Token.of(TEXT, Scope.NEUTRAL, pos, source.position() + 1 - pos);
                }
            }
            prev = ch;
        }
    }


    /**
     * Read the number.
     * @param source the lexer source
     * @return the token
     */
    private Token readNumber(LexerSource source) {

        int pos = source.position();
        char ch = source.currentChar();

        if (ch == '-') {
            ch = source.peekChar();
            if (ch < '0' || ch > '9') {
                source.rollbackPeek();
                return Token.any(source);
            }
        }

        if (ch == '0') {
            ch = source.peekChar();
        } else {
            do {
                ch = source.peekChar();
            } while (ch >= '0' && ch <= '9');
        }

        if (ch == '.') {
            int count = 0;
            do {
                ch = source.peekChar();
                count++;
            } while (ch >= '0' && ch <= '9');
            if (count == 1) {
                source.rollbackPeek();
                return Token.any(source);
            }
        }

        if (ch == 'e' || ch == 'E') {
            ch = source.peekChar();
            if (ch == '+' || ch == '-') {
                ch = source.peekChar();
            }
            int count;
            for (count = 0; ch >= '0' && ch <= '9'; count++) {
                ch = source.peekChar();
            }
            if (count == 0) {
                source.rollbackPeek();
                return Token.any(source);
            }
        }
        source.commitPeekBefore();
        return Token.of(NUMBER, Scope.NEUTRAL, pos, source.position() - pos);
    }


    /**
     * Read true.
     * @param source the lexer source
     * @return the token
     */
    private Token readTrue(LexerSource source) {
        if (source.peekChar() == 'r' &&
            source.peekChar() == 'u' &&
            source.peekChar() == 'e') {
            source.commitPeek();
            return Token.of(LITERAL, Scope.NEUTRAL, source.position() - 4, 4);
        }
        source.rollbackPeek();
        return Token.any(source);
    }


    /**
     * Read false.
     * @param source the lexer source
     * @return the token
     */
    private Token readFalse(LexerSource source) {
        if (source.peekChar() == 'a' &&
            source.peekChar() == 'l' &&
            source.peekChar() == 's' &&
            source.peekChar() == 'e') {
            source.commitPeek();
            return Token.of(LITERAL, Scope.NEUTRAL, source.position() - 5, 5);
        }
        source.rollbackPeek();
        return Token.any(source);
    }


    /**
     * Read null.
     * @param source the lexer source
     * @return the token
     */
    private Token readNull(LexerSource source) {
        if (source.peekChar() == 'u' &&
            source.peekChar() == 'l' &&
            source.peekChar() == 'l') {
            source.commitPeek();
            return Token.of(LITERAL, Scope.NEUTRAL, source.position() - 4, 4);
        }
        source.rollbackPeek();
        return Token.any(source);
    }


    /**
     * Peek the next char.
     * @param source the lexer source
     * @return the next char
     */
    private char peekNextChar(LexerSource source) {
        char ch;
        for (;;) {
            ch = source.peekChar();
            if (ch == 0) break;
            if (!Character.isWhitespace(ch)) break;
        }
        source.rollbackPeek();
        return ch;
    }

}
