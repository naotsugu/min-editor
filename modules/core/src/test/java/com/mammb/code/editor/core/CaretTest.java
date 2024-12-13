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
package com.mammb.code.editor.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link Caret).
 * @author Naotsugu Kobayashi
 */
class CaretTest {

    @Test
    void direction() {
        var caret = Caret.of();
        assertEquals(0, caret.direction());

        caret.at(2, 4);
        assertEquals(0, caret.direction());

        caret.mark();
        caret.at(2, 5);
        assertEquals(1, caret.direction());

        caret.at(3, 0);
        assertEquals(1, caret.direction());

        caret.at(2, 4);
        assertEquals(0, caret.direction());

        caret.at(2, 3);
        assertEquals(-1, caret.direction());

        caret.at(1, 6);
        assertEquals(-1, caret.direction());

        caret.clearMark();
        assertEquals(0, caret.direction());
    }

    @Test
    void marge() {
        var caret1 = Caret.of();
        var caret2 = Caret.of();

        assertEquals(true, caret1.marge(caret2));
        assertEquals(true, caret2.marge(caret1));

        //        | 0 | 1 | 2 |
        // caret1 |-->|
        // caret2 |
        caret1.markTo(Point.of(0, 0), Point.of(0, 1));
        assertEquals(true, caret1.marge(caret2));
        assertEquals(Point.of(0, 0), caret1.markedPoint());
        assertEquals(Point.of(0, 1), caret1.point());

        //        | 0 | 1 | 2 |
        // caret1 |-->|
        // caret2         |
        caret1.markTo(Point.of(0, 0), Point.of(0, 1));
        caret2.at(Point.of(0, 2));
        assertEquals(false, caret1.marge(caret2));

        //        | 0 | 1 | 2 |
        // caret1 |-->|
        // caret2         |-->|
        caret1.markTo(Point.of(0, 0), Point.of(0, 1));
        caret2.markTo(Point.of(0, 2), Point.of(0, 3));
        assertEquals(false, caret1.marge(caret2));

        //        | 0 | 1 | 2 |
        // caret1 |------>|
        // caret2     |------>|
        caret1.markTo(Point.of(0, 0), Point.of(0, 2));
        caret2.markTo(Point.of(0, 1), Point.of(0, 3));
        assertEquals(true, caret1.marge(caret2));
        assertEquals(Point.of(0, 0), caret1.markedPoint());
        assertEquals(Point.of(0, 3), caret1.point());

    }

}
