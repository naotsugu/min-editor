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

import java.util.List;
import java.util.Optional;
import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.Loc;
import com.mammb.code.editor.core.ScreenScroll;
import com.mammb.code.editor.core.text.Text;

/**
 * The screen view port layout.
 * @author Naotsugu Kobayashi
 */
public interface ScreenLayout extends LineLayout {

    /**
     * Set the screen size.
     * @param width the width
     * @param height the height
     */
    void setScreenSize(double width, double height);

    /**
     * Scroll next.
     * @param lineDelta scroll delta
     */
    void scrollNext(int lineDelta);

    /**
     * Scroll previous.
     * @param lineDelta scroll delta
     */
    void scrollPrev(int lineDelta);

    /**
     * Scroll to the specified line.
     * @param line the specified line
     */
    void scrollAt(int line);

    /**
     * Scroll horizontally.
     * @param x the scroll amount
     */
    void scrollX(double x);

    /**
     * Clear the buffer for the specified line range.
     * @param startRow the start row
     * @param endRow the end row (include)
     */
    void refreshBuffer(int startRow, int endRow);

    /**
     * Get the text list on the screen.
     * @return the text list on the screen
     */
    List<Text> texts();

    /**
     * Get the list of line numbers on the screen.
     * @return the list of line numbers on the screen
     */
    List<Text> lineNumbers();

    /**
     * Get the width of a line number.
     * @return the width of a line number
     */
    double lineNumberWidth();

    /**
     * Get the coordinates of the specified position.
     * @param row the row number
     * @param col the column number
     * @return the location
     */
    Optional<Loc> locationOn(int row, int col);

    /**
     * Get the line number at the y-coordinate location.
     * @param y the y-coordinate location
     * @return the line number
     */
    int yToLineOnScreen(double y);

    /**
     * Get the screen width.
     * @return the screen width
     */
    double screenWidth();

    /**
     * Get the screen height.
     * @return the screen height
     */
    double screenHeight();

    /**
     * Get the number of lines on the screen.
     * @return the number of lines on the screen
     */
    int screenLineSize();

    /**
     * Get the number of columns on the screen.
     * @return the number of columns on the screen
     */
    int screenColSize();

    /**
     * Get the number of line at the top of the screen.
     * @return the number of line at the top of the screen
     */
    int topLine();

    /**
     * Get the number of row at the top of the screen.
     * @return the number of row at the top of the screen
     */
    int topRow();

    /**
     * Get the horizontal scroll position.
     * @return the horizontal scroll position
     */
    double xShift();

    /**
     * Apply the screen scroll.
     * @param screenScroll the screen scroll
     */
    void applyScreenScroll(ScreenScroll screenScroll);

    /**
     * Update the {@link FontMetrics}.
     * @param fontMetrics the {@link FontMetrics}
     */
    void updateFontMetrics(FontMetrics fontMetrics);

    void toggleLayout();

    /**
     * Create a new {@link ScreenLayout}.
     * @param content the content
     * @param fm the font metrics
     * @return a new {@link ScreenLayout}
     */
    static ScreenLayout of(Content content, FontMetrics fm) {
        return new BasicScreenLayout(new RowLayout(content, fm));
    }

    /**
     * Create a new {@link ScreenLayout}.
     * @param content the content
     * @param fm the font metrics
     * @return a new {@link ScreenLayout}
     */
    static ScreenLayout csvOf(Content content, FontMetrics fm) {
        return new BasicScreenLayout(new CsvLayout(content, fm));
    }

    /**
     * Create a new line wrapped {@link ScreenLayout}.
     * @param content the content
     * @param fm the font metrics
     * @return a new {@link ScreenLayout}
     */
    static ScreenLayout wrapOf(Content content, FontMetrics fm) {
        return new BasicScreenLayout(new WrapLayout(content, fm));
    }

}
