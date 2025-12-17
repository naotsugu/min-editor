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
package com.mammb.code.editor.ui.fx;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import com.mammb.code.editor.core.TabStop;
import javafx.scene.text.Font;
import com.mammb.code.editor.core.FontMetrics;
import com.sun.javafx.font.CharToGlyphMapper;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.FontStrike;
import com.sun.javafx.font.PGFont;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.FontSmoothingType;

/**
 * The fontMetrics.
 * @author Naotsugu Kobayashi
 */
public class FxFontMetrics implements FontMetrics {

    /** The font metrics. */
    private final com.sun.javafx.tk.FontMetrics fontMetrics;
    /** The standard a character width. */
    private final double standardCharWidth;

    /** The font strike. */
    private final FontStrike strike;
    /** The font resource. */
    private final FontResource resource;
    /** The glyph mapper. */
    private final CharToGlyphMapper mapper;
    /** The cache of advance. */
    private final Map<Integer, Float> advanceCache = new ConcurrentHashMap<>();
    /** The tab stop. */
    private TabStop tabStop = TabStop.DEFAULT;

    /**
     * Constructor.
     * @param font the font that was used to construct these metrics
     * @param smoothingType the smoothing type used to construct these metrics
     */
    FxFontMetrics(Font font, FontSmoothingType smoothingType) {
        Objects.requireNonNull(font);
        Objects.requireNonNull(smoothingType);
        this.fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
        var pgFont = (PGFont) FontHelper.getNativeFont(font);
        this.strike = pgFont.getStrike(BaseTransform.IDENTITY_TRANSFORM,
            smoothingType == FontSmoothingType.GRAY ? FontResource.AA_GREYSCALE : FontResource.AA_LCD);
        this.resource = strike.getFontResource();
        this.mapper  = resource.getGlyphMapper();
        this.standardCharWidth = getAdvance("0");
    }

    /**
     * Create the new {@link FxFontMetrics}.
     * @param font the font
     * @param smoothingType the smoothing type used to construct these metrics
     * @return the new {@link FxFontMetrics}
     */
    public static FxFontMetrics of(Font font, FontSmoothingType smoothingType) {
        return new FxFontMetrics(font, smoothingType);
    }

    @Override
    public final double getMaxAscent() {
        return fontMetrics.getMaxAscent();
    }

    @Override
    public final double getAscent() {
        return fontMetrics.getAscent();
    }

    @Override
    public final double getXheight() {
        return fontMetrics.getXheight();
    }

    @Override
    public final int getBaseline() {
        return fontMetrics.getBaseline();
    }

    @Override
    public final double getDescent() {
        return fontMetrics.getDescent();
    }

    @Override
    public final double getMaxDescent() {
        return fontMetrics.getMaxDescent();
    }

    @Override
    public final double getLeading() {
        return fontMetrics.getLeading();
    }

    @Override
    public final double getLineHeight() {
        return fontMetrics.getLineHeight();
    }

    @Override
    public double standardCharWidth() {
        return standardCharWidth;
    }

    @Override
    public double getAdvance(int codePoint) {
        return advanceCache.computeIfAbsent(codePoint,
                cp -> resource.getAdvance(mapper.charToGlyph(cp), strike.getSize()));
    }

    @Override
    public int getTabSize() {
        return tabStop.value();
    }

    @Override
    public void setTabSize(int size) {
        tabStop = new TabStop(size);
    }

}
