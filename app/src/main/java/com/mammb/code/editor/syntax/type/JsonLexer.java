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

import com.mammb.code.editor.model.Coloring;
import com.mammb.code.editor.model.Decorated;
import com.mammb.code.editor.syntax.DecorateTo;
import com.mammb.code.editor.syntax.Lexer;
import com.mammb.code.editor.syntax.LexerSource;
import com.mammb.code.editor.syntax.LexicalScope;
import com.mammb.code.editor.syntax.ScopeType;
import com.mammb.code.editor.syntax.Token;

/**
 * Lexer.
 * @author Naotsugu Kobayashi
 */
public class JsonLexer implements Lexer, DecorateTo {

    /** Token type. */
    protected interface Type extends TokenType {
        int KEY = serial.getAndIncrement();
        int TEXT = serial.getAndIncrement();
        int NUMBER = serial.getAndIncrement();
        int LITERAL = serial.getAndIncrement();
        int LINE_COMMENT = serial.getAndIncrement();
        int COMMENT = serial.getAndIncrement();
    }

    /** The name. */
    private final String name;

    /** The input string. */
    private LexerSource source;


    /**
     * Constructor.
     * @param name the name
     * @param source the {@link LexerSource}
     */
    private JsonLexer(String name, LexerSource source) {
        this.name = name;
        this.source = source;
    }


    /**
     * Create a new lexer.
     * @param source the {@link LexerSource}
     * @return a lexer
     */
    public static Lexer of(LexerSource source) {
        return new JsonLexer("", source);
    }


    /**
     * Create a new lexer.
     * @param name the name
     * @return a lexer
     */
    public static Lexer of(String name) {
        return new JsonLexer(name, null);
    }


    @Override
    public String name() {
        return name;
    }


    @Override
    public void setSource(LexerSource source, LexicalScope lexicalScope) {
        this.source = source;
    }


    @Override
    public Token nextToken() {

        if (source == null) return TokenType.empty(null);

        char ch = source.readChar();
        return switch (ch) {
            case ' ', '\t' -> TokenType.whitespace(source);
            case '\n', '\r' -> TokenType.lineEnd(source);
            case '/' -> readComment(source);
            case '"' -> readString(source);
            case 't' -> readTrue(source);
            case 'f' -> readFalse(source);
            case 'n' -> readNull(source);
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' -> readNumber(source);
            case 0 -> new Token(Type.EMPTY, ScopeType.NEUTRAL, 0, 0);
            default -> TokenType.any(source);
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
            return new Token(Type.LINE_COMMENT, ScopeType.INLINE_START, pos, 2);
        } else if (ch == '*') {
            source.commitPeek();
            return new Token(JavaLexer.Type.COMMENT, ScopeType.BLOCK_START, pos, 2);
        } else {
            source.rollbackPeek();
            return TokenType.any(source);
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
                    return new Token(Type.KEY, ScopeType.NEUTRAL, pos, source.position() + 1 - pos);
                } else {
                    return new Token(Type.TEXT, ScopeType.NEUTRAL, pos, source.position() + 1 - pos);
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
                return TokenType.any(source);
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
                return TokenType.any(source);
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
                return TokenType.any(source);
            }
        }
        source.commitPeekBefore();
        return new Token(Type.NUMBER, ScopeType.NEUTRAL, pos, source.position() - pos);
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
            return new Token(Type.LITERAL, ScopeType.NEUTRAL, source.position() - 4, 4);
        }
        source.rollbackPeek();
        return TokenType.any(source);
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
            return new Token(Type.LITERAL, ScopeType.NEUTRAL, source.position() - 5, 5);
        }
        source.rollbackPeek();
        return TokenType.any(source);
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
            return new Token(Type.LITERAL, ScopeType.NEUTRAL, source.position() - 4, 4);
        }
        source.rollbackPeek();
        return TokenType.any(source);
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


    @Override
    public Decorated apply(int type) {
        return (type == Type.KEY) ? Decorated.of(Coloring.DarkOrange) :
               (type == Type.TEXT) ? Decorated.of(Coloring.DarkGreen) :
               (type == Type.NUMBER) ? Decorated.of(Coloring.DarkSkyBlue) :
               (type == Type.LITERAL) ? Decorated.of(Coloring.DarkOrange) :
               (type == Type.COMMENT) ? Decorated.of(Coloring.DarkGray) :
               (type == Type.LINE_COMMENT) ? Decorated.of(Coloring.DarkGray) : Decorated.empty();
    }

}
