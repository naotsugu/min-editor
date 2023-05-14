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
package com.mammb.code.editor2.model.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link LineEnding}.
 * @author Naotsugu Kobayashi
 */
class LineEndingTest {

    @Test void unifyLf() {
        var lf = LineEnding.LF;
        assertEquals("", lf.unify(""));
        assertEquals("a", lf.unify("a"));
        assertEquals("ab", lf.unify("ab"));
        assertEquals("\n", lf.unify("\n"));
        assertEquals("\n", lf.unify("\r\n"));
        assertEquals("\n\n", lf.unify("\r\n\r\n"));
        assertEquals("a\nb\nc", lf.unify("a\nb\r\nc"));
    }


    @Test void unifyCrLf() {
        var crlf = LineEnding.CRLF;
        assertEquals("", crlf.unify(""));
        assertEquals("a", crlf.unify("a"));
        assertEquals("ab", crlf.unify("ab"));
        assertEquals("\r\n", crlf.unify("\r\n"));
        assertEquals("\r\n", crlf.unify("\n"));
        assertEquals("\r\n\r\n", crlf.unify("\n\n"));
        assertEquals("a\r\nb\r\nc", crlf.unify("a\nb\r\nc"));
    }

}
