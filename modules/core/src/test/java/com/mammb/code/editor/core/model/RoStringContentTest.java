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
package com.mammb.code.editor.core.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link RoStringContent}.
 * @author Naotsugu Kobayashi
 */
class RoStringContentTest {

    @TempDir
    private static Path dir;

    @Test
    void stringListMono() {
        String text = "abc";
        var target = content(text, 2);
        assertEquals(1, target.rows());
        assertEquals("abc", target.getText(0));
    }

    @Test
    void stringListSingle() {
        String text = "abc\n";
        var target = content(text, 2);
        assertEquals(2, target.rows());
        assertEquals("abc\n", target.getText(0));
        assertEquals("", target.getText(1));
    }

    @Test
    void stringListMulti() {
        String text = "abc\ndef😊";
        var target = content(text, 2);
        assertEquals(2, target.rows());
        assertEquals("abc\n", target.getText(0));
        assertEquals("def😊", target.getText(1));
    }

    @Test
    void stringListLimit() {
        String text = "abc\ndef\nghi";
        var target = content(text, 2);
        assertEquals(3, target.rows());
        assertEquals("abc\n", target.getText(0));
        assertEquals("def\n", target.getText(1));
        assertEquals("", target.getText(2));
    }

    @Test
    void stringListZero() {
        String text = "";
        var target = content(text, 2);
        assertEquals(1, target.rows());
        assertEquals("", target.getText(0));
    }

    private RoStringContent content(String text, int rowLimit) {
        Path path = dir.resolve(UUID.randomUUID() + ".txt");
        try (var writer = Files.newBufferedWriter(path)) {
            writer.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new RoStringContent(path, rowLimit);
    }

}
