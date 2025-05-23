/*
 * Copyright 2022-2025 the original author or authors.
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
package com.mammb.code.editor.core.syntax;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * The lexer source.
 * @author Naotsugu Kobayashi
 */
public class LexerSource {

    private int row;
    private String text;
    private int index = 0;
    private int peek = 0;

    private LexerSource(int row, String text) {
        this.row = row;
        this.text = text;
    }

    /**
     * Create a new {@link LexerSource}.
     * @param row the number of rows
     * @param source the source text
     * @return a new {@link LexerSource}
     */
    public static LexerSource of(int row, String source) {
        return new LexerSource(row, source);
    }

    /**
     * Get the number of rows.
     * @return the number of rows
     */
    public int row() {
        return row;
    }

    /**
     * Get the source text.
     * @return the source text
     */
    public String text() {
        return text;
    }

    /**
     * Get the source text length.
     * @return the source text length
     */
    public int length() {
        return text.length();
    }

    /**
     * Get the current index.
     * @return the current index
     */
    public int index() {
        return index;
    }

    public boolean hasNext() {
        return index < text.length();
    }

    public Indexed peek() {
        var ret = new Indexed(index + peek, text.charAt(index + peek), text.length());
        peek++;
        return ret;
    }

    public LexerSource commitPeek() {
        index += peek;
        peek = 0;
        return this;
    }

    public LexerSource rollbackPeek() {
        peek = 0;
        return this;
    }

    public Indexed next() {
        var ret = new Indexed(index, text.charAt(index), text.length());
        index++;
        peek = 0;
        return ret;
    }

    public Indexed next(int n) {
        int endIndex = Math.min(index + n, text.length());
        var ret = new Indexed(index, text.substring(index, endIndex), text.length());
        index = endIndex;
        peek = 0;
        return ret;
    }

    public Indexed nextUntilWs() {
        String split = text.substring(index).split("\\s", 2)[0];
        return next(split.length());
    }

    public Indexed nextUntil(Predicate<Character> predicate) {
        int i = index;
        for (; i < text.length(); i++) {
            if (!predicate.test(text.charAt(i))) break;
        }
        var ret = new Indexed(index, text.substring(index, i), text.length());
        index = i;
        peek = 0;
        return ret;
    }

    public Indexed nextRemaining() {
        var ret = new Indexed(index, text.substring(index), text.length());
        index = text.length();
        peek = 0;
        return ret;
    }

    public boolean match(CharSequence cs) {
        return index + cs.length() <= text.length() &&
            Objects.equals(text.substring(index, index + cs.length()), cs.toString());
    }

    public record Indexed(int index, String text, int parentLength) {
        private Indexed(int index, char ch, int parentLength) {
            this(index, String.valueOf(ch), parentLength);
        }
        public char ch() {
            return length() == 0 ? 0 : text.charAt(0);
        }
        public int lastIndex() {
            return index + text.length() - 1;
        }
        public int length() { return text.length(); }
        public boolean isFirst() { return index == 0; }
        public boolean isLast() { return index == parentLength - 1; }
    }
}
