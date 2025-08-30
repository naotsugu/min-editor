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
package com.mammb.code.editor.core.tools;

import com.mammb.code.editor.core.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link BinaryView}.
 * @author Naotsugu Kobayashi
 */
class BinaryViewTest {

    @Test
    void run(@TempDir Path tempDir) {

        Path path = tempDir.resolve("test.dat");
        Files.write(path, List.of("this is a binary view test"), StandardCharsets.UTF_8, "\n");

        var source = new Source16(path);
        var iterable = BinaryView.run(source);
        var list = new ArrayList<String>();
        iterable.forEach(cs -> list.add(cs.toString()));

        assertEquals(2, list.size());
        assertEquals("00000000 | 74 68 69 73 20 69 73 20  61 20 62 69 6E 61 72 79 | this is a binary", list.get(0).trim());
        assertEquals("00000010 | 20 76 69 65 77 20 74 65  73 74 0A                |  view test.", list.get(1).trim());
    }

    @Test
    void testRunWithEmptySource(@TempDir Path tempDir) {
        Path path = tempDir.resolve("empty.dat");
        Files.write(path, List.of());

        var source = new Source16(path);
        var iterable = BinaryView.run(source);
        var list = new ArrayList<String>();
        iterable.forEach(cs -> list.add(cs.toString()));

        assertEquals(0, list.size());
    }

    @Test
    void testWith16Bytes(@TempDir Path tempDir) {
        Path path = tempDir.resolve("16bytes.dat");
        Files.write(path, "0123456789abcdef".getBytes());

        var source = new Source16(path);
        var iterable = BinaryView.run(source);
        var list = new ArrayList<String>();
        iterable.forEach(cs -> list.add(cs.toString()));

        assertEquals(1, list.size());
        assertEquals("00000000 | 30 31 32 33 34 35 36 37  38 39 61 62 63 64 65 66 | 0123456789abcdef", list.getFirst().trim());
    }

    @Test
    void testWith32Bytes(@TempDir Path tempDir) throws java.io.IOException {
        Path path = tempDir.resolve("32bytes.dat");
        Files.write(path, "0123456789abcdef0123456789abcdef".getBytes());

        var source = new Source16(path);
        var iterable = BinaryView.run(source);
        var list = new ArrayList<String>();
        iterable.forEach(cs -> list.add(cs.toString()));

        assertEquals(2, list.size());
        assertEquals("00000000 | 30 31 32 33 34 35 36 37  38 39 61 62 63 64 65 66 | 0123456789abcdef", list.get(0).trim());
        assertEquals("00000010 | 30 31 32 33 34 35 36 37  38 39 61 62 63 64 65 66 | 0123456789abcdef", list.get(1).trim());
    }

    @Test
    void testWithNonPrintableChars(@TempDir Path tempDir) {
        Path path = tempDir.resolve("nonprintable.dat");
        Files.write(path, new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 });

        var source = new Source16(path);
        var iterable = BinaryView.run(source);
        var list = new ArrayList<String>();
        iterable.forEach(cs -> list.add(cs.toString()));

        assertEquals(1, list.size());
        assertEquals("00000000 | 00 01 02 03 04 05 06 07  08 09 0A 0B 0C 0D 0E 0F | ................", list.getFirst().trim());
    }

    @Test
    void testWithAllByteValues(@TempDir Path tempDir) {
        Path path = tempDir.resolve("allbytes.dat");
        byte[] content = new byte[256];
        for (int i = 0; i < 256; i++) {
            content[i] = (byte) i;
        }
        Files.write(path, content);

        var source = new Source16(path);
        var iterable = BinaryView.run(source);
        var list = new ArrayList<String>();
        iterable.forEach(cs -> list.add(cs.toString()));

        assertEquals(16, list.size());
        // Just check a few lines for brevity
        assertEquals("00000000 | 00 01 02 03 04 05 06 07  08 09 0A 0B 0C 0D 0E 0F | ................", list.get(0).trim());
        assertEquals("00000010 | 10 11 12 13 14 15 16 17  18 19 1A 1B 1C 1D 1E 1F | ................", list.get(1).trim());
        assertEquals("00000020 | 20 21 22 23 24 25 26 27  28 29 2A 2B 2C 2D 2E 2F |  !\"#$%&'()*+,-./", list.get(2).trim());
        assertEquals("00000070 | 70 71 72 73 74 75 76 77  78 79 7A 7B 7C 7D 7E 7F | pqrstuvwxyz{|}~.", list.get(7).trim());
        assertEquals("000000F0 | F0 F1 F2 F3 F4 F5 F6 F7  F8 F9 FA FB FC FD FE FF | ................", list.get(15).trim());
    }

    @Test
    void save(@TempDir Path tempDir) {

        var source = Source.of(List.of(
            "00000000 | 74 68 69 73 20 69 73 20  61 20 62 69 6E 61 72 79 | this is a binary",
            "00000010 | 20 76 69 65 77 20 74 65  73 74 0A                |  view test."));

        Path path = BinaryView.save(tempDir.resolve("saved.dat"), source);
        assertArrayEquals("this is a binary view test\n".getBytes(), Files.readAllBytes(path));

    }

}
