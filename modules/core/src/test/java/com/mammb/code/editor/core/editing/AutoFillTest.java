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
package com.mammb.code.editor.core.editing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link AutoFill}.
 * @author Naotsugu Kobayashi
 */
class AutoFillTest {

    @Test
    void apply() {
        AutoFill autoFill = new AutoFill();
        assertEquals("1", autoFill.apply("1"));
        assertEquals("2", autoFill.apply("2"));
        assertEquals("3", autoFill.apply(""));
        assertEquals("4", autoFill.apply(""));
    }

    @Test
    void testApplyWithEmptyInitial() {
        AutoFill autoFill = new AutoFill();
        assertEquals("1", autoFill.apply(""));
        assertEquals("2", autoFill.apply(""));
        assertEquals("3", autoFill.apply(""));
        assertEquals("4", autoFill.apply(""));
    }

    @Test
    void testApplyWithIncrement() {
        AutoFill autoFill = new AutoFill();
        assertEquals("2", autoFill.apply("2"));
        assertEquals("4", autoFill.apply("4"));
        assertEquals("6", autoFill.apply(""));
        assertEquals("8", autoFill.apply(""));
    }

    @Test
    void testApplyWithIncrement2() {
        AutoFill autoFill = new AutoFill();
        assertEquals("10", autoFill.apply("10"));
        assertEquals("11", autoFill.apply(""));
        assertEquals("12", autoFill.apply(""));
        assertEquals("13", autoFill.apply(""));
    }

    @Test
    void testApplyWithIncrement3() {
        AutoFill autoFill = new AutoFill();
        assertEquals("002", autoFill.apply("002"));
        assertEquals("003", autoFill.apply(""));
        assertEquals("004", autoFill.apply(""));
        assertEquals("005", autoFill.apply(""));
    }

}
