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
package com.mammb.code.editor.syntax.base;

/**
 * Hue.
 * @author Naotsugu Kobayashi
 */
public enum Hue {
    RED, PINK, PURPLE, DEEP_PURPLE, INDIGO, BLUE, LIGHT_BLUE ,CYAN, TEAL, GREEN,
    LIGHT_GREEN,LIME,YELLOW,AMBER,ORANGE,DEEP_ORANGE,BROWN,GREY,BLUE_GREY, DARK_GREY,
    NONE, TRANSPARENT;

    boolean isColored() {
        return this != NONE && this != TRANSPARENT;
    }

}
