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
import com.mammb.code.editor.model.text.OffsetPointRange;
import java.util.List;

/**
 * Selection.
 * @author Naotsugu Kobayashi
 */
public interface Selection extends RangeSupplier {

    /**
     * Select as fixed.
     * @param start the start point
     * @param end the end point
     */
    void selectOn(OffsetPoint start, OffsetPoint end);

    /**
     * Start select at the caret position.
     * @param caret the source caret position
     */
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

    boolean hasSelection();

    /**
     * Get the selected char length.
     * @return the selected char length
     */
    long length();

    /**
     * Draw selection
     * @param gc the graphic context
     * @param run the text run
     * @param offsetY the offset y
     * @param left the left position
     */
    void draw(GraphicsContext gc, TextRun run, double offsetY, double left);

    @Override
    List<OffsetPointRange> getRanges();

}
