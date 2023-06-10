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
package com.mammb;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Style.
 * @author Naotsugu Kobayashi
 */
public interface Style {

    /**
     * Get the font.
     * @return the font
     */
    Font font();

    /**
     * Create a new Style.
     * @param font the font
     * @return a created Style
     */
    static Style of(Font font) {
        record StyleRecord(Font font) implements Style { }
        return new StyleRecord(font);
    }

    /**
     * Create a new Style.
     * @param font the font
     * @param color the color
     * @return a created Style
     */
    static Style of(Font font, Color color) {
        record StyleRecord(Font font, Color color) implements Style { }
        return new StyleRecord(font, color);
    }

    /**
     * Create a new Style.
     * @param font the font
     * @param color the color
     * @param bgColor the background color
     * @return a created Style
     */
    static Style of(Font font, Color color, Color bgColor) {
        record StyleRecord(Font font, Color color, Color bgColor) implements Style { }
        return new StyleRecord(font, color, bgColor);
    }

}
