/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.core.syntax2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link Trie}.
 * @author Naotsugu Kobayashi
 */
class TrieTest {

    @Test
    void testMatch() {

        var trie = Trie.of("public private protected");

        assertEquals(true, trie.match("public"));
        assertEquals(true, trie.match("private"));
        assertEquals(true, trie.match("protected"));

        assertEquals(false, trie.match("apublic"));
        assertEquals(false, trie.match("pubalic"));
        assertEquals(false, trie.match("publica"));
        assertEquals(false, trie.match("ublic"));
        assertEquals(false, trie.match("pubic"));
        assertEquals(false,  trie.match("publi"));

        assertEquals(false, trie.match("aprotected"));
        assertEquals(false, trie.match("protaected"));
        assertEquals(false, trie.match("protecteda"));
        assertEquals(false, trie.match("rotected"));
        assertEquals(false, trie.match("proected"));
        assertEquals(false,  trie.match("protecte"));

        assertEquals(false, trie.match("publi"));
        assertEquals(false, trie.match("publ"));
        assertEquals(false, trie.match("pub"));
        assertEquals(false, trie.match("pu"));
        assertEquals(false, trie.match("p"));
        assertEquals(false, trie.match(""));

    }

    @Test
    void testPutAndRemove() {

        var trie = Trie.of("public protected void");

        assertEquals(true, trie.match("public"));
        assertEquals(true, trie.match("protected"));
        assertEquals(true, trie.match("void"));

        trie.remove("protected");
        assertEquals(true, trie.match("public"));
        assertEquals(false, trie.match("protected"));
        assertEquals(true, trie.match("void"));

        trie.remove("public");
        assertEquals(false, trie.match("public"));
        assertEquals(false, trie.match("protected"));
        assertEquals(true, trie.match("void"));

        trie.remove("void");
        assertEquals(false, trie.match("public"));
        assertEquals(false, trie.match("protected"));
        assertEquals(false, trie.match("void"));

        trie.put("public");
        trie.put("protected");
        trie.put("void");
        assertEquals(true, trie.match("public"));
        assertEquals(true, trie.match("protected"));
        assertEquals(true, trie.match("void"));

    }

    @Test
    void testPartialMatch() {

        var trie = Trie.of("public private protected");

        assertEquals(true, trie.startsWith("public"));
        assertEquals(true, trie.startsWith("private"));
        assertEquals(true, trie.startsWith("protected"));

        assertEquals(false, trie.startsWith("apublic"));
        assertEquals(false, trie.startsWith("pubalic"));
        assertEquals(false, trie.startsWith("publica"));
        assertEquals(false, trie.startsWith("ublic"));
        assertEquals(false, trie.startsWith("pubic"));
        assertEquals(true,  trie.startsWith("publi"));

        assertEquals(false, trie.startsWith("aprotected"));
        assertEquals(false, trie.startsWith("protaected"));
        assertEquals(false, trie.startsWith("protecteda"));
        assertEquals(false, trie.startsWith("rotected"));
        assertEquals(false, trie.startsWith("proected"));
        assertEquals(true,  trie.startsWith("protecte"));

        assertEquals(true, trie.startsWith("publi"));
        assertEquals(true, trie.startsWith("publ"));
        assertEquals(true, trie.startsWith("pub"));
        assertEquals(true, trie.startsWith("pu"));
        assertEquals(true, trie.startsWith("p"));
        assertEquals(true, trie.startsWith(""));

    }

}
