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
package com.mammb.code.editor.ui.pane;

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.ui.pane.impl.LayoutLine;
import javafx.scene.canvas.GraphicsContext;

/**
 * Caret.
 * @author Naotsugu Kobayashi
 */
public interface Caret {

    /**
     * Draw caret.
     * @param gc the graphics context
     * @param margin the left position offset
     * @param hScrolled the size of horizontal scroll
     */
    void draw(GraphicsContext gc, double margin, double hScrolled);

    /**
     * Mark the caret to dirty.
     */
    void markDirty();

    /**
     * Moves the caret to the specified offset.
     * @param charOffset the char offset
     * @param syncLogicalX whether to synchronize x position
     */
    void at(int charOffset, boolean syncLogicalX);

    /**
     * Move the caret to the right.
     */
    void right();

    /**
     * Move the caret to the left.
     */
    void left();

    /**
     * Move the caret to the up.
     */
    void up();

    /**
     * Move the caret to the down.
     */
    void down();

    /**
     * Get the point at caret.
     * @return the point at caret
     */
    CaretPoint caretPoint();

    /**
     * Get the char offset.
     * @return the char offset
     */
    int offset();

    /**
     * Get the row number(zero origin) of the row where the caret exists.
     * @return the row number
     */
    int row();

    /**
     * Get the x position of the row where the caret exists.
     * @return the x position
     */
    double x();

    /**
     * Get the y position of the row where the caret exists.
     * @return the y position
     */
    double y();

    /**
     * Get the bottom position of the row where the caret exists.
     * @return the bottom position
     */
    double y2();

    /**
     * Get the caret height.
     * @return the caret height
     */
    double height();

    /**
     * Get the caret width.
     * @return the caret width
     */
    double width();

    /**
     * Get whether the caret is drawn or not
     * @return
     */
    boolean drawn();

    /**
     * Switches to undrawn if already drawn.
     * @return whether it has been drawn or not
     */
    boolean flipIfDrawn();

    /**
     * Get the layoutLine at caret.
     * @return the layoutLine
     */
    LayoutLine layoutLine();

    record CaretPoint(OffsetPoint head, OffsetPoint current) {}
}
