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

import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.model.text.OffsetPoint;
import javafx.scene.canvas.GraphicsContext;

/**
 * Selection.
 * @author Naotsugu Kobayashi
 */
public interface Selection {

    /**
     * Select as fixed.
     * @param start the start point
     * @param end the end point
     */
    void selectOn(OffsetPoint start, OffsetPoint end);


    void selectOn(CaretMulti caret);

    /**
     * Start select.
     * @param point the point of select start
     */
    void selectOn(OffsetPoint point);

    /**
     * Close select.
     * @param point the point of select close
     */
    void closeOn(OffsetPoint point);

    boolean isOpened();

    /**
     * Clear selection.
     */
    void selectOff();

    /**
     * Get the selected char length.
     * @return the selected char length
     */
    long length();

    void draw(GraphicsContext gc, TextRun run, double offsetY, double left);

    /**
     * Get the min select offset.
     * @return the min select offset
     */
    default OffsetPoint min() {
        // TODO
        return OffsetPoint.zero;
    }

    /**
     * Get the max select offset.
     * @return the max select offset
     */
    default OffsetPoint max() {
        // TODO
        return OffsetPoint.zero;
    }

    /**
     * Get the select start offset.
     * @return the select start offset
     */
    default OffsetPoint startOffset() {
        // TODO
        return OffsetPoint.zero;
    }

    /**
     * Gets whether this selection contains the specified rows.
     * @param row the specified rows
     * @return {@code true} if this selection contains the specified rows
     */
    default boolean contains(int row) {
        // TODO
        return false;
    }

}
