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

/**
 * ScopeTreeImpl.
 * @author Naotsugu Kobayashi
 */
public class ScopeTreeImpl implements ScopeTree {

    private ScopeTreeNode root;
    private int hwm = 0;

    public ScopeTreeImpl() {
        this.root = ScopeTreeNode.root();
    }

    @Override
    public void push(Token token) {
        if (token.position() >= hwm) {
            root.removeAfter(token.position());
        }
        hwm = token.position();
        ScopeTreeNode node = root.at(hwm);
        if (node.isOpen() && node.open().type() == token.type()) {
            node.closeOn(token);
        } else {
            node.children().add(ScopeTreeNode.of(node, token));
        }
    }

    @Override
    public ScopeNode current() {
        return root.at(hwm);
    }

    @Override
    public ScopeNode at(int offset) {
        return root.at(offset);
    }

}
