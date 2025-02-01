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
package com.mammb.code.editor.core.layout;

import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.text.Text;

/**
 * The line layout.
 * @author Naotsugu Kobayashi
 */
public interface LineLayout {

    /**
     * Get the first number of lines in the specified row.
     * @param row the specified row
     * @return the first number of lines
     */
    int rowToFirstLine(int row);

    /**
     * Get the last number of lines in the specified row.
     * @param row the specified row
     * @return the last number of lines
     */
    int rowToLastLine(int row);

    /**
     * Get the number of line from the specified caret position.
     * @param row the number of row
     * @param col the number of col
     * @return the number of line
     */
    int rowToLine(int row, int col);

    /**
     * Get the number of row for the specified number of line.
     * @param line the specified number of line
     * @return the number of row
     */
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

    /**
     * Get the text as the specified line number.
     * @param line the specified line
     * @return the text
     */
    Text text(int line);

    /**
     * Get the row text as the specified row number.
     * @param row the specified row number
     * @return the row text
     */
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

    /**
     * Get the first column of the specified number of line.
     * Always zero if not line wrapped
     * @param line the specified number of line
     * @return the first column
     */
    int homeColOnRow(int line);

    /**
     * Get the end column of the specified number of line.
     * Always line length if not line wrapped
     * @param line the specified number of line
     * @return the end column
     */
    default int endColOnRow(int line) {
        return homeColOnRow(line) + text(line).textLength();
    }

    /**
     * Get the number of column at the specified x-coordinate of the specified line.
     * @param line the specified line
     * @param x the specified x-coordinate
     * @return the number of column
     */
    int xToCol(int line, double x);

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

    /**
     * Update the {@link FontMetrics).
     * @param fontMetrics the {@link FontMetrics)
     */
    void updateFontMetrics(FontMetrics fontMetrics);

    /**
     * Get the x-coordinate of the specified position.
     * Threshold is the center coordinates of the character.
     * @param line the number of line
     * @param x the x-coordinate
     * @return the x-coordinate
     */
    default int xToMidCol(int line, double x) {
        return xToCol(line, x + standardCharWidth() / 2);
    }

    /**
     * Get the x-coordinate of the specified position.
     * @param line the number of line
     * @param col the number of column
     * @return the x-coordinate
     */
    default double xOnLayout(int line, int col) {
        return text(line).widthTo(col);
    }

    /**
     * Get the y-coordinate of the specified line.
     * @param line the specified line
     * @return the y-coordinate
     */
    default double yOnLayout(int line) {
        return line * lineHeight();
    }

}
