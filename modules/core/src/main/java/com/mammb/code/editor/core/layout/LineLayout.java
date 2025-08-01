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
     * Get the number of lines from the specified caret position.
     * @param row the number of rows
     * @param col the number of cols
     * @return the number of lines
     */
    int rowToLine(int row, int col);

    /**
     * Get the number of rows for the specified number of lines.
     * @param line the specified number of lines
     * @return the number of rows
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
     * Get the line width.
     * @return the line width, {@code 0} if no wrapped
     */
    default double lineWidth() {
        return charsInLine() * standardCharWidth();
    }

    /**
     * Set the number of characters in a line.
     * @param n the number of characters in a line
     */
    void setCharsInLine(int n);

    /**
     * Get the number of characters in a line.
     * @return the number of characters in a line, {@code 0} if no wrapped
     */
    int charsInLine();

    /**
     * Get the first column of the specified number of lines.
     * Always zero if not line wrapped
     * @param line the specified number of lines
     * @return the first column
     */
    int homeColOnRow(int line);

    /**
     * Get the end column of the specified number of lines.
     * Always line length, if not line wrapped
     * <p>
     *  row  line
     *   0    0    |*|*|*|*|*|    -> 5
     *        1    |*|*|          -> 2
     *   1    2    |*|*|*| | |    -> 3
     * </p>
     * @param line the specified number of lines
     * @return the end column
     */
    default int endColOnRow(int line) {
        return homeColOnRow(line) + text(line).textLength();
    }

    /**
     * Get the end column of the specified number of rows.
     * <p>
     *  row
     *   0  |*|*|*|*|*|
     *      |*|*|          -> 7
     *   1  |*|*|*| | |    -> 3
     * </p>
     * @param row the specified number of rows
     * @return the end column
     */
    default int endColOnRowAt(int row) {
        return rowTextAt(row).textLength();
    }

    /**
     * Get the number of columns at the specified x-coordinate of the specified line.
     * @param line the specified line
     * @param x the specified x-coordinate
     * @return the number of columns
     */
    int xToCol(int line, double x);


    int xToCaretCol(int line, double x);

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
     * @param line the number of lines
     * @param x the x-coordinate
     * @return the x-coordinate
     */
    default int xToMidCol(int line, double x) {
        return xToCol(line, x + standardCharWidth() / 2);
    }

    /**
     * Get the x-coordinate of the specified position.
     * @param line the number of lines
     * @param col the number of columns
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

    /**
     * Get the font metrics.
     * @return the font metrics
     */
    FontMetrics fontMetrics();

}
