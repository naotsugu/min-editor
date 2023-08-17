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
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

/**
 * LexicalScope.
 * @author Naotsugu Kobayashi
 */
public class LexicalScope {

    /** The context scopes. */
    private final TreeMap<Integer, Token> contextScopes = new TreeMap<>();

    /** The block scopes. */
    private final TreeMap<Integer, Token> blockScopes = new TreeMap<>();

    /** The inline scopes. */
    private final TreeMap<Integer, Token> inlineScopes = new TreeMap<>();



    public void init() {
        contextScopes.clear();
        blockScopes.clear();
        inlineScopes.clear();
    }


    public void init(int offset) {
        contextScopes.subMap(offset, Integer.MAX_VALUE).clear();
        blockScopes.subMap(offset, Integer.MAX_VALUE).clear();
        inlineScopes.subMap(offset, Integer.MAX_VALUE).clear();
    }


    /**
     * Put the token.
     * @param token the token
     * @param offset the offset
     */
    public void put(Token token, int offset) {

        if (token.scope().isNeutral()) {
            throw new IllegalArgumentException();
        }

        if (token.scope().isContext()) {
            putScope(token, offset, contextScopes);
        } else if (token.scope().isBlock()) {
            putScope(token, offset, blockScopes);
        } else if (token.scope().isInline()) {
            if (token.type() == TokenType.EOL) {
                inlineScopes.clear();
            } else { //if (token.scope().isStart() || token.scope().isEnd()) {
                putScope(token, offset, inlineScopes);
            }
        }
    }

    /**
     * Get the current context scope token.
     * @return the current context scope token
     */
    public Token currentContext() {
        if (!contextScopes.isEmpty()) {
            var last = contextScopes.lastEntry().getValue();
            if (last.scope().isStart() || last.scope().isAny()) {
                return last;
            }
        }
        return null;
    }


    /**
     * Get the current scope token.
     * @return the current scope token
     */
    public Token current() {

        if (!blockScopes.isEmpty()) {
            var last = blockScopes.lastEntry().getValue();
            if (last.scope().isStart() || last.scope().isAny()) {
                return last;
            }
        }

        if (!inlineScopes.isEmpty()) {
            var last = inlineScopes.lastEntry().getValue();
            if (last.scope().isStart() || last.scope().isAny()) {
                return last;
            }
        }

        return null;
    }


    /**
     * Put the scope.
     * @param token the token
     * @param scopes the target scope
     */
    private void putScope(Token token, int offset, TreeMap<Integer, Token> scopes) {

        if (token.scope().isStart()) {
            scopes.put(offset, token);
            return;
        }

        if (token.scope().isEnd()) {
            var before = beforeEntry(scopes, offset,
                t -> t.type() == token.type() && t.scope().isStart());
            if (before != null) {
                scopes.remove(before.getKey());
            }
            return;
        }

        if (token.scope().isAny()) {
            // toggle scope if any
            var before = beforeEntry(scopes, offset,
                t -> t.type() == token.type() && t.scope().isAny());
            if (before != null) {
                scopes.remove(before.getKey());
            } else {
                scopes.put(offset, token);
            }
            return;
        }

    }


    private static Map.Entry<Integer, Token> beforeEntry(
            TreeMap<Integer, Token> scopes, int offset, Predicate<Token> match) {
        while (true) {
            var entry = scopes.floorEntry(offset);
            if (entry == null) {
                return null;
            }
            if (match.test(entry.getValue())) {
                return entry;
            }
            offset = entry.getKey() - 1;
        }
    }

}
