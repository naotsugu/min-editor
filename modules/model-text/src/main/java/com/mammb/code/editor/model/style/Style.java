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
package com.mammb.code.editor.model.style;

/**
 * Style.
 * @author Naotsugu Kobayashi
 */
public sealed interface Style {

    /**
     * Font family.
     * @param name the font name
     */
    record FontFamily(String name) implements Style {}

    /**
     * Font size.
     * @param size
     */
    record FontSize(double size) implements Style {}

    /**
     * Color
     * @param colorString the string representation of color
     * @param opacity the opacity of color
     */
    record Color(String colorString, double opacity) implements Style {
        public Color(String colorString) { this(colorString, 1); }
    }

    /**
     * BgColor.
     * @param colorString the string representation of color
     * @param opacity the opacity of color
     */
    record BgColor(String colorString, double opacity) implements Style {
        public BgColor(String colorString) { this(colorString, 1); }
    }

}
