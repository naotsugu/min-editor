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
import javafx.scene.control.Labeled;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * The ThemeColor.
 * @param background the background color
 * @param foreground the foreground color
 * @param backgroundActive the background color of active state
 * @param foregroundActive the foreground color of active state
 * @author Naotsugu Kobayashi
 */
public record ThemeColor(
    Color background,
    Color foreground,
    Color backgroundActive,
    Color foregroundActive) {

    public Background backgroundFill() {
        return new Background(new BackgroundFill(background, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public Background backgroundActiveFill() {
        return new Background(new BackgroundFill(backgroundActive, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public String foregroundColorString() {
        return toRGBCode(foreground);
    }

    private static String toRGBCode(Color color) {
        return "#%02X%02X%02X".formatted(
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }

    public void apply(Region region) {
        region.setBackground(backgroundFill());
        region.setBorder(Border.EMPTY);
        if (region instanceof Labeled labeled) {
            labeled.setTextFill(foreground);
        }
    }

    public void apply(ThemeIcon icon) {
        icon.setFill(foreground);
    }

    public void applyHover(Region region) {
        region.setOnMouseEntered(e -> region.setBackground(backgroundActiveFill()));
        region.setOnMouseExited(e -> region.setBackground(backgroundFill()));
    }

    public static ThemeColor darkDefault() {
        return new ThemeColor(
            Color.web("#26252D"), Color.web("#CACACE"),
            Color.web("#42424A"), Color.web("#CACACE"));
    }

    public static ThemeColor lightDefault() {
        return new ThemeColor(
            Color.web("#FFFFFF"), Color.web("#616A71"),
            Color.web("#F7F8F9"), Color.web("#616A71"));
    }

}
