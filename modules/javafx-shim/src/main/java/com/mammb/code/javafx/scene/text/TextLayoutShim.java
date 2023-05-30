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
package com.mammb.code.javafx.scene.text;

import com.mammb.code.editor.model.layout.GlyphRun;
import com.mammb.code.editor.model.layout.HitOn;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.tk.Toolkit;

/**
 * TextLayoutShim.
 * @author Naotsugu Kobayashi
 */
public class TextLayoutShim {

    /** The delegated text layout. */
    private final TextLayout textLayout;


    public TextLayoutShim() {
        this.textLayout = Toolkit.getToolkit().getTextLayoutFactory().createLayout();
    }


    /**
     * Sets the content for the TextLayout. Shorthand for single span text
     * (no rich text).
     * @return returns true is the call modifies the layout internal state.
     */
    public boolean setContent(String string, Object font) {
        return textLayout.setContent(string, font);
    }


    /**
     * Sets the alignment for the TextLayout.
     * @return returns true is the call modifies the layout internal state.
     */
    public boolean setAlignment(TextAlignment alignment) {
        return textLayout.setAlignment(alignment.ordinal());
    }


    /**
     * Sets the wrap width for the TextLayout.
     * @return returns true is the call modifies the layout internal state.
     */
    public boolean setWrapWidth(float wrapWidth) {
        return textLayout.setWrapWidth(wrapWidth);
    }


    /**
     * Sets the line spacing for the TextLayout.
     * @return returns true is the call modifies the layout internal state.
     */
    public boolean setLineSpacing(float spacing) {
        return textLayout.setLineSpacing(spacing);
    }


    /**
     * Sets the tab size for the TextLayout.
     * @param spaces the number of spaces represented by a tab. Default is 8.
     * Minimum is 1, lower values will be clamped to 1.
     * @return returns true if the call modifies the layout internal state.
     */
    public boolean setTabSize(int spaces) {
        return textLayout.setTabSize(spaces);
    }


    /**
     * Returns the GlyphList of text layout.
     * The runs are returned order visually (rendering order), starting from the first line.
     * @return the runs
     */
    public GlyphRun[] getRuns() {
        GlyphList[] runs = textLayout.getRuns();
        return null;
    }


    public HitOn getHitInfo(float x, float y) {
        TextLayout.Hit hit = textLayout.getHitInfo(x, y);
        return new HitOn(
            hit.getCharIndex(),
            hit.getInsertionIndex(),
            hit.isLeading());
    }

}
