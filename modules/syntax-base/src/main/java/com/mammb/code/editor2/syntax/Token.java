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
package com.mammb.code.editor2.syntax;

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
