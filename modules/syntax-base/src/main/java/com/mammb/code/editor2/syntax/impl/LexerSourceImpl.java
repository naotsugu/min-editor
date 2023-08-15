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
package com.mammb.code.editor2.syntax.impl;

import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.syntax.LexerSource;
import java.util.Objects;

/**
 * The lexer source implementation.
 * @author Naotsugu Kobayashi
 */
public class LexerSourceImpl implements LexerSource {

    /** The input textual. */
    private Textual textual;

    /** The current position in input (points to current char). */
    private int position = -1;

    /** The peeked count. */
    private int peekCount = 0;

    /**
     * Constructor.
     * @param textual the input textual
     */
    private LexerSourceImpl(Textual textual) {
        this.textual = Objects.requireNonNull(textual);
    }

    /**
     * Create a new LexerSource.
     * @param textual the input textual
     * @return a LexerSource
     */
    public static LexerSource of(Textual textual) {
        return new LexerSourceImpl(textual);
    }


    @Override
    public char readChar() {
        position++;
        peekCount = 0;
        if (position >= textual.length()) {
            position = textual.length();
            return 0;
        }
        return textual.text().charAt(position);

    }

    @Override
    public char currentChar() {
        if (position < 0 || position >= textual.length()) {
            return 0;
        }
        return textual.text().charAt(position);

    }

    @Override
    public char peekChar() {
        peekCount++;
        int index = position + peekCount;
        if (index >= textual.length()) {
            peekCount = Math.min(textual.length() - position, peekCount);
            return 0;
        }
        return textual.text().charAt(index);

    }

    @Override
    public void commitPeek() {
        position += peekCount;
        peekCount = 0;
    }

    @Override
    public void commitPeekBefore() {
        position += Math.max(peekCount - 1, 0);
        peekCount = 0;
    }

    @Override
    public void rollbackPeek() {
        peekCount = 0;
    }

    @Override
    public int position() {
        return position;
    }

    @Override
    public int length() {
        return textual.length();
    }

}
