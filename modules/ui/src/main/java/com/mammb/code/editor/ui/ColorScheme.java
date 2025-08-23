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
package com.mammb.code.editor.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * ColorScheme.
 * @author Naotsugu Kobayashi
 */
public enum ColorScheme {

    /** The light scheme. */
    LIGHT,

    /** The dark scheme. */
    DARK,
    ;

    /** logger. */
    private static final System.Logger log = System.getLogger(ColorScheme.class.getName());

    /**
     * Gets whether the color scheme is in dark mode.
     * @return {@code true}, if the color scheme is in dark mode
     */
    public boolean isDark() {
        return this == DARK;
    }

    /**
     * Gets whether the color scheme is in light mode.
     * @return {@code true}, if the color scheme is in light mode
     */
    public boolean isLight() {
        return this == LIGHT;
    }

    /**
     * Gets the color scheme according to the OS.
     * @return the color scheme
     */
    public static ColorScheme platform() {

        String os = System.getProperty("os.name", "generic").toLowerCase();
        try {
            if (os.contains("mac") || os.contains("darwin")) {
                ProcessBuilder pb = new ProcessBuilder("defaults", "read", "-g", "AppleInterfaceStyle");
                pb.redirectErrorStream(true);
                Process process = pb.start();
                boolean ret = process.waitFor(3, TimeUnit.SECONDS);
                if (!ret) {
                    process.destroy();
                    return DARK;
                }
                try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    return Objects.equals(reader.readLine(), "Dark") ? DARK : LIGHT;
                }
            } else if (os.contains("win")) {
                ProcessBuilder pb = new ProcessBuilder("reg", "query",
                    "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                    "/v", "AppsUseLightTheme");
                pb.redirectErrorStream(true);
                Process process = pb.start();
                boolean ret = process.waitFor(3, TimeUnit.SECONDS);
                if (!ret) {
                    process.destroy();
                    return DARK;
                }
                try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        int index = line.indexOf("REG_DWORD");
                        if (index >= 0) {
                            String val = line.substring(index + "REG_DWORD".length()).trim();
                            // 1 == Light Mode, 0 == Dark Mode
                            int mode = Integer.parseInt(val.substring("0x".length()), 16);
                            if (mode == 1) {
                                return LIGHT;
                            } else if (mode == 0) {
                                return DARK;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.log(System.Logger.Level.WARNING, "Failed to detect OS theme", e);
        }

        return DARK;
    }
}
