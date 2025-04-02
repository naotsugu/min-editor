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
import com.mammb.code.editor.core.FontMetricsTestImpl;
import com.mammb.code.editor.core.Point;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The test of {@link WrapLayout#refreshAt(int, int)}.
 * @author Naotsugu Kobayashi
 */
public class WrapLayoutRefreshAtTest {

    @TempDir
    private static Path dir;

    @Test
    void refreshAt() {
        String text = "abc".repeat(10); // 30 char
        var target = new WrapLayout(content(text), new FontMetricsTestImpl());
        target.setCharsInLine(20);
        target.refreshAt(0, 10);
        List<SubRange> lines = target.lines();
        assertEquals(2, lines.size());
        assertEquals(new SubRange(0, 0, 2, 0, 20), lines.get(0));
        assertEquals(new SubRange(0, 1, 2, 20, 30), lines.get(1));
    }

    @Test
    void refreshAtAfterInlineEdit() {

        String line0 = "a".repeat(11) + "\n";
        String line1 = "b".repeat(12) + "\n";
        String line2 = "c".repeat(14);
        Content content = content(line0 + line1 + line2);
        var target = new WrapLayout(content, new FontMetricsTestImpl());
        target.setCharsInLine(10);
        assertEquals(6, target.lineSize());

        content.replace(Point.of(1, 0), Point.of(1, 10), "");
        assertEquals(line0, content.getText(0));
        assertEquals("bb\n", content.getText(1));
        assertEquals(line2, content.getText(2));

        target.refreshAt(1, 1);
        List<SubRange> lines = target.lines();
        assertEquals(5, lines.size());
        assertEquals(new SubRange(0, 0, 2, 0, 10), lines.get(0));
        assertEquals(new SubRange(0, 1, 2, 10, 12), lines.get(1));
        assertEquals(new SubRange(1, 0, 1, 0, 3), lines.get(2));
        assertEquals(new SubRange(2, 0, 2, 0, 10), lines.get(3));
        assertEquals(new SubRange(2, 1, 2, 10, 14), lines.get(4));
    }


    @Test
    void refreshAtAfterDeleteEdit() {

        String line0 = "a".repeat(11) + "\n";
        String line1 = "b".repeat(12) + "\n";
        String line2 = "c".repeat(14);
        Content content = content(line0 + line1 + line2);
        var target = new WrapLayout(content, new FontMetricsTestImpl());
        target.setCharsInLine(10);
        assertEquals(6, target.lineSize());

        content.replace(Point.of(1, 0), Point.of(1, line1.length()), "");
        assertEquals(line0, content.getText(0));
        assertEquals(line2, content.getText(1));

        target.refreshAt(1, 1);
        List<SubRange> lines = target.lines();
        assertEquals(4, lines.size());
        assertEquals(new SubRange(0, 0, 2, 0, 10), lines.get(0));
        assertEquals(new SubRange(0, 1, 2, 10, 12), lines.get(1));
        assertEquals(new SubRange(1, 0, 2, 0, 10), lines.get(2));
        assertEquals(new SubRange(1, 1, 2, 10, 14), lines.get(3));
    }

    @Test
    void refreshAtAfterInsertEdit() {

        String line0 = "a".repeat(11) + "\n";
        String line1 = "c".repeat(14);
        Content content = content(line0 + line1);
        var target = new WrapLayout(content, new FontMetricsTestImpl());
        target.setCharsInLine(10);
        assertEquals(4, target.lineSize());

        String line = "b".repeat(12) + "\n";
        content.insert(Point.of(1, 0), line);
        assertEquals(line0, content.getText(0));
        assertEquals(line, content.getText(1));
        assertEquals(line1, content.getText(2));

        target.refreshAt(1, 1);
        List<SubRange> lines = target.lines();
        assertEquals(6, lines.size());
        assertEquals(new SubRange(0, 0, 2, 0, 10), lines.get(0));
        assertEquals(new SubRange(0, 1, 2, 10, 12), lines.get(1));
        assertEquals(new SubRange(1, 0, 2, 0, 10), lines.get(2));
        assertEquals(new SubRange(1, 1, 2, 10, 13), lines.get(3));
        assertEquals(new SubRange(2, 0, 2, 0, 10), lines.get(4));
        assertEquals(new SubRange(2, 1, 2, 10, 14), lines.get(5));
    }

    @Test
    void refreshAtAfterReplaceEdit() {

        String line0 = "a".repeat(11) + "\n";
        String line1 = "b".repeat(12) + "\n";
        String line2 = "c".repeat(13) + "\n";
        String line3 = "d".repeat(15);

        Content content = content(line0 + line1 + line2 + line3);
        var target = new WrapLayout(content, new FontMetricsTestImpl());
        target.setCharsInLine(10);
        assertEquals(8, target.lineSize());

        content.replace(Point.of(1, 0), Point.of(2, 10), "xxx");

        target.refreshAt(1, 2);
        List<SubRange> lines = target.lines();
        assertEquals(5, lines.size());
        assertEquals(new SubRange(0, 0, 2, 0, 10), lines.get(0));
        assertEquals(new SubRange(0, 1, 2, 10, 12), lines.get(1));
        assertEquals(new SubRange(1, 0, 1, 0, 7), lines.get(2));
        assertEquals(new SubRange(2, 0, 2, 0, 10), lines.get(3));
        assertEquals(new SubRange(2, 1, 2, 10, 15), lines.get(4));

    }

    private Content content(String text) {
        Path path = dir.resolve(UUID.randomUUID() + ".txt");
        try (var writer = Files.newBufferedWriter(path)) {
            writer.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Content.of(path);
    }

}
