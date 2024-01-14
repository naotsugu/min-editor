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
package com.mammb.code.editor.model.find.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link FindSpecImpl}.
 * @author Naotsugu Kobayashi
 */
class FindSpecImplTest {

    @Test void match() {

        var target = new FindSpecImpl("abc", true, true);

        assertEquals(0, target.match("abx").length);

        var actual = target.match("abc");
        assertEquals(1, actual.length);
        assertEquals(0, actual[0].start());
        assertEquals(3, actual[0].length());

        actual = target.match("abcabc");
        assertEquals(2, actual.length);
        assertEquals(0, actual[0].start());
        assertEquals(3, actual[0].length());
        assertEquals(3, actual[1].start());
        assertEquals(3, actual[1].length());

        actual = target.match("abcabcxxabcxxxabcxxxab");
        assertEquals(4, actual.length);
        assertEquals(0, actual[0].start());
        assertEquals(3, actual[0].length());
        assertEquals(3, actual[1].start());
        assertEquals(3, actual[1].length());
        assertEquals(8, actual[2].start());
        assertEquals(3, actual[2].length());
        assertEquals(14, actual[3].start());
        assertEquals(3, actual[3].length());
    }

}
