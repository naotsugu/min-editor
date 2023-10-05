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
     * Start select.
     * @param offset the point of select start
     */
    void start(OffsetPoint offset);

    /**
     * Move select to.
     * @param toOffset the point of select to
     */
    void to(OffsetPoint toOffset);

    /**
     * Start dragging.
     * @param offset the point of dragging select start
     */
    void startDragging(OffsetPoint offset);

    /**
     * End dragging.
     */
    void endDragging();

    /**
     * Clear selection.
     */
    void clear();

    /**
     * Get the select start offset.
     * @return the select start offset
     */
    OffsetPoint startOffset();

    /**
     * Get the select end offset.
     * @return the select end offset
     */
    OffsetPoint endOffset();

    /**
     * Gets whether the select has been started or not.
     * @return {@code true} if the select has been started
     */
    boolean started();

    /**
     * Gets whether the dragging select has been started or not.
     * @return {@code true} if the dragging select has been started
     */
    boolean isDragging();

    /**
     * Draw the selection.
     * @param gc the graphics context
     * @param run the text run
     * @param offsetY the position y
     * @param left the left position of run(margin included)
     */
    void draw(GraphicsContext gc, TextRun run, double offsetY, double left);

    /**
     * Get the selected char length.
     * @return the selected char length
     */
    default int length() {
        return started() ? max().offset() - min().offset() : 0;
    }

    /**
     * Get the min select offset.
     * @return the min select offset
     */
    default OffsetPoint min() {
        return (startOffset().offset() <= endOffset().offset()) ? startOffset() : endOffset();
    }

    /**
     * Get the max select offset.
     * @return the max select offset
     */
    default OffsetPoint max() {
        return (startOffset().offset() <= endOffset().offset()) ? endOffset() : startOffset();
    }

    /**
     * Gets whether this selection contains the specified rows.
     * @param row the specified rows
     * @return {@code true} if this selection contains the specified rows
     */
    default boolean contains(int row) {
        if (length() <= 0) {
            return false;
        }
        return min().row() <= row && row <= max().row();
    }

}
