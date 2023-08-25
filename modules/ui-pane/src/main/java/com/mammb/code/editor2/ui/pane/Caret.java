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
package com.mammb.code.editor2.ui.pane;

import com.mammb.code.editor2.model.text.OffsetPoint;
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
     * Clear.
     * @param gc the graphics context
     */
    Rect clear(GraphicsContext gc, double left);


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
     * Get the offsetPoint at caret.
     * @return the offsetPoint at caret
     */
    OffsetPoint offsetPoint();

    /**
     * Get the char offset.
     * @return the char offset
     */
    int offset();

    int row();

    double x();

    double y();

    double y2();

    double height();

    double width();

    boolean drawn();

}
