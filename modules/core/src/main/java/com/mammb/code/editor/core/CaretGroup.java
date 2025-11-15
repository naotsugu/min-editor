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
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
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

    /**
     * The implementation of {@link CaretGroup}.
     */
    class CaretGroupImpl implements CaretGroup {

        /** The primary caret. */
        private Caret primary;

        /** The carets. */
        private final List<Caret> carets = new ArrayList<>();

        /**
         * Constructor.
         */
        CaretGroupImpl() {
            primary = Caret.of();
            carets.add(primary);
        }

        @Override
        public Caret getOne() {
            return primary;
        }

        @Override
        public Caret unique() {
            carets.clear();
            carets.add(primary);
            return primary;
        }

        @Override
        public List<Point> points() {
            normalize();
            return carets.stream().map(Caret::flushedPoint).toList();
        }

        @Override
        public List<Caret> carets() {
            normalize();
            return Collections.unmodifiableList(carets);
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
            if (points.isEmpty()) return;
            carets.clear();
            points.forEach(p -> carets.add(Caret.of(p.row(), p.col())));
            primary = carets.getFirst();
            normalize();
        }

        @Override
        public void add(List<Point> points) {
            points.forEach(p -> add(p.row(), p.col()));
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

            Predicate<Caret> match = (Caret c) -> c.point().compareTo(point) == 0;
            boolean removed = carets.removeIf(match);
            if (!removed) {
                // toggle on
                carets.add(Caret.of(point.row(), point.col()));
                return;
            }

            if (carets.isEmpty()) {
                primary = Caret.of(point.row(), point.col());
                carets.add(primary);
                return;
            }

            if (match.test(primary)) {
                primary = carets.getFirst();
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
                primary = Caret.of();
                carets.add(primary);
            }

            if (!carets.contains(primary)) {
                primary = carets.stream().filter(c -> primary.row() == c.row()).findFirst().orElse(carets.getFirst());
            }
        }

    }

}
