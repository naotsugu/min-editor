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
package com.mammb.code.editor.model.until.impl;

import com.mammb.code.editor.model.text.OffsetPoint;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link TraverseStack}.
 * @author Naotsugu Kobayashi
 */
class TraverseStackTest {

    @Test void test() {

        var target = TraverseStack.of(3);
        target.accept("a".getBytes(StandardCharsets.UTF_8));

        assertEquals(OffsetPoint.of(0, 1, 1), target.asOffsetPoint());
        assertEquals("a", target.asString());

        target.accept("b".getBytes(StandardCharsets.UTF_8));
        target.accept("c".getBytes(StandardCharsets.UTF_8));

        assertEquals(OffsetPoint.of(0, 3, 3), target.asOffsetPoint());
        assertEquals("abc", target.asString());

        target.accept("あ".getBytes(StandardCharsets.UTF_8));
        target.accept("\n".getBytes(StandardCharsets.UTF_8));
        target.accept("😀".getBytes(StandardCharsets.UTF_8));

        assertEquals(OffsetPoint.of(1, 7, 6), target.asOffsetPoint());
        assertEquals("abcあ\n😀", target.asString());

    }

}
