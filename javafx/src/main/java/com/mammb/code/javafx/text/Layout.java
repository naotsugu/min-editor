/*
 * Copyright 2019-2023 the original author or authors.
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
package com.mammb.code.javafx.text;

import com.sun.javafx.geom.Shape;
import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.scene.text.TextLine;
import com.sun.javafx.scene.text.TextSpan;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;
import java.util.Objects;

/**
 * Layout.
 * @author Naotsugu Kobayashi
 */
public class Layout {

    private TextLayout layout;


    public Layout() {
        layout = Toolkit.getToolkit().getTextLayoutFactory().createLayout();
        layout.setTabSize(4);
    }


    /**
     * Sets the content for the TextLayout.
     * Supports multiple spans (rich text).
     * @return {@code true} is the call modifies the layout internal state
     */
    public void setContent(Span... spans) {
        layout.setContent(spans);
    }


    /**
     * Sets the content for the TextLayout. Shorthand for single span text.
     * @return returns true is the call modifies the layout internal state.
     */
    public boolean setContent(String string, Font font) {
        Objects.requireNonNull(string);
        Objects.requireNonNull(font);
        return layout.setContent(string, FontHelper.getNativeFont(font));
    }


    /**
     * Returns the lines of text layout.
     * @return the text lines
     */
    public TextLine[] getLines() {
        return layout.getLines();
    }


    /**
     * Returns the GlyphList of text layout.
     * The runs are returned order visually (rendering order), starting from the first line.
     * @return the runs
     */
    public GlyphList[] getRuns() {
        return layout.getRuns();
    }


    /**
     * Returns the shape of the entire text layout relative to the baseline of the first line.
     * @param type the type of the shapes to include
     * @return the shape
     */
    public Shape getShape(int type, TextSpan filter) {
        return layout.getShape(type, filter);
    }


    /**
     * Maps local point to index in the content.
     * @param point the specified point to be tested
     * @return a {@code HitInfo} representing the character index found
     */
    public final HitInfo hitInfo(javafx.geometry.Point2D point) {
        if (point != null) {
            double x = point.getX();
            double y = point.getY();
            TextLayout.Hit layoutHit = layout.getHitInfo((float)x, (float)y);
            return new HitInfo(
                layoutHit.getCharIndex(),
                layoutHit.getInsertionIndex(),
                layoutHit.isLeading());
        } else {
            return null;
        }
    }


    /**
     * Sets the wrap width for the TextLayout.
     * @return {@code true} is the call modifies the layout internal state.
     */
    public boolean setWrapWidth(float wrapWidth) {
        return layout.setWrapWidth(wrapWidth);
    }


    /**
     * Sets the line spacing for the TextLayout.
     * @return {@code true} is the call modifies the layout internal state.
     */
    public boolean setLineSpacing(float spacing) {
        return layout.setLineSpacing(spacing);
    }


    /**
     * Sets the tab size for the TextLayout.
     * @param spaces the number of spaces represented by a tab. Default is 8.
     * Minimum is 1, lower values will be clamped to 1
     * @return {@code true} if the call modifies the layout internal state
     */
    public boolean setTabSize(int spaces) {
        return layout.setTabSize(spaces);
    }

}
