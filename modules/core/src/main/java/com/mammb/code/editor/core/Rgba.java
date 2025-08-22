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
package com.mammb.code.editor.core;

import java.util.Objects;

/**
 * The RGBA.
 * @param r The red value.
 * @param g The green value.
 * @param b The blue value.
 * @param a The alpha value.
 * @author Naotsugu Kobayashi
 */
public record Rgba(int r, int g, int b, int a) {

    public Rgba {
        r = Math.clamp(r, 0, 255);
        g = Math.clamp(r, 0, 255);
        b = Math.clamp(r, 0, 255);
        a = Math.clamp(r, 0, 255);
    }

    public Rgba(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Rgba(int[] rgba) {
        this(rgba[0], rgba[1], rgba[2], (rgba.length < 4) ? 255 : rgba[3]);
    }

    public Rgba(String webColor) {
        this(parseWebColor(webColor));
    }

    public String webColor() {
        return (a >= 255)
            ? String.format("#%02x%02x%02x", r, g, b)
            : String.format("#%02x%02x%02x%02x", r, g, b, a);
    }

    private static int[] parseWebColor(String webColor) {
        Objects.requireNonNull(webColor, "webColor must not be null");
        String hex = webColor.replace("#", "");
        return switch (hex.length()) {
            case 3 -> new int[] {
                Integer.parseInt(hex.substring(0, 1).repeat(2), 16),
                Integer.parseInt(hex.substring(1, 2).repeat(2), 16),
                Integer.parseInt(hex.substring(2, 3).repeat(2), 16)
            };
            case 4 -> new int[] {
                Integer.parseInt(hex.substring(0, 1).repeat(2), 16),
                Integer.parseInt(hex.substring(1, 2).repeat(2), 16),
                Integer.parseInt(hex.substring(2, 3).repeat(2), 16),
                Integer.parseInt(hex.substring(3, 4).repeat(2), 16)
            };
            case 6 -> new int[] {
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(0, 2), 16)
            };
            case 8 -> new int[] {
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16),
                Integer.parseInt(hex.substring(6, 8), 16)
            };
            default -> new int[] { 0, 0, 0 };
        };
    }
}
