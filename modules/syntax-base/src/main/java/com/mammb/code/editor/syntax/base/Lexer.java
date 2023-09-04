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
 * Lexer.
 * @author Naotsugu Kobayashi
 */
public interface Lexer {

    /**
     * Get the name.
     * @return the name
     */
    String name();

    /**
     * Set the source.
     * @param source the source.
     * @param scope the scope.
     */
    void setSource(LexerSource source, ScopeTree scope);

    /**
     * Gets the next token.
     * @return the next token
     */
    Token nextToken();

}