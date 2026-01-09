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

/**
 * The OS.
 * @author Naotsugu Kobayashi
 */
public class OS {

    /** The OS name. */
    static final Name name = current();

    /**
     * Determines if the current operating system is macOS.
     * @return {@code true} if the current operating system is macOS, {@code false} otherwise
     */
    public static boolean isMac() {
        return name == Name.MAC;
    }

    /**
     * Determines if the current operating system is Linux.
     * @return {@code true} if the current operating system is Linux, {@code false} otherwise
     */
    public static boolean isLinux() {
        return name == Name.LINUX;
    }

    /**
     * Determines if the current operating system is Windows.
     * @return {@code true} if the current operating system is Windows, {@code false} otherwise
     */
    public static boolean isWindows() {
        return name == Name.WINDOWS;
    }

    /**
     * Get the platform os name.
     * @return the platform os name
     */
    private static Name current() {
        var osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) return Name.WINDOWS;
        if (osName.contains("mac")) return Name.MAC;
        if (osName.contains("linux")) return Name.LINUX;
        return Name.UNKNOWN;
    }

    /**
     * Get the platform arch.
     * @return the platform arch
     */
    private static Arch arch() {
        String osArch = System.getProperty("os.arch").toLowerCase();
        if (osArch.contains("amd64") || osArch.contains("x86_64")) return Arch.X64;
        if (osArch.contains("aarch64") || osArch.contains("arm64")) return Arch.ARM64;
        if (osArch.contains("x32") || osArch.contains("i386") || osArch.contains("i686")) return Arch.X32;
        if (osArch.contains("arm")) return Arch.ARM32;
        return Arch.UNKNOWN;
    }

    enum Name { MAC, WINDOWS, LINUX, UNKNOWN }
    enum Arch { X32, X64, ARM32, ARM64, UNKNOWN }

}
