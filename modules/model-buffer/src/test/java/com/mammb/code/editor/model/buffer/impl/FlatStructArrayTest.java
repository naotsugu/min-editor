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
package com.mammb.code.editor.model.buffer.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link FlatStructArray}.
 * @author Naotsugu Kobayashi
 */
class FlatStructArrayTest {

    @Test void test() {
        var target = new FlatStructArray(3, 2);
        assertEquals(0, target.length());

        target.set(0, 10, 20, 30);
        assertArrayEquals(new long[] { 10, 20, 30 }, target.get(0));

        target.set(1, 11, 21, 31);
        assertArrayEquals(new long[] { 11, 21, 31 }, target.get(1));

        target.set(2, 12, 22, 32);
        assertArrayEquals(new long[] { 12, 22, 32 }, target.get(2));

        target.set(3, 13, 23, 33);
        assertArrayEquals(new long[] { 13, 23, 33 }, target.get(3));

        target.set(4, 14, 24, 34);
        assertArrayEquals(new long[] { 14, 24, 34 }, target.get(4));

        target.set(5, 15, 25, 35);
        assertArrayEquals(new long[] { 15, 25, 35 }, target.get(5));

        assertEquals(6, target.length());

    }

    @Test void binarySearch() {
        var target = new FlatStructArray(3);
        target.add(100, 200, 300); // 0
        target.add(110, 210, 310); // 1
        target.add(120, 220, 320); // 2
        target.add(130, 230, 330); // 3
        target.add(140, 240, 340); // 4
        target.add(150, 250, 350); // 5

        assertEquals(0, target.binarySearch(0, 90));
        assertEquals(0, target.binarySearch(0, 100));
        assertEquals(1, target.binarySearch(0, 105));

        assertEquals(1, target.binarySearch(0, 110));
        assertEquals(2, target.binarySearch(0, 120));
        assertEquals(3, target.binarySearch(0, 130));
        assertEquals(4, target.binarySearch(0, 140));

        assertEquals(5, target.binarySearch(0, 145));
        assertEquals(5, target.binarySearch(0, 150));
        assertEquals(5, target.binarySearch(0, 160));

    }

    @Test void plusValues() {

        var target = new FlatStructArray(3);
        target.add(10, 20, 30); // 0
        target.add(11, 21, 31); // 1
        target.add(12, 22, 32); // 2

        target.plusValues(1, 1, 2, 3);
        assertArrayEquals(new long[] { 10, 20, 30 }, target.get(0));
        assertArrayEquals(new long[] { 12, 23, 34 }, target.get(1));
        assertArrayEquals(new long[] { 13, 24, 35 }, target.get(2));

    }

}
