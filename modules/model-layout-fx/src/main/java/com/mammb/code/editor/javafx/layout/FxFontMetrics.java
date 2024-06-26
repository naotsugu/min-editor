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
package com.mammb.code.editor.javafx.layout;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * FxFontMetrics.
 * @author Naotsugu Kobayashi
 */
public class FxFontMetrics implements com.mammb.code.editor.model.layout.FontMetrics<Font> {

    /** The FontMetrics. */
    private final Map<Font, FontMetrics> metrics = new WeakHashMap<>();
    /** The loader. */
    private final FontLoader fontLoader;
    /** The base font. */
    private Font base;

    /**
     * Constructor.
     */
    public FxFontMetrics() {
        this(Font.getDefault());
    }


    /**
     * Constructor.
     * @param font the base font
     */
    public FxFontMetrics(Font font) {
        fontLoader = Toolkit.getToolkit().getFontLoader();
        base = Objects.requireNonNull(font);
        metrics.put(base, fontLoader.getFontMetrics(font));
    }

    @Override
    public void setDefault(Font font) {
        base = font;
    }

    @Override
    public double lineHeight() {
        return metrics.get(base).getLineHeight();
    }

    @Override
    public double ascent() {
        return metrics.get(base).getAscent();
    }

    @Override
    public double descent() {
        return metrics.get(base).getDescent();
    }

    @Override
    public double lineHeight(Font font) {
        return metrics.computeIfAbsent(font, fontLoader::getFontMetrics).getLineHeight();
    }

    @Override
    public float getCharWidth(char ch) {
        return metrics.get(base).getCharWidth(ch);
    }

    @Override
    public float getCharWidth(Font font, char ch) {
        return metrics.computeIfAbsent(font, fontLoader::getFontMetrics).getCharWidth(ch);
    }

}
