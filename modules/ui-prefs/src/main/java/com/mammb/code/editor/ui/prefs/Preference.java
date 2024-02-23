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
package com.mammb.code.editor.ui.prefs;

import com.mammb.code.editor.ui.prefs.impl.PreferenceImpl;

/**
 * Preference.
 * @author Naotsugu Kobayashi
 */
public interface Preference {

    /**
     * Get the color scheme.
     * @return the color scheme
     */
    ColorScheme colorScheme();

    /**
     * Get the font size.
     * @return the font size
     */
    double fontSize();

    /**
     * Get the font name.
     * @return the font name
     */
    String fontName();

    /**
     * Get the background color.
     * @return the background color
     */
    String bgColor();

    /**
     * Get the foreground color.
     * @return the foreground color
     */
    String fgColor();

    /**
     * Create a new preference.
     * @return a new preference
     */
    static Preference of() {
        return PreferenceImpl.of();
    }

}
