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

import com.mammb.code.editor2.syntax.Token;
import com.mammb.code.editor2.syntax.TokenType;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Optional;
import java.util.Collection;
import java.util.Comparator;

/**
 * LexicalScope.
 * @author Naotsugu Kobayashi
 */
public class LexicalScope {

    /** The tokens(key:offset value:token). */
    private final TreeMap<Integer, Token> tokens = new TreeMap<>();

    /** The context scopes(key:token type number). */
    private final Map<TokenType, Deque<Token>> contextScopes = new HashMap<>();

    /** The block scopes(key:tokenType value:tokens). */
    private final Map<TokenType, Deque<Token>> blockScopes = new HashMap<>();

    /** The inline scopes(key:tokenType value:tokens). */
    private final Map<TokenType, Deque<Token>> inlineScopes = new HashMap<>();

    /** The high water offset. */
    private int highWaterOffset;


    /**
     * Start scope processing.
     * @param offset the offset
     */
    public void start(int offset) {
        markDirty(offset);
        blockScopes.clear();
        inlineScopes.clear();
        for (Map.Entry<Integer, Token> entry : tokens.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }


    /**
     * Deletes the scope after the specified offset as dirty.
     * @param offset the specified offset
     */
    public void markDirty(int offset) {
        if (offset < highWaterOffset) {
            if (!tokens.isEmpty()) {
                tokens.subMap(offset, Integer.MAX_VALUE).clear();
            }
            highWaterOffset = offset;
        }
    }

    /**
     * Put the token.
     * @param offset the offset
     * @param token the token
     */
    public void put(int offset, Token token) {

        if (token.scope().isNeutral()) {
            throw new IllegalArgumentException();
        }

        if (token.scope().isBlock()) {
            tokens.put(offset, token);
            highWaterOffset = offset;
            putScope(token, blockScopes);
            return;
        }
        if (token.scope().isInline()) {
            if (token.type() == TokenType.EOL) {
                inlineScopes.clear();
            } else { //if (token.scope().isStart() || token.scope().isEnd()) {
                putScope(token, inlineScopes);
            }
            return;
        }
        if (token.scope().isContext()) {
            putScope(token, contextScopes);
        }
    }


    /**
     * Get the current scope token.
     * @return the current scope token
     */
    public Optional<Token> current() {

        Optional<Token> blockScope = blockScopes.values().stream()
            .flatMap(Collection::stream)
            .min(Comparator.comparing(Token::type));
        if (blockScope.isPresent()) {
            return blockScope;
        }

        return inlineScopes.values().stream()
            .flatMap(Collection::stream)
            .min(Comparator.comparing(Token::type));
    }


    /**
     * Put the scope.
     * @param token the token
     * @param scopes the target scope
     */
    private void putScope(Token token, Map<TokenType, Deque<Token>> scopes) {

        if (token.scope().isStart()) {
            scopes.computeIfAbsent(token.type(), k -> new ArrayDeque<>()).push(token);
            return;
        }

        if (token.scope().isEnd() && scopes.containsKey(token.type())) {
            scopes.get(token.type()).poll();
            return;
        }

        if (token.scope().isAny()) {
            // toggle scope if any
            Deque<Token> deque = scopes.get(token.type());
            if (deque == null || deque.isEmpty()) {
                scopes.computeIfAbsent(token.type(), k -> new ArrayDeque<>()).push(token);
            } else {
                scopes.get(token.type()).poll();
            }
        }
    }

}
