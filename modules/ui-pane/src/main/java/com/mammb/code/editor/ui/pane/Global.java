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
package com.mammb.code.editor.ui.pane;

import com.mammb.code.editor.javafx.layout.FxFontMetrics;
import com.mammb.code.editor.model.layout.FontMetrics;
import javafx.scene.text.Font;

import java.util.stream.IntStream;

/**
 * Global config.
 * @author Naotsugu Kobayashi
 */
public class Global {

    /**
     * Get the maximum unit width of a number character when drawn in the specified font.
     * @param font the specified font
     * @return the unit width
     */
    public static double numberCharacterWidth(Font font) {
        FontMetrics<Font> fontMetrics = new FxFontMetrics(font);
        return IntStream.rangeClosed('0', '9')
            .mapToDouble(c -> fontMetrics.getCharWidth((char) c))
            .max().orElse(0.0);
    }

}
