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

    public static final Rgba ORANGE = new Rgba("#FF8C00");
    public static final Rgba LIGHTGRAY = new Rgba("#D3D3D3");

    public Rgba {
        r = Math.clamp(r, 0, 255);
        g = Math.clamp(g, 0, 255);
        b = Math.clamp(b, 0, 255);
        a = Math.clamp(a, 0, 255);
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

    public String web() {
        return (a >= 255)
            ? String.format("#%02x%02x%02x", r, g, b)
            : String.format("#%02x%02x%02x%02x", r, g, b, a);
    }

    public <T> T as(Function<int[], T> fun) {
        return fun.apply(new int[] { r, g, b, a });
    }

    public Rgba opaque() {
        return new Rgba(r, g, b, 255);
    }

    public Rgba alphaWith(int alpha) {
        return new Rgba(r, g, b, alpha);
    }

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
