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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of {@link Anchor}.
 * @author Naotsugu Kobayashi
 */
class AnchorTest {

    @Test void nearest() {
        var target = new Anchor(2, 1000);
        target.push(1000, 1001, 1002);
        target.push(2000, 2001, 2002);

        var point = target.nearest(0);
        assertEquals(0, point.anchorPoint());
        assertEquals(0, point.value1());
        assertEquals(0, point.value2());

        point = target.nearest(499);
        assertEquals(0, point.anchorPoint());
        assertEquals(0, point.value1());
        assertEquals(0, point.value2());

        point = target.nearest(500);
        assertEquals(1000, point.anchorPoint());
        assertEquals(1001, point.value1());
        assertEquals(1002, point.value2());

        point = target.nearest(1000);
        assertEquals(1000, point.anchorPoint());
        assertEquals(1001, point.value1());
        assertEquals(1002, point.value2());

        point = target.nearest(1499);
        assertEquals(1000, point.anchorPoint());
        assertEquals(1001, point.value1());
        assertEquals(1002, point.value2());

        point = target.nearest(1500);
        assertEquals(2000, point.anchorPoint());
        assertEquals(2001, point.value1());
        assertEquals(2002, point.value2());

        point = target.nearest(2000);
        assertEquals(2000, point.anchorPoint());
        assertEquals(2001, point.value1());
        assertEquals(2002, point.value2());

        point = target.nearest(3000);
        assertEquals(2000, point.anchorPoint());
        assertEquals(2001, point.value1());
        assertEquals(2002, point.value2());
    }

}
