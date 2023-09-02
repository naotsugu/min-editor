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
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;
import com.mammb.code.editor.syntax.base.TokenType;

/**
 * ScopeTreeImpl.
 * @author Naotsugu Kobayashi
 */
public class ScopeTreeImpl implements ScopeTree {

    /** logger. */
    private static final System.Logger log = System.getLogger(ScopeTreeImpl.class.getName());

    /** The root node. */
    private ScopeTreeNode root;

    /** The high watermark. */
    private int hwm = 0;


    /**
     * Constructor.
     */
    private ScopeTreeImpl(ScopeTreeNode root) {
        this.root = root;
    }


    /**
     * Create a new ScopeTree.
     * @param context the name of context
     * @return a new ScopeTree
     */
    public static ScopeTree of(String context) {
        return new ScopeTreeImpl(ScopeTreeNode.root(context));
    }


    @Override
    public void push(Token token) {

        if (token.position() <= hwm) {
            root.removeAfter(token.position());
        }
        hwm = token.position();

        if (token.scope().isNeutral()) {
            return;
        }

        final ScopeTreeNode node = root.at(hwm);
        if (node == null) {
            if (!token.scope().isEnd()) {
                root.children().add(ScopeTreeNode.startOf(root, token));
            }
            return;
        }

        if (token.type() == TokenType.EOL) {
            // if current token is end of line, close all inline scopes
            node.closeAncestorOn(token, t -> t.scope().isInline());

        } else if (node.isOpen() && node.open().type() == token.type() &&
                node.open().scope().isPair(token.scope())) {
            // if it is an open scope of the same type, it closes the scope
            node.closeOn(token);

        } else if (!token.scope().isEnd()) {
            // add scope
            node.children().add(ScopeTreeNode.startOf(node, token));
        }

    }


    @Override
    public ScopeNode current() {
        return at(hwm);
    }


    @Override
    public ScopeNode at(int offset) {
        final ScopeNode node = root.at(offset);
        return (node == null) ? root : node;
    }

    public String toString() {
        return "hwm:" + hwm + ", " + root.toString();
    }

}
