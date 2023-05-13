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

import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.scene.text.TextLine;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Layout utilities.
 * @author Naotsugu Kobayashi
 */
public class Layout {

    /** The delegated text layout. */
    private final TextLayout textLayout;

    /** The base font. */
    private final Font baseFont;

    /** The native font map. */
    private final Map<Font, Object> nativeFonts = new HashMap<>();

    /** The wrap width for the layout. */
    private float wrapWidth;


    /**
     * Constructor.
     * @param font the base font
     * @param wrapWidth the wrap width for the layout
     * @param tabSize the tab size for the layout
     */
    private Layout(Font font, float wrapWidth, int tabSize) {
        this.textLayout = Toolkit.getToolkit().getTextLayoutFactory().createLayout();
        this.textLayout.setWrapWidth(wrapWidth);
        this.textLayout.setTabSize(tabSize);
        this.baseFont = Objects.requireNonNull(font);
        this.nativeFonts.put(baseFont, FontHelper.getNativeFont(font));
        this.wrapWidth = wrapWidth;
    }


    /**
     * Create a new Layout.
     * @param font the base font
     * @param wrapWidth the wrap width for the layout
     * @return the created Layout
     */
    public static Layout of(Font font, float wrapWidth) {
        return of(font, wrapWidth, 4);
    }


    /**
     * Create a new Layout.
     * @param font the base font
     * @param wrapWidth the wrap width for the layout
     * @param tabSize the tab size for the layout
     * @return the created Layout
     */
    public static Layout of(Font font, float wrapWidth, int tabSize) {
        return new Layout(font, wrapWidth, tabSize);
    }


    /**
     * Get the layout line text
     * @param str the source string
     * @return the layout line text
     */
    public String[] lines(String str) {
        return lines(str, nativeFonts.get(baseFont));
    }


    /**
     * Get the layout line text
     * @param str the source string
     * @param font the font
     * @return the layout line text
     */
    public String[] lines(String str, Font font) {
        return lines(str, nativeFonts.computeIfAbsent(font, FontHelper::getNativeFont));
    }


    /**
     * Get the layout line text
     * @param str the source string
     * @param font the native font
     * @return the layout line text
     */
    private String[] lines(String str, Object font) {

        if (wrapWidth <= 0) {
            return new String[] { str };
        }

        textLayout.setContent(str, font);
        TextLine[] textLines = textLayout.getLines();

        String[] ret = new String[textLines.length];
        if (textLines.length == 1) {
            ret[0] = str;
            return ret;
        }

        int index = 0;
        for (int i = 0; i < textLines.length; i++ ) {
            int len = textLines[i].getLength();
            ret[i] = str.substring(index, index + len);
            index += len;
        }
        return ret;
    }


    /**
     * Sets the wrap width for the layout.
     * @return {@code true} is the call modifies the layout internal state.
     */
    public boolean setWrapWidth(float wrapWidth) {
        this.wrapWidth = wrapWidth;
        return textLayout.setWrapWidth(wrapWidth);
    }


    /**
     * Sets the line spacing for the layout.
     * @return {@code true} is the call modifies the layout internal state.
     */
    public boolean setLineSpacing(float spacing) {
        return textLayout.setLineSpacing(spacing);
    }


    /**
     * Sets the tab size for the layout.
     * @param spaces the number of spaces represented by a tab. Default is 8.
     * Minimum is 1, lower values will be clamped to 1
     * @return {@code true} if the call modifies the layout internal state
     */
    public boolean setTabSize(int spaces) {
        return textLayout.setTabSize(spaces);
    }

}
