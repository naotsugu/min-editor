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
package com.mammb.code.editor.core;

import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.Text;
import java.util.List;

/// The Draw.
/// @author Naotsugu Kobayashi
public interface Draw {

    /// Clear canvas.
    void clear();

    /// Draw the text.
    /// @param text
    /// @param x the position x
    /// @param y the position y
    /// @param w the width
    /// @param styles the styles
    void text(Text text, double x, double y, double w, List<Style> styles);

    /// Draw the text.
    /// @param x the position x
    /// @param y the position y
    /// @param w the width
    /// @param h the height
    void rect(double x, double y, double w, double h);

    /// Draw the caret.
    /// @param x the position x
    /// @param y the position y
    void caret(double x, double y);

    void select(double x1, double y1, double x2, double y2, double l, double r);
    void underline(double x1, double y1, double x2, double y2);
    void hLine(double x, double y, double w);

    /// Update font size.
    /// @param sizeDelta the font size delta
    void increaseFontSize(double sizeDelta);

    /// Get the FontMetrics.
    /// @return
    FontMetrics fontMetrics();

    /// Draw the line.
    /// @param lines
    void line(Line... lines);

    record Line(double x1, double y1, double x2, double y2, String color) {}

}
