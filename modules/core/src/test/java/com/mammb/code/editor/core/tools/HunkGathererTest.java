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
package com.mammb.code.editor.core.tools;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link HunkGatherer}.
 * @author Naotsugu Kobayashi
 */
class HunkGathererTest {

    @Test
    void test() {
        assertEquals(
            List.of(1, 5),
            Stream.of(1, 5).gather(HunkGatherer.of(0, 10)).toList());

        assertEquals(
            List.of(-1, 0, 1, 2, -1, 4, 5, 6),
            Stream.of(1, 5).gather(HunkGatherer.of(1, 10, -1)).toList());

        assertEquals(
            List.of(-1, 0, 1, 2, 3, 4, 5, 6, 7),
            Stream.of(1, 5).gather(HunkGatherer.of(2, 10, -1)).toList());
    }

}
