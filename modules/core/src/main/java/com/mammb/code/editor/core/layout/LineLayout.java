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
package com.mammb.code.editor.core.layout;

import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.text.Text;

/**
 * The line layout.
 * @author Naotsugu Kobayashi
 */
public interface LineLayout {

    int rowToFirstLine(int row);
    int rowToLastLine(int row);

    /**
     * Get the number of line from the specified caret position.
     * @param row the number of row
     * @param col the number of col
     * @return the number of line
     */
    int rowToLine(int row, int col);

    int lineToRow(int line);

    /**
     * Get the total number of lines.
     * @return the total number of lines
     */
    int lineSize();

    /**
     * Get the total number of rows.
     * @return the total number of rows
     */
    int rowSize();

    Text text(int line);
    Text rowTextAt(int row);

    /**
     * Get the line height.
     * @return the line height
     */
    double lineHeight();

    /**
     * Set the width of line wrap characters.
     * @param width the number of characters per line
     */
    void setLineWidth(int width);

    /**
     * Get the width of line wrap characters.
     * @return the width of line wrap characters
     */
    int lineWidth();

    int homeColOnRow(int line);
    default int endColOnRow(int line) {
        return homeColOnRow(line) + text(line).textLength();
    }

    int xToCol(int line, double x);

    default int xToMidCol(int line, double x) {
        return xToCol(line, x + standardCharWidth() / 2);
    }

    default double xOnLayout(int line, int col) {
        return text(line).widthTo(col);
    }
    default double yOnLayout(int line) {
        return line * lineHeight();
    }

    /**
     * Get the standard a character width.
     * @return the standard a character width
     */
    double standardCharWidth();

    /**
     * Get the tab size.
     * @return the tab size
     */
    int tabSize();

    void updateFontMetrics(FontMetrics fontMetrics);

}
