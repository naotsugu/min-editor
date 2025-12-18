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
import java.util.function.Function;

/**
 * The RGBA.
 * @param r The red value.
 * @param g The green value.
 * @param b The blue value.
 * @param a The alpha value.
 * @author Naotsugu Kobayashi
 */
public record Rgba(int r, int g, int b, int a) {

    /** The orange. */
    public static final Rgba ORANGE = new Rgba("#FF8C00");
    /** The light gray. */
    public static final Rgba LIGHTGRAY = new Rgba("#D3D3D3");

    /**
     * Canonical Constructor.
     * @param r The red value.
     * @param g The green value.
     * @param b The blue value.
     * @param a The alpha value.
     */
    public Rgba {
        r = Math.clamp(r, 0, 255);
        g = Math.clamp(g, 0, 255);
        b = Math.clamp(b, 0, 255);
        a = Math.clamp(a, 0, 255);
    }

    /**
     * Create a new Rgba.
     * @param r The red value.
     * @param g The green value.
     * @param b The blue value.
     */
    public Rgba(int r, int g, int b) {
        this(r, g, b, 255);
    }

    /**
     * Create a new Rgba.
     * @param rgba the rgba array
     */
    public Rgba(int[] rgba) {
        this(rgba[0], rgba[1], rgba[2], (rgba.length < 4) ? 255 : rgba[3]);
    }

    /**
     * Create a new Rgba from the webColor string.
     * @param webColor the webColor string
     */
    public Rgba(String webColor) {
        this(parseWebColor(webColor));
    }

    /**
     * Get the webColor string.
     * @return the webColor string
     */
    public String web() {
        return (a >= 255)
            ? String.format("#%02x%02x%02x", r, g, b)
            : String.format("#%02x%02x%02x%02x", r, g, b, a);
    }

    /**
     * Get the rgba as the specified type.
     * @param fun the function to apply
     * @return the specified type
     * @param <T> the type to convert to
     */
    public <T> T as(Function<int[], T> fun) {
        return fun.apply(new int[] { r, g, b, a });
    }

    /**
     * Creates a new {@code Rgba} with the alpha value set to 255.
     * @return a new opaque {@code Rgba} object
     */
    public Rgba opaque() {
        return new Rgba(r, g, b, 255);
    }

    /**
     * Creates a new {@code Rgba} with the specified alpha value.
     * @param alpha the alpha value
     * @return a new {@code Rgba} object with the specified alpha value
     */
    public Rgba alphaWith(int alpha) {
        return new Rgba(r, g, b, alpha);
    }

    /**
     * Parses a web color string and converts it into an array of integer values
     * representing the color components.
     *
     * @param webColor the web color string (e.g., "#RRGGBB", "#RGB", "#RRGGBBAA").
     *                 It must not be null. The "#" prefix is optional.
     * @return an array of integers representing the red, green, blue, and optionally
     *         alpha components of the color. If the input format is invalid, it returns
     *         a default array [0, 0, 0].
     */
    private static int[] parseWebColor(String webColor) {
        Objects.requireNonNull(webColor, "webColor must not be null");
        String hex = webColor.replace("#", "");
        return switch (hex.length()) {
            case 3 -> new int[] { h(hex, 0, 1), h(hex, 1, 1), h(hex, 2, 1) };
            case 4 -> new int[] { h(hex, 0, 1), h(hex, 1, 1), h(hex, 2, 1), h(hex, 3, 1) };
            case 6 -> new int[] { h(hex, 0, 2), h(hex, 2, 2), h(hex, 4, 2) };
            case 8 -> new int[] { h(hex, 0, 2), h(hex, 2, 2), h(hex, 4, 2), h(hex, 6, 2) };
            default -> new int[] { 0, 0, 0 };
        };
    }

    private static int h(String s, int i, int n) {
        return Integer.parseInt(s.substring(i, i + n).repeat(n == 1 ? 2 : 1), 16);
    }

}
