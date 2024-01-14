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

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * ScopeNode.
 * @author Naotsugu Kobayashi
 */
public interface ScopeNode {

    /**
     * Get the parent node.
     * @return the parent node.
     * {@code null} if this node is the root node
     */
    ScopeNode parent();

    /**
     * Get the token in open position.
     * @return the token in open position
     */
    Token open();

    /**
     * Get the token in close position.
     * @return the token in close position
     */
    Token close();


    boolean hasScope(Predicate<ScopeNode> predicate);

    Optional<ScopeNode> select(Predicate<ScopeNode> predicate);

    Optional<ScopeNode> selectPrime(Predicate<ScopeNode> until);

    /**
     * Collects nodes that meet specified conditions(Excluding root node).
     * @param filter the filter
     * @return Nodes matching the specified conditions
     */
    List<ScopeNode> collect(Predicate<ScopeNode> filter);

    /**
     * Gets the scope level of this node.
     * @return the scope level of this node
     */
    default int level() {
        ScopeNode node = this;
        int level = 0;
        while (!node.isRoot()) {
            level++;
            node = node.parent();
        }
        return level;
    }

    /**
     * Get the current context name.
     * @return the current context name
     */
    default String currentContext() {
        for (ScopeNode node = this; !node.isRoot(); node = node.parent()) {
            String context = node.open().context();
            if (!context.isEmpty()) {
                return context;
            }
        }
        return "";
    }

    /**
     * Get whether this node is the root or not.
     * @return {@code true}, if this node is the root
     */
    default boolean isRoot() {
        return parent() == null;
    }

    /**
     * Gets whether this node is an open node or not.
     * @return {@code true}, if this node is an open node
     */
    default boolean isOpen() {
        return close() == null;
    }

    /**
     * Gets whether this node is a closed node or not.
     * @return {@code true}, if this node is a closed node
     */
    default boolean isClosed() {
        return !isOpen();
    }

}
