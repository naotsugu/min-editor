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
package com.mammb.code.editor.javafx.layout;

import com.mammb.code.editor2.model.layout.FontStyle;
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
public class FxFontMetrics implements com.mammb.code.editor2.model.layout.FontMetrics<Font> {

    private final Map<Font, FontMetrics> metrics = new WeakHashMap<>();
    private final FontLoader fontLoader;
    private Font base;

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
    public double lineHeight(FontStyle<Font, ?> font) {
        return metrics.computeIfAbsent(
                font.font(),
                fontLoader::getFontMetrics)
            .getLineHeight();
    }

}
