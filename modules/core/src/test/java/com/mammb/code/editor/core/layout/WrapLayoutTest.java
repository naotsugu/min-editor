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
package com.mammb.code.editor.core.layout;

import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.FontMetricsTestImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link WrapLayout}.
 * @author Naotsugu Kobayashi
 */
class WrapLayoutTest {

    @TempDir
    private static Path dir;

    @Test
    void lineWidth() {

        String text = "abc".repeat(10);
        var target = new WrapLayout(content(text), new FontMetricsTestImpl());

        target.setLineWidth(0);
        List<SubRange> lines = target.lines();
        assertEquals(1, lines.size());
        assertEquals(new SubRange(0, 0, 1, 0, 30), lines.get(0));

        target.setLineWidth(20);
        lines = target.lines();
        assertEquals(2, lines.size());
        assertEquals(new SubRange(0, 0, 2, 0, 20), lines.get(0));
        assertEquals(new SubRange(0, 1, 2, 20, 30), lines.get(1));

        target.setLineWidth(10);
        lines = target.lines();
        assertEquals(3, lines.size());
        assertEquals(new SubRange(0, 0, 3, 0, 10), lines.get(0));
        assertEquals(new SubRange(0, 1, 3, 10, 20), lines.get(1));
        assertEquals(new SubRange(0, 2, 3, 20, 30), lines.get(2));
    }

    @Test
    void rowToFirstLine() {
        String text = "abc".repeat(10); // 30 char
        var target = new WrapLayout(content(text), new FontMetricsTestImpl());
        target.setLineWidth(20);
        // abcabcabcabcabcabcab  row:0  line:0
        // cabcabcabc            row:0  line:1
        assertEquals(0, target.rowToFirstLine(0));
        assertEquals(0, target.rowToFirstLine(1));
    }

    @Test
    void rowToFirstLineMulti() {
        String text = "abc".repeat(10) + "\n" + "123".repeat(10) + "\n";
        var target = new WrapLayout(content(text), new FontMetricsTestImpl());
        target.setLineWidth(20);

        assertEquals(0, target.rowToFirstLine(0));
        assertEquals(2, target.rowToFirstLine(1));
        assertEquals(4, target.rowToFirstLine(2));
    }

    @Test
    void rowToLastLine() {
        String text = "abc".repeat(10); // 30 char
        var target = new WrapLayout(content(text), new FontMetricsTestImpl());
        target.setLineWidth(20);
        // abcabcabcabcabcabcab  row:0  line:0
        // cabcabcabc            row:0  line:1
        assertEquals(1, target.rowToLastLine(0));
        assertEquals(1, target.rowToLastLine(1));
    }

    @Test
    void rowToLastLineMulti() {
        String text = "abc".repeat(10) + "\n" + "123".repeat(10) + "\n";
        var target = new WrapLayout(content(text), new FontMetricsTestImpl());
        target.setLineWidth(20);

        assertEquals(1, target.rowToLastLine(0));
        assertEquals(3, target.rowToLastLine(1));
        assertEquals(4, target.rowToLastLine(2));
    }

    private Content content(String text) {
        Path path = dir.resolve(UUID.randomUUID() + ".txt");
        try (var writer = Files.newBufferedWriter(path)) {
            writer.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Content.readOnlyPartOf(path);
    }

}
