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
package com.mammb.code.editor.model.text.impl;

import com.mammb.code.editor.model.slice.impl.StringText;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link StringText}.
 * @author Naotsugu Kobayashi
 */
class StringTextTest {

    @Test void at() {
        var text = new StringText("ab😀c\nde😀f\ngh😀i");
        assertNull(text.at(-1));
        assertEquals("ab😀c\n", text.at(0));
        assertEquals("de😀f\n", text.at(5));
        assertEquals("gh😀i", text.at(10));
        assertEquals("i", text.at(13));
        assertNull(text.at(14));
    }

    @Test
    void before() {
        var text = new StringText("ab😀c\nde😀f\ngh😀i");
        assertNull(text.before(0));
        assertEquals("ab😀c\n", text.before(5));
        assertEquals("de😀f\n", text.before(10));
        assertEquals("gh😀i", text.before(14));
        assertNull(text.before(15));
    }
}
