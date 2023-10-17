/*
 * Copyright 2019-2023 the original author or authors.
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
package com.mammb.code.editor.model.buffer.impl;

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The test of {@link FixedText}.
 * @author Naotsugu Kobayashi
 */
class FixedTextTest {

    @Test void fixedText(@TempDir Path tempDir) throws Exception {
        var path = tempDir.resolve("test.txt");
        Files.writeString(path, IntStream.rangeClosed('a', 'z')
            .mapToObj(i -> (char) i + "\n").collect(Collectors.joining()));
        var target = new FixedText(path, 10);

        //  a b c d e f g h i j k l m n o p q r s t u v w x y z
        // |                   |
        assertEquals(
            IntStream.rangeClosed('a', 'j').mapToObj(i -> (char) i + "\n").collect(Collectors.joining()),
            target.texts().stream().map(Textual::text).collect(Collectors.joining()));

        //  a b c d e f g h i j k l m n o p q r s t u v w x y z
        // |                   |
        //     |                   |
        assertEquals(
            "k\nl\n",
            target.next(2).stream().map(Textual::text).collect(Collectors.joining()));
        assertEquals(
            IntStream.rangeClosed('c', 'l').mapToObj(i -> (char) i + "\n").collect(Collectors.joining()),
            target.texts().stream().map(Textual::text).collect(Collectors.joining()));

        //  a b c d e f g h i j k l m n o p q r s t u v w x y z
        //     |                   |
        //                                 |                   |
        assertEquals(
            "m\nn\no\np\nq\nr\ns\nt\nu\nv\nw\nx\ny\nz\n",
            target.next(14).stream().map(Textual::text).collect(Collectors.joining()));
        assertEquals(
            IntStream.rangeClosed('q', 'z').mapToObj(i -> (char) i + "\n").collect(Collectors.joining()),
            target.texts().stream().map(Textual::text).collect(Collectors.joining()));

        //  a b c d e f g h i j k l m n o p q r s t u v w x y z
        //                                 |                   |
        //                                 |                   |
        assertEquals(
            "",
            target.next(3).stream().map(Textual::text).collect(Collectors.joining()));
        assertEquals(
            IntStream.rangeClosed('q', 'z').mapToObj(i -> (char) i + "\n").collect(Collectors.joining()),
            target.texts().stream().map(Textual::text).collect(Collectors.joining()));

        //  a b c d e f g h i j k l m n o p q r s t u v w x y z
        //                                 |                   |
        //                               |                   |
        assertEquals(
            "p\n",
            target.prev(1).stream().map(Textual::text).collect(Collectors.joining()));
        assertEquals(
            IntStream.rangeClosed('p', 'y').mapToObj(i -> (char) i + "\n").collect(Collectors.joining()),
            target.texts().stream().map(Textual::text).collect(Collectors.joining()));

        //  a b c d e f g h i j k l m n o p q r s t u v w x y z
        //                               |                   |
        //                           |                   |
        assertEquals(
            "n\no\n",
            target.prev(2).stream().map(Textual::text).collect(Collectors.joining()));
        assertEquals(
            IntStream.rangeClosed('n', 'w').mapToObj(i -> (char) i + "\n").collect(Collectors.joining()),
            target.texts().stream().map(Textual::text).collect(Collectors.joining()));


        //  a b c d e f g h i j k l m n o p q r s t u v w x y z
        //                           |                   |
        // |                   |
        assertEquals(
            "a\nb\nc\nd\ne\nf\ng\nh\ni\nj\nk\nl\nm\n",
            target.prev(16).stream().map(Textual::text).collect(Collectors.joining()));
        assertEquals(
            IntStream.rangeClosed('a', 'j').mapToObj(i -> (char) i + "\n").collect(Collectors.joining()),
            target.texts().stream().map(Textual::text).collect(Collectors.joining()));

        //  a b c d e f g h i j k l m n o p q r s t u v w x y z
        // |                   |
        assertEquals(
            "",
            target.prev(1).stream().map(Textual::text).collect(Collectors.joining()));
        assertEquals(
            IntStream.rangeClosed('a', 'j').mapToObj(i -> (char) i + "\n").collect(Collectors.joining()),
            target.texts().stream().map(Textual::text).collect(Collectors.joining()));

    }

    @Test void subText(@TempDir Path tempDir) throws Exception {
        var path = tempDir.resolve("test.txt");
        Files.writeString(path, IntStream.rangeClosed('a', 'z')
            .mapToObj(i -> (char) i + "\n").collect(Collectors.joining()));
        var target = new FixedText(path, 10);

        assertEquals("a", target.subText(OffsetPoint.zero, 1));
        assertEquals("a\n", target.subText(OffsetPoint.zero, 2));
        assertEquals("a\nb", target.subText(OffsetPoint.zero, 3));

        assertEquals("\n", target.subText(OffsetPoint.of(0, 1, 0), 1));
        assertEquals("\nb", target.subText(OffsetPoint.of(0, 1, 0), 2));
        assertEquals("\nb\n", target.subText(OffsetPoint.of(0, 1, 0), 3));

        assertEquals("z", target.subText(OffsetPoint.of(25, 25 * 2, 0), 1));
        assertEquals("z\n", target.subText(OffsetPoint.of(25, 25 * 2, 0), 2));
        assertEquals("z\n", target.subText(OffsetPoint.of(25, 25 * 2, 0), 3));
    }


    @Test void readLineLf() throws Exception {
        Reader r = new StringReader("a\n𠀋\nc");
        assertEquals("a\n", FixedText.readLine(r));
        assertEquals("𠀋\n", FixedText.readLine(r));
        assertEquals("c", FixedText.readLine(r));
        assertNull(FixedText.readLine(r));
    }

    @Test void readLineCrLf() throws Exception {
        Reader r = new StringReader("a\r\n𠀋\r\nc");
        assertEquals("a\r\n", FixedText.readLine(r));
        assertEquals("𠀋\r\n", FixedText.readLine(r));
        assertEquals("c", FixedText.readLine(r));
        assertNull(FixedText.readLine(r));
    }

    @Test void readLineCr() throws Exception {
        Reader r = new StringReader("a\r𠀋\rc");
        assertEquals("a\r", FixedText.readLine(r));
        assertEquals("𠀋\r", FixedText.readLine(r));
        assertEquals("c", FixedText.readLine(r));
        assertNull(FixedText.readLine(r));
    }

}
