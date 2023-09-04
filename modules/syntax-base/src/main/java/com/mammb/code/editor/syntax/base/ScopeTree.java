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
 * ScopeTree.
 * @author Naotsugu Kobayashi
 */
public interface ScopeTree {

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
     * Get the current(edge) scope node in the current context.
     * @return the current(edge) scope node
     */
    ScopeNode primeInContext();

    /**
     * Get the scope node at the specified offset position.
     * @param offset the specified offset position
     * @return the scope node at the specified offset position
     */
    ScopeNode at(int offset);

}