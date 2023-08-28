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

    private ScopeTreeNode parent;

    private final List<ScopeTreeNode> children = new ArrayList<>();

    private Token open;

    private Token close;


    private ScopeTreeNode(ScopeTreeNode parent, Token open) {
        this.parent = parent;
        this.open = open;
    }

    public static ScopeTreeNode root() {
        return new ScopeTreeNode(null, Token.empty(null));
    }

    public static ScopeTreeNode of(ScopeTreeNode parent, Token open) {
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

    public void closeOn(Token token) {
        this.close = token;
    }

    public List<ScopeTreeNode> children() {
        return children;
    }

    public void removeAfter(int offset) {
        children().removeIf(node -> node.open().position() >= offset);
        children().forEach(node -> node.removeAfter(offset));
    }

    public ScopeTreeNode at(int offset) {
        if (open.position() < offset) {
            return null;
        } else if (open.position() >= offset && !children.isEmpty()) {
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

    public boolean within(int offset) {
        return (isOpen() && open.position() <= offset) ||
            (isClosed() && open.position() <= offset && offset < close.position());
    }

}
