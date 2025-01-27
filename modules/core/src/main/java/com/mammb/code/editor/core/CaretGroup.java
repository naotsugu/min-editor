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

    Caret unique();

    /**
     * Get the caret points.
     * @return the caret points
     */
    List<Point> points();
    List<Caret> carets();
    List<Range> marked();

    /**
     * Get the caret points as range.
     * @return the caret points as range
     */
    List<Range> ranges();
    boolean hasMarked();
    void at(List<Point> points);
    void add(List<Point> points);
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

    class CaretGroupImpl implements CaretGroup {
        private final List<Caret> carets = new ArrayList<>();

        public CaretGroupImpl() {
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
            if (carets.size() <= 1) return;
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
        }

    }

}
