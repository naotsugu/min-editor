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

import com.mammb.code.editor.model.layout.FontMetrics;
import javafx.scene.text.Font;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.IntStream;

/**
 * The utilities of FxFont.
 * @author Naotsugu Kobayashi
 */
public class FxFonts {

    /** The font metrics. */
    private static final FontMetrics<Font> fontMetrics = new FxFontMetrics();

    /** The cache of number character width. */
    private static final Map<Font, Double> numberCharacterWidths = new WeakHashMap<>();

    /** The cache of uppercase letter width. */
    private static final Map<Font, Double> uppercaseLetterWidths = new WeakHashMap<>();


    /**
     * Get the maximum unit width of a number character when drawn in the specified font.
     * @param font the specified font
     * @return the unit width
     */
    public static double numberCharacterWidth(Font font) {
        return numberCharacterWidths.computeIfAbsent(font, f ->
            IntStream.rangeClosed('0', '9')
                .mapToDouble(c -> fontMetrics.getCharWidth(f, (char) c))
                .max().orElse(0.0)
        );
    }


    /**
     * Get the maximum unit width of a uppercase letter when drawn in the specified font.
     * @param font the specified font
     * @return the unit width
     */
    public static double uppercaseLetterWidth(Font font) {
        return uppercaseLetterWidths.computeIfAbsent(font, f ->
            IntStream.rangeClosed('A', 'Z')
                .mapToDouble(c -> fontMetrics.getCharWidth(f, (char) c))
                .max().orElse(0.0)
        );
    }


    /**
     * Get the font line height(ascent + descent + leading).
     * @param font the font
     * @return the font line height(ascent + descent + leading)
     */
    public static double lineHeight(Font font) {
        return fontMetrics.lineHeight(font);
    }

}
