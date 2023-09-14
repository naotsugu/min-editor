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

import com.mammb.code.editor.model.layout.Span;
import com.mammb.code.editor.model.layout.SpanTranslate;
import com.mammb.code.editor.model.style.Style;
import com.mammb.code.editor.model.style.StyleSpan;
import com.mammb.code.editor.model.style.StyledText;
import com.mammb.code.editor.model.text.Translate;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * FxSpanTranslate.
 * @author Naotsugu Kobayashi
 */
public class FxSpanTranslate implements SpanTranslate {

    /** The default font name. */
    private final String defaultName;
    /** The default font size. */
    private final double defaultSize;
    /** The default foreground color. */
    private final Color defaultColor;
    /** The default background color. */
    private final Color defaultBgColor;


    /**
     * Constructor.
     */
    private FxSpanTranslate(String defaultName, double defaultSize, Color defaultColor) {
        this.defaultName = defaultName;
        this.defaultSize = defaultSize;
        this.defaultColor = defaultColor;
        this.defaultBgColor = Color.TRANSPARENT;
    }


    /**
     * Create a new translation.
     * @param defaultName the default font name
     * @param defaultSize the default font size
     * @param defaultColor the default foreground color
     * @return a new translation
     */
    public static Translate<StyledText, List<Span>> of(
            String defaultName,
            double defaultSize,
            String defaultColor) {
        return new FxSpanTranslate(
            defaultName,
            defaultSize,
            Color.web(defaultColor));
    }


    @Override
    public List<Span> applyTo(StyledText row) {

        var list = new ArrayList<Span>();

        for (StyledText text : row.spans()) {

            String fontName = defaultName;
            double fontSize = defaultSize;
            Color fgColor = defaultColor;
            Color bgColor = defaultBgColor;

            for (StyleSpan styleSpan : text.styles()) {
                Style style = styleSpan.style();
                if (style instanceof Style.FontFamily family) {
                    fontName = family.name();
                } else if (style instanceof Style.FontSize size) {
                    fontSize = size.size();
                } else if (style instanceof Style.Color fg) {
                    fgColor = Color.web(fg.colorString());
                } else if (style instanceof Style.BgColor bg) {
                    bgColor = Color.web(bg.colorString());
                }
            }

            Font font = Font.font(fontName, fontSize);
            Span span = Span.of(text, FxFontStyle.of(font, fgColor, bgColor));
            list.add(span);
        }

        return list;

    }

}
