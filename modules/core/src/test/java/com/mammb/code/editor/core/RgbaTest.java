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
package com.mammb.code.editor.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link Rgba}.
 * @author Naotsugu Kobayashi
 */
class RgbaTest {

    @Test
    void testConstructors() {
        Rgba rgba1 = new Rgba(10, 20, 30, 40);
        assertEquals(10, rgba1.r());
        assertEquals(20, rgba1.g());
        assertEquals(30, rgba1.b());
        assertEquals(40, rgba1.a());

        Rgba rgba2 = new Rgba(10, 20, 30);
        assertEquals(10, rgba2.r());
        assertEquals(20, rgba2.g());
        assertEquals(30, rgba2.b());
        assertEquals(255, rgba2.a());

        Rgba rgba3 = new Rgba(new int[]{10, 20, 30, 40});
        assertEquals(10, rgba3.r());
        assertEquals(20, rgba3.g());
        assertEquals(30, rgba3.b());
        assertEquals(40, rgba3.a());

        Rgba rgba4 = new Rgba(new int[]{10, 20, 30});
        assertEquals(10, rgba4.r());
        assertEquals(20, rgba4.g());
        assertEquals(30, rgba4.b());
        assertEquals(255, rgba4.a());
    }

    @Test
    void testWebColor() {
        Rgba rgba1 = new Rgba(255, 255, 255);
        assertEquals("#ffffff", rgba1.webColor());

        Rgba rgba2 = new Rgba(0, 0, 0);
        assertEquals("#000000", rgba2.webColor());

        Rgba rgba3 = new Rgba(16, 32, 48);
        assertEquals("#102030", rgba3.webColor());

        Rgba rgba4 = new Rgba(16, 32, 48, 128);
        assertEquals("#10203080", rgba4.webColor());
    }

    @Test
    void testWebColorParsing() {
        // 3-digit
        Rgba rgba1 = new Rgba("#fff");
        assertEquals(255, rgba1.r());
        assertEquals(255, rgba1.g());
        assertEquals(255, rgba1.b());
        assertEquals(255, rgba1.a());

        // 4-digit
        Rgba rgba2 = new Rgba("#fff8");
        assertEquals(255, rgba2.r());
        assertEquals(255, rgba2.g());
        assertEquals(255, rgba2.b());
        assertEquals(136, rgba2.a()); // 0x88

        // 6-digit
        Rgba rgba3 = new Rgba("#112233");
        assertEquals(17, rgba3.r());
        assertEquals(34, rgba3.g());
        assertEquals(51, rgba3.b());
        assertEquals(255, rgba3.a());

        // 8-digit
        Rgba rgba4 = new Rgba("#11223344");
        assertEquals(17, rgba4.r());
        assertEquals(34, rgba4.g());
        assertEquals(51, rgba4.b());
        assertEquals(68, rgba4.a());
    }

    @Test
    void testClamping() {
        Rgba rgba = new Rgba(300, -10, 255, 0);
        assertEquals(255, rgba.r());
        assertEquals(0, rgba.g());
        assertEquals(255, rgba.b());
        assertEquals(0, rgba.a());
    }

}
