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
    private final ColorString defaultColor;
    /** The default background color. */
    private final ColorString defaultBgColor;

    /** The font cache. */
    private Font fontCache;
    /** The color cache. */
    private ColorString colorCache;
    /** The background cache. */
    private ColorString bgColorCache;


    /**
     * Constructor.
     */
    private FxSpanTranslate(String defaultName, double defaultSize, String defaultColor) {
        this.defaultName = defaultName;
        this.defaultSize = defaultSize;
        this.defaultColor = new ColorString(defaultColor, 1.0);
        this.defaultBgColor = new ColorString("rgba(0,0,0,0)", 0); // Color.TRANSPARENT;
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
            defaultColor);
    }


    @Override
    public List<Span> applyTo(StyledText row) {

        var list = new ArrayList<Span>();

        for (StyledText text : row.spans()) {

            String fontName = defaultName;
            double fontSize = defaultSize;
            ColorString fgColor = defaultColor;
            ColorString bgColor = defaultBgColor;
            String context = "";

            for (StyleSpan styleSpan : text.styles()) {
                Style style = styleSpan.style();
                if (style instanceof Style.FontFamily family) {
                    fontName = family.name();
                } else if (style instanceof Style.FontSize size) {
                    fontSize = size.size();
                } else if (style instanceof Style.Color fg) {
                    fgColor = color(fg.colorString());
                } else if (style instanceof Style.BgColor bg) {
                    bgColor = bgColor(bg.colorString(), bg.opacity());
                } else if (style instanceof Style.Context ctx) {
                    context = ctx.name();
                }
            }

            Span span = Span.of(text, context, FxFontStyle.of(
                font(fontName, fontSize), fgColor.color, bgColor.color));
            list.add(span);
        }

        return list;

    }


    private Font font(String fontName, double fontSize) {
        if (fontCache != null && fontCache.getName().equals(fontName) && fontCache.getSize() == fontSize) {
            return fontCache;
        }
        return fontCache = Font.font(fontName, fontSize);
    }


    private ColorString color(String colorString) {
        if (defaultColor.colorString().equals(colorString)) {
            return defaultColor;
        }
        return colorCache = (colorCache == null)
            ? defaultColor.of(colorString, 1.0)
            : colorCache.of(colorString, 1.0);
    }


    private ColorString bgColor(String colorString, double opacity) {
        if (defaultBgColor.colorString().equals(colorString)) {
            return defaultBgColor;
        }
        return bgColorCache = (bgColorCache == null)
            ? defaultColor.of(colorString, opacity)
            : bgColorCache.of(colorString, opacity);
    }


    private record ColorString(Color color, String colorString, double opacity) {
        ColorString(String colorString, double opacity) {
            this(Color.web(colorString, opacity), colorString, opacity);
        }
        ColorString of(String cs, double op) {
            return (colorString.equals(cs) && opacity == op) ? this : new ColorString(cs, op);
        }
    }

}
