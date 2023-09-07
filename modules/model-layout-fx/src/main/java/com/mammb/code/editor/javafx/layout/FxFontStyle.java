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
package com.mammb.code.editor.javafx.layout;

import com.mammb.code.editor2.model.layout.FontStyle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * FxSpanStyle.
 * @author Naotsugu Kobayashi
 */
public interface FxFontStyle extends FontStyle<Font, Color> {

    String defaultName = System.getProperty("os.name").toLowerCase().startsWith("windows") ? "MS Gothic" : "Consolas";
    double defaultSize = 15;
    Color defaultColor = Color.BLACK;
    Color defaultBgColor = Color.TRANSPARENT;

    /**
     * Get the font.
     * @return the font
     */
    Font font();

    /**
     * Create a new Style.
     * @return a created Style
     */
    static FxFontStyle of() {
        return of(Font.font(defaultName, defaultSize));
    }

    /**
     * Create a new Style.
     * @param color the color
     * @return a created Style
     */
    static FxFontStyle of(Color color) {
        return of(Font.font(defaultName, defaultSize), color);
    }

    /**
     * Create a new Style.
     * @param font the font
     * @return a created Style
     */
    static FxFontStyle of(Font font) {
        return of(font, defaultColor, defaultBgColor);
    }

    /**
     * Create a new Style.
     * @param font the font
     * @param color the color
     * @return a created Style
     */
    static FxFontStyle of(Font font, Color color) {
        return of(font, color, defaultBgColor);
    }

    /**
     * Create a new Style.
     * @param family the family of the font
     * @param size the point size of the font
     * @param colorString the name or numeric representation of the color in one of the supported formats
     * @param opacity the opacity component in range from 0.0 (transparent) to 1.0 (opaque)
     * @param bgColorString the name or numeric representation of the background color in one of the supported formats
     * @param bgOpacity the background color opacity component in range from 0.0 (transparent) to 1.0 (opaque)
     * @return a created Style
     */
    static FxFontStyle of(
            String family, double size,
            String colorString, double opacity,
            String bgColorString, double bgOpacity) {
        return of(Font.font(family, size), Color.web(colorString, opacity), Color.web(bgColorString, bgOpacity));
    }

    /**
     * Create a new Style.
     * @param font the font
     * @param color the color
     * @param background the background color
     * @return a created Style
     */
    static FxFontStyle of(Font font, Color color, Color background) {
        record FontStyleRecord(Font font, Color color, Color background) implements FxFontStyle { }
        return new FontStyleRecord(font, color, background);
    }

}
