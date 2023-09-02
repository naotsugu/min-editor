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

/**
 * Token.
 * @author Naotsugu Kobayashi
 */
public interface Token {

    /**
     * Get the token type.
     * @return the token type
     */
    TokenType type();

    /**
     * Get the token scope.
     * @return the token scope
     */
    Scope scope();

    /**
     * Get the position.
     * @return the position
     */
    int position();

    /**
     * Get the length.
     * @return the length
     */
    int length();

    /**
     * Get the context.
     * @return the context
     */
    String context();

    /**
     * Get whether this type is empty.
     * @return {@code true}, if this type is empty
     */
    default boolean isEmpty() {
        return length() <= 0;
    }


    /**
     * Get the context implicit empty token.
     * @param context the name of context
     * @return the context implicit empty token
     */
    static Token implicit(String context) {
        return of(TokenType.EMPTY, Scope.CONTEXT_ANY, 0, 0, context);
    }


    /**
     * Get the empty token.
     * @param source the lexer source
     * @return the empty token
     */
    static Token empty(LexerSource source) {
        return of(TokenType.EMPTY, Scope.NEUTRAL, (source == null) ? 0 : source.offset() + source.position(), 0);
    }


    /**
     * Get an any token.
     * @param source the lexer source
     * @return an any token
     */
    static Token any(LexerSource source) {
        return of(TokenType.ANY, Scope.NEUTRAL, source.offset() + source.position(), 1);
    }


    /**
     * Get the whitespace token.
     * @param source the lexer source
     * @return the whitespace token
     */
    static Token whitespace(LexerSource source) {
        return of(TokenType.SP, Scope.NEUTRAL, source.offset() + source.position(), 1);
    }


    /**
     * Read line end.
     * @param source the lexer source
     * @return the token
     */
    static Token lineEnd(LexerSource source) {
        int pos = source.position();
        if (source.currentChar() == '\r' && source.peekChar() == '\n') {
            source.commitPeek();
            return of(TokenType.EOL, Scope.INLINE_END, source.offset() + pos, 2);
        } else {
            source.rollbackPeek();
            return of(TokenType.EOL, Scope.INLINE_END, source.offset() + pos, 1);
        }
    }


    /**
     * Create a new Token.
     * @param type the token type
     * @param scope the token scope
     * @param position the position
     * @param length the length
     * @return token
     */
    static Token of(TokenType type, Scope scope, int position, int length) {
        return of(type, scope, position, length, "");
    }


    /**
     * Create a new Token.
     * @param type the token type
     * @param scope the token scope
     * @param position the position
     * @param length the length
     * @param context the context
     * @return token
     */
    static Token of(TokenType type, Scope scope, int position, int length, String context) {
        record TokenRecode(TokenType type, Scope scope, int position, int length, String context) implements Token { }
        return new TokenRecode(type, scope, position, length, context);
    }

}
