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

import com.mammb.code.editor.syntax.base.Scope;
import com.mammb.code.editor.syntax.base.ScopeTree;
import com.mammb.code.editor.syntax.base.Token;
import com.mammb.code.editor.syntax.base.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Text for {@link ScopeTreeImpl}.
 * @author Naotsugu Kobayashi
 */
class ScopeTreeImplTest {

    public interface TestToken extends com.mammb.code.editor.syntax.base.TokenType {
        TokenType BLOCK1 = TokenType.build();
        TokenType BLOCK2 = TokenType.build();
    }

    @Test
    void push() {

        ScopeTree scope = ScopeTreeImpl.of();
        var node = scope.current();
        assertEquals(true, node.isOpen());
        assertEquals(TestToken.EMPTY, node.open().type());

        // a {
        scope.push(Token.of(TestToken.BLOCK1, Scope.BLOCK_START, 2, 1));

        node = scope.current();
        assertEquals(true, node.isOpen());
        assertEquals(TestToken.BLOCK1, node.open().type());

        node = scope.at(1);
        assertEquals(TestToken.EMPTY, node.open().type());


        // a {
        //    b {
        scope.push(Token.of(TestToken.BLOCK2, Scope.BLOCK_START, 9, 1));

        node = scope.current();
        assertEquals(true, node.isOpen());
        assertEquals(TestToken.BLOCK2, node.open().type());

        node = scope.at(7);
        assertEquals(true, node.isOpen());
        assertEquals(TestToken.BLOCK1, node.open().type());


        // a {
        //    b {
        //    }
        scope.push(Token.of(TestToken.BLOCK2, Scope.BLOCK_END, 14, 1));

        node = scope.current();
        assertEquals(true, node.isOpen());
        assertEquals(TestToken.BLOCK1, node.open().type());

        node = scope.at(13);
        assertEquals(true, node.isClosed());
        assertEquals(TestToken.BLOCK2, node.open().type());

    }

}
