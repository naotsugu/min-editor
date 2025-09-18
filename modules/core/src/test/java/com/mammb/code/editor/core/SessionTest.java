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
package com.mammb.code.editor.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link Session}.
 * @author Naotsugu Kobayashi
 */
class SessionTest {

    @Test
    void stashOf(@TempDir Path tempDir) throws IOException {
        var path = tempDir.resolve("test.txt");
        Files.writeString(path, "ab\ncd");
        var session = Session.of(path);
        assertEquals(path, session.path());

        var string = session.asString();
        session = Session.valueOf(string);
        assertEquals(path, session.path());
        assertEquals(string, session.asString());
    }
}
