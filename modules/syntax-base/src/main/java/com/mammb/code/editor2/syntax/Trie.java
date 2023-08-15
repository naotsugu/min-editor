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

import com.mammb.code.editor2.syntax.impl.TrieImpl;

import java.util.List;

/**
 * Trie.
 * @author Naotsugu Kobayashi
 */
public interface Trie {

    /**
     * Put the word.
     * @param word the word
     */
    void put(String word);

    /**
     * Remove the word.
     * @param word the word
     */
    void remove(String word);

    /**
     * Gets whether the specified word matches.
     * @param word the words to be inspected
     * @return {@code true}, if the specified word matches
     */
    boolean match(String word);

    /**
     * Gets whether the specified word left-hand matches.
     * @param prefix the text to be inspected
     * @return {@code true}, if the specified word left-hand matches
     */
    boolean startsWith(String prefix);

    /**
     * Gets the list of suggestion by the specified word.
     * @param word the specified word
     * @return the specified word
     */
    List<String> suggestion(String word);

    /**
     *
     * @return
     */
    static Trie of() {
        return new TrieImpl();
    }

}
