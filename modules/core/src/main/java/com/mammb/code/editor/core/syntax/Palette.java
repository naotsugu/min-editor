/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.core.syntax;

import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.Style.TextColor;

/**
 * The palette.
 * @author Naotsugu Kobayashi
 */
public interface Palette {

    TextColor gray = new Style.TextColor("#888888");
    TextColor darkGreen = new Style.TextColor("#6A8759");
    TextColor darkOrange = new Style.TextColor("#CC7832");
    TextColor darkPale = new Style.TextColor("#6897BB");

    TextColor ansiRed = new Style.TextColor("#f0524f");
    TextColor ansiGreen = new Style.TextColor("#5c962c");
    TextColor ansiBlue = new Style.TextColor("#3993d4");

    TextColor twDarkRed = new Style.TextColor("#B91C1C");    // Tailwind RED 700
    TextColor twRed = new Style.TextColor("#ef4444");        // Tailwind RED 500
    TextColor twLightRed = new Style.TextColor("#FECACA");   // Tailwind RED 300
    TextColor twDarkGreen = new Style.TextColor("#15803d");  // Tailwind GREEN 700
    TextColor twGreen = new Style.TextColor("#22c55e");      // Tailwind GREEN 500
    TextColor twLightGreen = new Style.TextColor("#86EFAC"); // Tailwind GREEN 300
    TextColor twDarkBlue = new Style.TextColor("#1d4ed8");   // Tailwind BLUE 700
    TextColor twBlue = new Style.TextColor("#3b82f6");       // Tailwind BLUE 500
    TextColor twLightBlue = new Style.TextColor("#93c5fd");  // Tailwind BLUE 300

}
