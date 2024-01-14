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
package com.mammb.code.editor.syntax.base.impl;

import com.mammb.code.editor.syntax.base.ScopeNode;
import com.mammb.code.editor.syntax.base.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
     * @param context the name of context
     * @return a root node
     */
    public static ScopeTreeNode root(String context) {
        return new ScopeTreeNode(null, Token.implicit(context));
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

    @Override
    public boolean hasScope(Predicate<ScopeNode> predicate) {
        return predicate.test(this) || (!isRoot() && parent.hasScope(predicate));
    }

    @Override
    public Optional<ScopeNode> select(Predicate<ScopeNode> predicate) {
        if (predicate.test(this)) {
            return Optional.of(this);
        } else if (isRoot()) {
            return Optional.empty();
        } else {
            return parent().select(predicate);
        }
    }

    @Override
    public Optional<ScopeNode> selectPrime(Predicate<ScopeNode> until) {
        ScopeNode ret = null;
        for (ScopeNode n = this; !n.isRoot(); n = n.parent()) {
            if (until.test(n)) {
                break;
            }
            if (ret == null || n.open().type().serial() > ret.open().type().serial()) {
                ret = n;
            }
        }
        return Optional.ofNullable(ret);
    }

    @Override
    public List<ScopeNode> collect(Predicate<ScopeNode> filter) {
        List<ScopeNode> ret = new ArrayList<>();
        for (ScopeNode n = this; !n.isRoot(); n = n.parent()) {
            if (filter.test(n)) {
                ret.add(0, n);
            }
        }
        return ret;
    }

    /**
     * Close this scope with the specified token.
     * @param token the close token
     */
    void closeOn(Token token) {
        this.close = token;
    }


    /**
     * Close open scopes for all ancestors matching the given predicate.
     * @param token the close token
     * @param predicate the given predicate
     */
    void closeAncestorOn(Token token, Predicate<Token> predicate) {
        if (isRoot()) {
            return;
        }
        if (isOpen() && predicate.test(open)) {
            closeOn(token);
        }
        parent.closeAncestorOn(token, predicate);
    }


    /**
     * Get the children node.
     * @return the children node
     */
    List<ScopeTreeNode> children() {
        return children;
    }


    /**
     * Remove the scope after the specified offset.
     * @param offset the specified offset
     */
    void removeAfter(long offset) {
        children().removeIf(node -> node.open().position() >= offset);
        children().forEach(node -> {
            if (node.isClosed() && node.close().position() >= offset) {
                node.closeOn(null);
            }
            node.removeAfter(offset);
        });
    }


    /**
     * Get the scope node at the specified offset.
     * @param offset the specified offset
     * @return the scope node at the specified offset
     */
    ScopeTreeNode at(long offset) {
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


    private boolean within(long offset) {
        return (isOpen() && open.position() <= offset) ||
            (isClosed() && open.position() <= offset && offset < close.position());
    }

    @Override
    public String toString() {
        String indent = "  ".repeat(level());
        return indent + "open:" + open + ", close:" + close + "\n" +
               indent + "children:" + children.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }

}
