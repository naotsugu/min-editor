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
package com.mammb.code.editor.syntax.base;

import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.syntax.base.impl.LexerSourceImpl;

import java.util.Arrays;

/**
 * LexerSource.
 * @author Naotsugu Kobayashi
 */
public interface LexerSource {

    /**
     * Reads a character.
     * This operation moves the read position forward.
     * @return a character read. {@code 0} if the read target does not exist
     */
    char readChar();

    /**
     * Get a current character.
     * @return a character read. {@code 0} if the read target does not exist
     */
    char currentChar();

    /**
     * Reads a character.
     * This operation does not change the read position.
     * @return a character read. {@code 0} if the read target does not exist
     */
    char peekChar();

    /**
     * Forward the read position to the peeked position.
     */
    void commitPeek();

    /**
     * Forward the read position to the (one step before)peeked position.
     */
    void commitPeekBefore();

    /**
     * Rollback the peeked position.
     */
    void rollbackPeek();

    /**
     * Gets the current read position.
     * @return {@code -1} if loading has not started.
     */
    int position();

    /**
     * Gets the length.
     * @return the length
     */
    int length();

    /**
     * Get the start char (total) offset.
     * @return the start char (total) offset.
     */
    long offset();


    default char[] lookahead(int n) {
        char[] peeks = new char[n];
        for (int i = 0; i < n; i++) {
            peeks[i] = peekChar();
        }
        rollbackPeek();
        return peeks;
    }


    default boolean matchLookahead(char... chars) {
        return Arrays.equals(chars, lookahead(chars.length));
    }


    /**
     * Create a new LexerSource.
     * @param textual the input textual
     * @return a new LexerSource
     */
    static LexerSource of(Textual textual) {
        return LexerSourceImpl.of(textual);
    }

}
