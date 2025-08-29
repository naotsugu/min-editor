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
        assertEquals("00000000: 74 68 69 73 20 69 73 20  61 20 62 69 6E 61 72 79  | this is a binary", list.get(0).trim());
        assertEquals("00000010: 20 76 69 65 77 20 74 65  73 74 0A                 |  view test.", list.get(1).trim());
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

}
