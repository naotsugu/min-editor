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

import com.mammb.code.editor.syntax.base.impl.ScopeTreeImpl;

/**
 * ScopeTree.
 * @author Naotsugu Kobayashi
 */
public interface ScopeTree {

    /**
     * Truncates the scope after the specified offset.
     * @param offset the specified offset
     */
    void truncate(long offset);

    /**
     * push the token.
     * @param token the token
     */
    void push(Token token);

    /**
     * Get the current(edge) scope node.
     * @return the current(edge) scope node
     */
    ScopeNode current();

    /**
     * Get the scope node at the specified offset position.
     * @param offset the specified offset position
     * @return the scope node at the specified offset position
     */
    ScopeNode at(long offset);

    /**
     * Create a new ScopeTree.
     * @param context the name of context
     * @return a new ScopeTree
     */
    static ScopeTree of(String context) {
        return ScopeTreeImpl.of(context);
    }

}
