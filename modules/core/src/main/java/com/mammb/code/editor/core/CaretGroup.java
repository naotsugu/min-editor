/*
 * Copyright 2023-2026 the original author or authors.
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
package com.mammb.code.editor.core;

import java.util.List;
import com.mammb.code.editor.core.Point.Range;
import com.mammb.code.editor.core.model.CaretGroupImpl;

/**
 * The caret group.
 * @author Naotsugu Kobayashi
 */
public interface CaretGroup {

    /**
     * Get the caret first one.
     * @return the caret first one
     */
    Caret getOne();

    /**
     * Get unique processed caret.
     * @return the caret unique one
     */
    Caret unique();

    /**
     * Get the caret points.
     * @return the caret points
     */
    List<Point> points();

    /**
     * Get the caret list.
     * @return the caret list
     */
    List<Caret> carets();

    /**
     * Get the marked range list.
     * @return the marked range list
     */
    List<Range> marked();

    /**
     * Get the caret points as range.
     * The range has starting and ending range for text selection.
     * @return the caret points as range
     */
    List<Range> ranges();

    /**
     * Get if there is a marked caret.
     * A marked caret is one that has a starting point for text selection
     * @return {@code true}, if there is a marked caret
     */
    boolean hasMarked();

    /**
     * Add the caret at the specified position.
     * This operation deletes the existing caret position.
     * @param points the specified position
     */
    void at(List<Point> points);

    /**
     * Add the caret at the specified position.
     * @param points the specified position
     */
    void add(List<Point> points);

    /**
     * Add the caret at the specified position.
     * @param row the row
     * @param col the col
     * @return the caret
     */
    Caret add(int row, int col);

    /**
     * Adds or removes a caret at the specified point.
     * @param point the specified point
     */
    void toggle(Point point);

    /**
     * Get the count of caret.
     * @return the count of caret
     */
    int size();

    /**
     * Create a new {@link CaretGroup}.
     * @return a new {@link CaretGroup}
     */
    static CaretGroup of() {
        return new CaretGroupImpl();
    }

}
