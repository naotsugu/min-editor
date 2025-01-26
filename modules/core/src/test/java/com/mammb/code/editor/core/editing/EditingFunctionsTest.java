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
 * The of {@link EditingFunctions}.
 * @author Naotsugu Kobayashi
 */
class EditingFunctionsTest {

    @Test
    void decToHex() {
        assertEquals("0x0", EditingFunctions.decToHex.apply("0"));
        assertEquals("0xf", EditingFunctions.decToHex.apply("15"));
        assertEquals("0x10", EditingFunctions.decToHex.apply("16"));
        assertEquals("0x64", EditingFunctions.decToHex.apply("100"));
        assertEquals("0x3e8", EditingFunctions.decToHex.apply("1000"));
    }

    @Test
    void decToBin() {
        assertEquals("0000", EditingFunctions.decToBin.apply("0"));
        assertEquals("1111", EditingFunctions.decToBin.apply("15"));
        assertEquals("0001 0000", EditingFunctions.decToBin.apply("16"));
        assertEquals("0110 0100", EditingFunctions.decToBin.apply("100"));
        assertEquals("0011 1110 1000", EditingFunctions.decToBin.apply("1000"));
    }

    @Test
    void hexToBin() {
        assertEquals("0000", EditingFunctions.hexToBin.apply("0x0"));
        assertEquals("1111", EditingFunctions.hexToBin.apply("0xf"));
        assertEquals("0001 0000", EditingFunctions.hexToBin.apply("0x10"));
        assertEquals("0110 0100", EditingFunctions.hexToBin.apply("0x64"));
        assertEquals("0011 1110 1000", EditingFunctions.hexToBin.apply("0x3e8"));
    }

    @Test
    void hexToDec() {
        assertEquals("0", EditingFunctions.hexToDec.apply("0x0"));
        assertEquals("15", EditingFunctions.hexToDec.apply("0xf"));
        assertEquals("16", EditingFunctions.hexToDec.apply("0x10"));
        assertEquals("100", EditingFunctions.hexToDec.apply("0x64"));
        assertEquals("1000", EditingFunctions.hexToDec.apply("0x3e8"));
    }

    @Test
    void binToHex() {
        assertEquals("0x0", EditingFunctions.binToHex.apply("0000"));
        assertEquals("0xf", EditingFunctions.binToHex.apply("1111"));
        assertEquals("0x10", EditingFunctions.binToHex.apply("0001 0000"));
        assertEquals("0x64", EditingFunctions.binToHex.apply("0110 0100"));
        assertEquals("0x3e8", EditingFunctions.binToHex.apply("0011 1110 1000"));
    }

    @Test
    void binToDec() {
        assertEquals("0", EditingFunctions.binToDec.apply("0000"));
        assertEquals("15", EditingFunctions.binToDec.apply("1111"));
        assertEquals("16", EditingFunctions.binToDec.apply("0001 0000"));
        assertEquals("100", EditingFunctions.binToDec.apply("0110 0100"));
        assertEquals("1000", EditingFunctions.binToDec.apply("0011 1110 1000"));
    }

}
