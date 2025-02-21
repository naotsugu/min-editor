/*
 * Copyright 2023-2025 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;
import com.mammb.code.editor.core.Point.Range;

/**
 * The caret group.
 * @author Naotsugu Kobayashi
 */
public interface CaretGroup {

    /**
     * Get the caret first one.
     * @return the caret first one
     */
    Caret getFirst();

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

    /**
     * The implementation of {@link CaretGroup}.
     */
    class CaretGroupImpl implements CaretGroup {

        /** The carets. */
        private final List<Caret> carets = new ArrayList<>();

        /**
         * Constructor.
         */
        CaretGroupImpl() {
            carets.add(Caret.of());
        }

        @Override
        public Caret getFirst() {
            return carets.getFirst();
        }

        @Override
        public Caret unique() {
            if (carets.size() > 1) {
                carets.subList(1, carets.size()).clear();
            }
            return carets.getFirst();
        }

        @Override
        public List<Point> points() {
            normalize();
            return carets.stream().map(Caret::flushedPoint).toList();
        }

        @Override
        public List<Caret> carets() {
            normalize();
            return carets;
        }

        @Override
        public List<Range> marked() {
            normalize();
            return carets.stream().filter(Caret::isMarked).map(Caret::markedRange).toList();
        }

        @Override
        public List<Range> ranges() {
            normalize();
            return carets.stream().map(Caret::range).toList();
        }

        @Override
        public boolean hasMarked() {
            return carets.stream().anyMatch(Caret::isMarked);
        }

        @Override
        public void at(List<Point> points) {
            carets.clear();
            points.forEach(p -> carets.add(Caret.of(p.row(), p.col())));
            normalize();
        }

        @Override
        public void add(List<Point> points) {
            points.forEach(p -> carets.add(Caret.of(p.row(), p.col())));
            normalize();
        }

        @Override
        public Caret add(int row, int col) {
            var c = Caret.of(row, col);
            carets.add(c);
            return c;
        }

        @Override
        public void toggle(final Point point) {
            if (!carets.removeIf(p -> p.point().compareTo(point) == 0) || size() == 0) {
                carets.add(Caret.of(point.row(), point.col()));
            }
        }

        @Override
        public int size() {
            return carets.size();
        }

        void normalize() {
            if (carets.size() <= 1) {
                return;
            }

            List<Caret> merged = new ArrayList<>();
            for (Caret caret : carets.stream().sorted().toList()) {
                if (merged.isEmpty()) {
                    merged.add(caret);
                    continue;
                }
                if (!merged.getLast().marge(caret)) {
                    merged.add(caret);
                }
            }
            carets.clear();
            carets.addAll(merged);

            if (carets.isEmpty()) {
                carets.add(Caret.of());
            }
        }

    }

}
