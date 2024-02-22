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
package com.mammb.code.editor.model.until.impl;

import com.mammb.code.editor.model.text.OffsetPoint;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link TraverseForward}.
 * @author Naotsugu Kobayashi
 */
class TraverseForwardTest {

    @Test void accept() {

        var target = TraverseForward.of(OffsetPoint.of(1, 2, 2));

        target.accept("a".getBytes(StandardCharsets.UTF_8));
        assertEquals(OffsetPoint.of(1, 3, 3), target.asOffsetPoint());

        target.accept("Ω".getBytes(StandardCharsets.UTF_8));
        assertEquals(OffsetPoint.of(1, 4, 4), target.asOffsetPoint());

        target.accept("あ".getBytes(StandardCharsets.UTF_8));
        assertEquals(OffsetPoint.of(1, 5, 5), target.asOffsetPoint());

        target.accept("😊".getBytes(StandardCharsets.UTF_8));
        assertEquals(OffsetPoint.of(1, 7, 6), target.asOffsetPoint());

        target.accept("\n".getBytes(StandardCharsets.UTF_8));
        assertEquals(OffsetPoint.of(2, 8, 7), target.asOffsetPoint());

    }
}
