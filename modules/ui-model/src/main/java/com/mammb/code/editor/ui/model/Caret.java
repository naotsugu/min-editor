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
package com.mammb.code.editor.ui.model;

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.OffsetPointer;
import javafx.scene.canvas.GraphicsContext;

/**
 * Caret.
 * @author Naotsugu Kobayashi
 */
public interface Caret extends OffsetPointer, Rect {

    /**
     * Draw caret.
     * @param gc the graphics context
     * @param margin the left position offset
     * @param hScrolled the size of horizontal scroll
     */
    void draw(GraphicsContext gc, double margin, double hScrolled);

    /**
     * Set the hide flag.
     * @param hide the hide flag
     */
    void setHide(boolean hide);

    /**
     * Get the hide flag.
     * @return the hide flag
     */
    boolean isHide();

    /**
     * Refresh the caret.
     */
    void refresh();

    /**
     * Moves the caret to the specified offset.
     * @param charOffset the char offset
     * @param syncLogicalX whether to synchronize x position
     */
    void at(long charOffset, boolean syncLogicalX);

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
     * Move the caret to the home.
     */
    void home();

    /**
     * Move the caret to the end.
     */
    void end();

    @Override
    OffsetPoint offsetPoint();

    /**
     * Get the char offset.
     * @return the char offset
     */
    long offset();

    /**
     * Get the row number(zero origin) of the row where the caret exists.
     * @return the row number
     */
    int row();

    /**
     * Get the layoutLine at caret.
     * @return the layoutLine
     */
    LayoutLine layoutLine();

    /**
     * Get the selection range supplier.
     * @return the selection range supplier
     */
    RangeSupplier selectionRange();

    /**
     * Get the char string at this caret.
     * @see com.mammb.code.editor.model.layout.TextLine#charStringAt(long)
     * @return the char string
     */
    String charAt();

    @Override
    default int compareTo(OffsetPointer o) {
        if (o instanceof Caret caret) {
            return Long.compare(offset(), caret.offset());
        } else {
            return offsetPoint().compareTo(o.offsetPoint());
        }
    }
}
