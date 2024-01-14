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
package com.mammb.code.editor.syntax.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link ColorPalette}.
 * @author Naotsugu Kobayashi
 */
class ColorPaletteTest {

    @Test
    void getLuminance() {
        assertEquals(  0, (int) new ColorPalette("000000").getLuminance()); // black
        assertEquals(  8, (int) new ColorPalette("191919").getLuminance());
        assertEquals( 16, (int) new ColorPalette("292929").getLuminance());
        assertEquals( 85, (int) new ColorPalette("C9D7E6").getLuminance());
        assertEquals( 94, (int) new ColorPalette("F0F0F0").getLuminance());
        assertEquals(100, (int) new ColorPalette("FFFFFF").getLuminance()); // white
    }

}
