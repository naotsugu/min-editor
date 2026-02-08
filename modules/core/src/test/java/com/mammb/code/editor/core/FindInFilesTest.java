/*
 * Copyright 2023-2026 the original author or authors.
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
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link FindInFiles} class.
 * @author Naotsugu Kobayashi
 */
class FindInFilesTest {

    @Test
    void run(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve("file1.txt");
        Files.writeString(file1, "hello world\nhello java\n");
        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file2, "goodbye world\n");

        List<FindInFiles.Found> results = new ArrayList<>();
        Future<?> future = FindInFiles.run(tempDir, "hello", results::addAll);
        future.get();

        assertEquals(2, results.size());
        assertEquals("hello world", results.get(0).snippet());
        assertEquals(1, results.get(0).line());
        assertEquals("hello java", results.get(1).snippet());
        assertEquals(2, results.get(1).line());
    }

    @Test
    void runWithInterrupt(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve("file1.txt");
        Files.writeString(file1, "hello world\nhello java\n");
        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file2, "goodbye world\n");

        List<FindInFiles.Found> results = new ArrayList<>();
        Future<?> future = FindInFiles.run(tempDir, "hello", results::addAll);

        future.cancel(true);

        assertTrue(future.isCancelled());
    }

}
