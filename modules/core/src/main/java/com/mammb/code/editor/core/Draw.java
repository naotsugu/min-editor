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

import java.util.List;
import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.Text;

/**
 * The Draw.
 * @author Naotsugu Kobayashi
 */
public interface Draw {

    /**
     * Clear canvas.
     */
    void clear();

    /**
     * Draw the text.
     * @param text the text
     * @param x the position x
     * @param y the position y
     * @param w the width
     * @param styles the styles
     */
    void text(Text text, double x, double y, double w, List<Style> styles);

    /**
     * Draw the rectangle.
     * @param x the position x
     * @param y the position y
     * @param w the width
     * @param h the height
     */
    void rect(double x, double y, double w, double h);

    /**
     * Draw the caret.
     * @param x the position x
     * @param y the position y
     */
    void caret(double x, double y);

    /**
     * Draw the selection.
     * @param x1 the position x1
     * @param y1 the position y1
     * @param x2 the position x2
     * @param y2 the position y2
     * @param l the left position
     * @param r the right position
     */
    void select(double x1, double y1, double x2, double y2, double l, double r);

    /**
     * Draw the underline.
     * @param x1 the position x1
     * @param y1 the position y1
     * @param x2 the position x2
     * @param y2 the position y2
     * @param wrapWidth the wrap width
     */
    void underline(double x1, double y1, double x2, double y2, double wrapWidth);

    /**
     * Draw the horizontal line.
     * @param x the position x
     * @param y the position y
     * @param w the width
     */
    void hLine(double x, double y, double w);

    /**
     * Update font size.
     * @param sizeDelta the font size delta
     */
    void increaseFontSize(double sizeDelta);

    /**
     * Get the {@link FontMetrics}.
     * @return the {@link FontMetrics}
     */
    FontMetrics fontMetrics();

    /**
     * Draw the lines.
     * @param lines the lines
     */
    void line(Line... lines);

    /** Line record. */
    record Line(double x1, double y1, double x2, double y2, Rgba color) {}

}
