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
package com.mammb.code.javafx.text;

import javafx.scene.text.Font;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test for {@link Layout}.
 * @author Naotsugu Kobayashi
 */
class LayoutTest {

    @EnabledOnOs(OS.MAC)
    @Test void lines() {

        var str = IntStream.rangeClosed(0, 30)
            .mapToObj(i -> i + " ")
            .collect(Collectors.joining());

        var layout = Layout.of(Font.font("System Regular", 13), 100);
        String[] lines = layout.lines(str);

        assertEquals("0 1 2 3 4 5 6 7 8 9 ", lines[0]);
        assertEquals("10 11 12 13 14 15 ", lines[1]);
        assertEquals("16 17 18 19 20 21 ", lines[2]);
        assertEquals("22 23 24 25 26 ", lines[3]);
        assertEquals("27 28 29 30 ", lines[4]);
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test void linesWin() {

        var str = IntStream.rangeClosed(0, 30)
            .mapToObj(i -> i + " ")
            .collect(Collectors.joining());

        var layout = Layout.of(Font.font("System Regular", 13), 100);
        String[] lines = layout.lines(str);

        assertEquals("0 1 2 3 4 5 6 7 8 ", lines[0]);
        assertEquals("9 10 11 12 13 14 ", lines[1]);
        assertEquals("15 16 17 18 19 ", lines[2]);
        assertEquals("20 21 22 23 24 ", lines[3]);
        assertEquals("25 26 27 28 29 ", lines[4]);
        assertEquals("30 ", lines[5]);
    }

}
