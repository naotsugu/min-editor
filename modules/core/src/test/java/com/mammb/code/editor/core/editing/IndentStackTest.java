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
package com.mammb.code.editor.core.editing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The of {@link StackIndents}.
 * @author Naotsugu Kobayashi
 */
class IndentStackTest {

    @Test
    void indentify() {
        var text = "SELECT column1, column2 FROM table WHERE ((column1 = 'value') AND (column2 = 'value')) GROUP BY column1;";
        var formatted = StackIndents.indentify(text, '(', ')', "\n");
        assertEquals("""
            SELECT column1, column2 FROM table WHERE (
                (
                    column1 = 'value'
                ) AND (
                    column2 = 'value'
                )
            ) GROUP BY column1;""", formatted.toString());
    }
}
