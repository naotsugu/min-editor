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
package com.mammb.code.editor.syntax.base.impl;

import com.mammb.code.editor.syntax.base.ScopeNode;
import com.mammb.code.editor.syntax.base.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ScopeTreeNodeImpl.
 * @author Naotsugu Kobayashi
 */
public class ScopeTreeNode implements ScopeNode {

    /** The parent node. */
    private ScopeTreeNode parent;

    /** The children. */
    private final List<ScopeTreeNode> children = new ArrayList<>();

    /** The token in open position. */
    private Token open;

    /** The token in close position. */
    private Token close;


    /**
     * Constructor.
     */
    private ScopeTreeNode(ScopeTreeNode parent, Token open) {
        this.parent = parent;
        this.open = open;
    }

    /**
     * Create a root node.
     * @return a root node
     */
    public static ScopeTreeNode root() {
        return new ScopeTreeNode(null, Token.empty(null));
    }

    /**
     * Create a node of scope started.
     * @param parent the parent node
     * @param open the token in open position
     * @return
     */
    public static ScopeTreeNode startOf(ScopeTreeNode parent, Token open) {
        if (open.scope().isEnd()) {
            throw new IllegalArgumentException();
        }
        return new ScopeTreeNode(parent, open);
    }

    @Override
    public ScopeTreeNode parent() {
        return parent;
    }

    @Override
    public Token open() {
        return open;
    }

    @Override
    public Token close() {
        return close;
    }

    void closeOn(Token token) {
        this.close = token;
    }

    List<ScopeTreeNode> children() {
        return children;
    }

    void removeAfter(int offset) {
        children().removeIf(node -> node.open().position() >= offset);
        children().forEach(node -> node.removeAfter(offset));
    }

    ScopeTreeNode at(int offset) {
        if (open.position() > offset) {
            return null;
        } else if (open.position() <= offset && !children.isEmpty()) {
            var find = children.stream()
                .filter(n -> n.within(offset))
                .map(n -> n.at(offset))
                .filter(Objects::nonNull)
                .findFirst();
            if (find.isPresent()) {
                return find.get();
            }
        }
        return this;
    }

    boolean within(int offset) {
        return (isOpen() && open.position() <= offset) ||
            (isClosed() && open.position() <= offset && offset <= close.position());
    }

}
