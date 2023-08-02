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
package com.mammb.code.editor2.model.layout;

/**
 * FontMetrics.
 * @param <F> the type of font
 * @author Naotsugu Kobayashi
 */
public interface FontMetrics<F> {

    /**
     * Set the default font.
     * @param font the default marked font
     */
    void setDefault(F font);

    /**
     * Get the base font line height(ascent + descent + leading).
     * @return the base font line height(ascent + descent + leading)
     */
    double lineHeight();

    /**
     * Get the font line height(ascent + descent + leading).
     * @param font the font style
     * @return the font line height(ascent + descent + leading)
     */
    double lineHeight(FontStyle<F, ?> font);

    /**
     * Computes the width of the char when rendered with the font represented by this FontMetrics instance.
     * @param ch the characters to be inspected
     * @return the width of the char
     */
    float getCharWidth(char ch);

    /**
     * Computes the width of the char when rendered with the font represented by this FontMetrics instance.
     * @param font the font
     * @param ch the characters to be inspected
     * @return the width of the char
     */
    float getCharWidth(F font, char ch);
}
