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
package com.mammb.code.editor.ui.app;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * The UiColor.
 * @param background the background color
 * @param foreground the foreground color
 * @param backgroundActive the background color of active state
 * @param foregroundActive the foreground color of active state
 * @param backgroundDisable the background color of disable state
 * @param foregroundDisable the foreground color of disable state
 * @author Naotsugu Kobayashi
 */
public record UiColor(
    String background,
    String foreground,
    String backgroundActive,
    String foregroundActive,
    String backgroundDisable,
    String foregroundDisable,
    String accent) {

    public static UiColor darkDefault() {
        return new UiColor(
            "#26252D", "#CACACE",
            "#42424A", "#CACACE",
            "#42424A", "#8A8A8E",
            "#589DF6");
    }

    public static UiColor lightDefault() {
        return new UiColor(
            "#FFFFFF", "#616A71",
            "#F7F8F9", "#616A71",
            "#F7F8F9", "#C1CAD1",
            "#589DF6");
    }
    public Color backgroundColor() {
        return Color.web(background);
    }

    public Color foregroundColor() {
        return Color.web(foreground);
    }

    public Color backgroundActiveColor() {
        return Color.web(backgroundActive);
    }

    public Color foregroundActiveColor() {
        return Color.web(foregroundActive);
    }

    public Color backgroundDisableColor() {
        return Color.web(backgroundDisable);
    }

    public Color foregroundDisableColor() {
        return Color.web(foregroundDisable);
    }

    public Color accentColor() {
        return Color.web(accent);
    }

    public Background backgroundFill() {
        return new Background(new BackgroundFill(backgroundColor(), CornerRadii.EMPTY, Insets.EMPTY));
    }

    public Background backgroundActiveFill() {
        return new Background(new BackgroundFill(backgroundActiveColor(), CornerRadii.EMPTY, Insets.EMPTY));
    }

}
