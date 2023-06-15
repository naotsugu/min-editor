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
package com.mammb.code.editor2.model.layout;

import java.util.List;

/**
 * LayoutBuilder.
 * @author Naotsugu Kobayashi
 */
public interface LayoutBuilder {

    /**
     * Add spans.
     * @param spans the adding spans
     */
    void add(List<Span> spans);

    /**
     * Layout.
     * @return the TextLine
     */
    List<TextLine> layout();

    /**
     * Clear.
     */
    void clear();

    /**
     * Get the hit information in the text plane.
     * @param x the specified point x to be tested
     * @param y the specified point y to be tested
     * @return the hit position
     */
    HitPosition hitInfo(double x, double y);

    /**
     * Sets the wrap width for the TextLayout.
     * @return {@code true} is the call modifies the layout internal state.
     */
    boolean setWrapWidth(double wrapWidth);

    /**
     * Sets the line spacing for the TextLayout.
     * @return {@code true} is the call modifies the layout internal state.
     */
    boolean setLineSpacing(double spacing);

    /**
     * Sets the tab size for the TextLayout.
     * @param spaces the number of spaces represented by a tab. Default is 8.
     * Minimum is 1, lower values will be clamped to 1.
     * @return {@code true} if the call modifies the layout internal state.
     */
    boolean setTabSize(int spaces);

}
