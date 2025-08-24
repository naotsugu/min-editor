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

import java.util.Objects;

/**
 * The Theme.
 * @author Naotsugu Kobayashi
 */
public interface Theme {

    /** The dark theme. */
    Theme dark = new ThemeRecord(
        new Rgba("#292929"),   // baseColor
        new Rgba("#CEDFF2"),   // fgColor
        new Rgba("#214283"),   // paleHighlightColor
        new Rgba("#FDE047"),   // cautionColor
        new Rgba("#80808088"), // faintColor
        new Rgba("#303030")    // uiBaseColor
    );

    /** The light theme. */
    Theme light = new ThemeRecord(
        new Rgba("#ffffff"),   // baseColor
        new Rgba("#000000"),   // fgColor
        new Rgba("#a9d7ff"),   // paleHighlightColor
        new Rgba("#fbbf24"),   // cautionColor
        new Rgba("#80808088"), // faintColor
        new Rgba("#f0f0f0")    // uiBaseColor
    );

    /** The current theme. */
    Theme current = Objects.equals("light", System.getProperty("core.theme", "dark")) ? light : dark;

    /**
     * Get the base color.
     * @return the base color
     */
    Rgba baseColor();

    /**
     * Get the foreground color.
     * @return the foreground color
     */
    Rgba fgColor();

    /**
     * Get the pale highlight color.
     * @return the pale highlight color
     */
    Rgba paleHighlightColor();

    /**
     * Get the caution color.
     * Typically, highlighting the searched string color.
     * @return the caution color
     */
    Rgba cautionColor();

    /**
     * Get the faint color.
     * @return the faint color
     */
    Rgba faintColor();

    /**
     * Get the ui base color.
     * @return the ui base color
     */
    Rgba uiBaseColor();

    record ThemeRecord(
        Rgba baseColor,
        Rgba fgColor,
        Rgba paleHighlightColor,
        Rgba cautionColor,
        Rgba faintColor,
        Rgba uiBaseColor
    ) implements Theme { }

}
