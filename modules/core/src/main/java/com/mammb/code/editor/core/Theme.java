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
package com.mammb.code.editor.core;

/**
 * The Theme.
 * @author Naotsugu Kobayashi
 */
public interface Theme {

    Theme dark = new ThemeRecord(
            "#292929",   // baseColor
            "#C8D7E6",   // fgColor
            "#214283",   // paleHighlightColor
            "#ffa2a222", // cautionColor
            "#80808088", // faintColor
            "#303030"    // uiBaseColor
    );

    String baseColor();
    String fgColor();
    String paleHighlightColor();

    /**
     * Get the caution color string.
     * Typically, highlighting the searched string color.
     * @return the caution color string
     */
    String cautionColor();
    String faintColor();
    String uiBaseColor();

    record ThemeRecord(
            String baseColor,
            String fgColor,
            String paleHighlightColor,
            String cautionColor,
            String faintColor,
            String uiBaseColor
    ) implements Theme { }

}
