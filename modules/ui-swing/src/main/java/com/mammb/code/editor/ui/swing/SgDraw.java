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

import com.mammb.code.editor.core.Draw;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.Text;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * The draw.
 * @author Naotsugu Kobayashi
 */
public class SgDraw implements Draw {

    private Graphics g;
    private FontMetrics fontMetrics;

    public SgDraw(Component component) {
        this.fontMetrics = new SgFontMetrics(component);
    }

    public Draw with(Graphics g) {
        this.g = Objects.requireNonNull(g);
        return this;
    }

    @Override
    public void clear() {
        g.clearRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
    }

    @Override
    public void text(Text text, double x, double y, double w, List<Style> styles) {

    }

    @Override
    public void rect(double x, double y, double w, double h) {

    }

    @Override
    public void caret(double x, double y) {

    }

    @Override
    public void select(double x1, double y1, double x2, double y2, double l, double r) {

    }

    @Override
    public void underline(double x1, double y1, double x2, double y2, double wrapWidth) {

    }

    @Override
    public void hLine(double x, double y, double w) {

    }

    @Override
    public void increaseFontSize(double sizeDelta) {

    }

    @Override
    public FontMetrics fontMetrics() {
        return fontMetrics;
    }

    @Override
    public void line(Line... lines) {

    }
}
