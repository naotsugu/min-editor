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
package com.mammb.code.editor.ui.prefs.impl;

import com.mammb.code.editor.ui.prefs.ColorScheme;
import com.mammb.code.editor.ui.prefs.Preference;

/**
 * Preference.
 * @author Naotsugu Kobayashi
 */
public class PreferenceImpl implements Preference {

    private ColorScheme colorScheme;

    private double fontSize;

    public String fontName;

    public String bgColor;

    public String fgColor;


    private PreferenceImpl(ColorScheme colorScheme, double fontSize, String fontName, String bgColor, String fgColor) {
        this.colorScheme = colorScheme;
        this.fontSize = fontSize;
        this.fontName = fontName;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
    }

    public static Preference of() {
        ColorScheme colorScheme = ColorScheme.platform();
        String fontName = System.getProperty("os.name").toLowerCase().startsWith("windows") ? "MS Gothic" : "Consolas";
        return new PreferenceImpl(
            colorScheme,
            15,
            fontName,
            colorScheme.isDark() ? "#292929" : "#FEFEFE",
            colorScheme.isDark() ? "#C9D7E6" : "#191919"
        );
    }

    @Override
    public ColorScheme colorScheme() {
        return colorScheme;
    }

    @Override
    public double fontSize() {
        return fontSize;
    }

    @Override
    public String fontName() {
        return fontName;
    }

    @Override
    public String bgColor() {
        return bgColor;
    }

    @Override
    public String fgColor() {
        return fgColor;
    }

}
