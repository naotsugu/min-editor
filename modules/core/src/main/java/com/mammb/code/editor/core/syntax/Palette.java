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

import com.mammb.code.editor.core.Rgba;
import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.Style.TextColor;

/**
 * The palette.
 * @author Naotsugu Kobayashi
 */
public interface Palette {

    TextColor gray = new Style.TextColor(new Rgba("#888888"));
    TextColor darkGreen = new Style.TextColor(new Rgba("#6A8759"));
    TextColor darkOrange = new Style.TextColor(new Rgba("#CC7832"));
    TextColor darkPale = new Style.TextColor(new Rgba("#6897BB"));

    TextColor ansiRed = new Style.TextColor(new Rgba("#f0524f"));
    TextColor ansiGreen = new Style.TextColor(new Rgba("#5c962c"));
    TextColor ansiBlue = new Style.TextColor(new Rgba("#3993d4"));

    TextColor twDarkRed = new Style.TextColor(new Rgba("#B91C1C"));    // Tailwind RED 700
    TextColor twRed = new Style.TextColor(new Rgba("#ef4444"));        // Tailwind RED 500
    TextColor twLightRed = new Style.TextColor(new Rgba("#FECACA"));   // Tailwind RED 300
    TextColor twDarkGreen = new Style.TextColor(new Rgba("#15803d"));  // Tailwind GREEN 700
    TextColor twGreen = new Style.TextColor(new Rgba("#22c55e"));      // Tailwind GREEN 500
    TextColor twLightGreen = new Style.TextColor(new Rgba("#86EFAC")); // Tailwind GREEN 300
    TextColor twDarkBlue = new Style.TextColor(new Rgba("#1d4ed8"));   // Tailwind BLUE 700
    TextColor twBlue = new Style.TextColor(new Rgba("#3b82f6"));       // Tailwind BLUE 500
    TextColor twLightBlue = new Style.TextColor(new Rgba("#93c5fd"));  // Tailwind BLUE 300

}
