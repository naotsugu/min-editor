/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.core.syntax2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The Trie.
 * @author Naotsugu Kobayashi
 */
public class Trie {

    /** The root node. */
    private final TrieNode root;

    private Trie() {
        root = new TrieNode(null);
    }

    /**
     * Create a new {@link Trie}.
     * @param wordSequence the word sequence
     * @return a new {@link Trie}
     */
    public static Trie of(String wordSequence) {
        Trie trie = new Trie();
        Stream.of(wordSequence.split("[,\\s]")).forEach(trie::put);
        return trie;
    }

    /**
     * Put the word.
     * @param word the word
     */
    public void put(String word) {
        TrieNode node = root;
        for (int i = 0; i < word.length();) {
            int cp = word.codePointAt(i);
            node = node.createIfAbsent(cp);
            i += Character.charCount(cp);
        }
        node.setEndOfWord();
    }

    /**
     * Remove the word.
     * @param word the word
     */
    public void remove(String word) {
        TrieNode node = searchPrefix(word);
        if (node == null || !node.isEndOfWord()) {
            return;
        }
        node.setEndOfWord(false);
        node.removeIfEmpty();
    }

    /**
     * Gets whether the specified word matches.
     * @param word the words to be inspected
     * @return {@code true}, if the specified word matches
     */
    public boolean match(String word) {
        TrieNode node = searchPrefix(word);
        return node != null && node.isEndOfWord();
    }

    /**
     * Gets whether the specified word left-hand matches.
     * @param prefix the text to be inspected
     * @return {@code true}, if the specified word left-hand matches
     */
    public boolean startsWith(String prefix) {
        return searchPrefix(prefix) != null;
    }

    /**
     * Gets the list of suggestion by the specified word.
     * @param word the specified word
     * @return the specified word
     */
    public List<String> suggestion(String word) {
        TrieNode node = root;
        for (int i = 0; i < word.length();) {
            int cp = word.codePointAt(i);
            if (node.contains(cp)) {
                node = node.get(cp);
            } else {
                break;
            }
            i += Character.charCount(cp);
        }
        return node.childKeys();
    }

    private TrieNode searchPrefix(String word) {
        TrieNode node = root;
        for (int i = 0; i < word.length();) {
            int cp = word.codePointAt(i);
            if (node.contains(cp)) {
                node = node.get(cp);
            } else {
                return null;
            }
            i += Character.charCount(cp);
        }
        return node;
    }

    /**
     * TrieNode.
     */
    class TrieNode {

        /** The parent node. */
        private final TrieNode parent;
        /** The children. */
        private final Map<Integer, TrieNode> children;
        /** The marker of end of word. */
        private boolean endOfWord;

        /**
         * Constructor.
         * @param parent the parent node
         */
        TrieNode(TrieNode parent) {
            this.parent = parent;
            this.children = new HashMap<>();
        }

        TrieNode createIfAbsent(Integer key) {
            return children.computeIfAbsent(key, k -> new TrieNode(this));
        }

        boolean contains(int codePoint) {
            return children.containsKey(codePoint);
        }

        TrieNode get(int codePoint) {
            return children.get(codePoint);
        }

        void put(int codePoint, TrieNode node) {
            children.put(codePoint, node);
        }

        void removeIfEmpty() {
            if (parent == null) {
                return;
            }
            if (children.isEmpty()) {
                parent.children.remove(key());
                parent.removeIfEmpty();
            }
        }

        private Integer key() {
            if (parent == null) {
                return null;
            }
            return parent.children.entrySet().stream()
                .filter(e -> e.getValue() == this)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
        }

        List<String> childKeys() {
            return children.keySet().stream().map(Character::toString).toList();
        }

        void setEndOfWord() {
            endOfWord = true;
        }

        void setEndOfWord(boolean endOfWord) {
            this.endOfWord = endOfWord;
        }

        boolean isEndOfWord() {
            return endOfWord;
        }
    }

}
