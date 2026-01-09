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
package com.mammb.code.editor.platform;

import org.junit.jupiter.api.Test;

import static com.mammb.code.editor.platform.AppVersion.Version;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The unit test of the {@code Version} class.
 * @author Naotsugu Kobayashi
 */
class VersionTest {

    @Test
    void majorAndMinor() {
        assertEquals("0.5", AppVersion.Version.of("0.5.0").majorAndMinor());
    }

    @Test
    void isNewerThan() {
        assertTrue(Version.of("2.0.0").isNewerThan(Version.of("1.0.0")));
        assertTrue(Version.of("2.1.1").isNewerThan(Version.of("1.2.2")));
        assertFalse(Version.of("1.0.0").isNewerThan(Version.of("2.0.0")));

        assertTrue(Version.of("1.2.0").isNewerThan(Version.of("1.1.9")));
        assertFalse(Version.of("1.1.9").isNewerThan(Version.of("1.2.0")));

        assertTrue(Version.of("1.2.3").isNewerThan(Version.of("1.2.2")));
        assertFalse(Version.of("1.2.3").isNewerThan(Version.of("1.2.3")));
        assertFalse(Version.of("1.2.3").isNewerThan(Version.of("1.2.4")));
    }

}
