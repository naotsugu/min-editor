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
package com.mammb.code.editor.ui.swing;

import com.mammb.code.editor.core.FontMetrics;
import java.awt.*;
import java.util.Objects;

/**
 * The font metrics.
 * @author Naotsugu Kobayashi
 */
public class SgFontMetrics implements FontMetrics {

    /** The font metrics. */
    private final java.awt.FontMetrics fm;

    /** The standard a character width. */
    private final double standardCharWidth;

    public SgFontMetrics(java.awt.FontMetrics fm) {
        this.fm = Objects.requireNonNull(fm);
        this.standardCharWidth = fm.charWidth('0');
    }

    public SgFontMetrics(Component component) {
        this(component.getFontMetrics(component.getFont()));
    }

    @Override
    public double getMaxAscent() {
        return fm.getMaxAscent();
    }

    @Override
    public double getAscent() {
        return fm.getAscent();
    }

    @Override
    public double getXheight() {
        return fm.getAscent();
    }

    @Override
    public int getBaseline() {
        return 0;
    }

    @Override
    public double getDescent() {
        return fm.getDescent();
    }

    @Override
    public double getMaxDescent() {
        return fm.getMaxDescent();
    }

    @Override
    public double getLeading() {
        return fm.getLeading();
    }

    @Override
    public double getLineHeight() {
        return fm.getHeight();
    }

    @Override
    public double standardCharWidth() {
        return standardCharWidth;
    }

    @Override
    public double getAdvance(int codePoint) {
        return fm.charWidth(codePoint);
    }

}
