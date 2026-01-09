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

import java.nio.file.Path;

/**
 * The OS.
 * @author Naotsugu Kobayashi
 */
public enum OS {
    MAC, WINDOWS, LINUX, OTHER;

    /**
     * Represents the currently detected operating system.
     * This value is determined at runtime by inspecting the system's
     * "os.name" property and mapping it to one of the predefined
     * constants in the {@code OS} enum.
     */
    public static final OS CURRENT = current();

    /**
     * Retrieves the name of the current operating system in lowercase.
     * @return the lowercase name of the current operating system
     */
    public static String currentName() {
        return current().name().toLowerCase();
    }

    /**
     * Get the configuration directory for a platform.
     * @return the configuration directory
     */
    public static Path confRoot() {
        Path home = Path.of(System.getProperty("user.home"));
        return switch (CURRENT) {
            case WINDOWS -> home.resolve("AppData", "Local");
            case MAC -> home.resolve("Library", "Application Support");
            case LINUX -> home.resolve(".config");
            default -> home;
        };
    }

    /**
     * Get the platform.
     * @return the platform
     */
    private static OS current() {
        var osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) return WINDOWS;
        if (osName.contains("mac")) return MAC;
        if (osName.contains("linux")) return LINUX;
        return OTHER;
    }

}
