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

/**
 * The caret point.
 * @author Naotsugu Kobayashi
 */
public interface Point extends Comparable<Point> {

    int row();

    int col();

    static Point of(int row, int col) {
        return new PointRec(row, col);
    }

    static <E extends Point> Point of(E point) {
        return new PointRec(point);
    }

    default boolean isZero() {
        return row() == 0 && col() == 0;
    }

    @Override
    default int compareTo(Point that) {
        int c = Integer.compare(this.row(), that.row());
        if (c == 0) {
            return Integer.compare(this.col(), that.col());
        } else {
            return c;
        }
    }

    record PointRec(int row, int col) implements Point {
        PointRec(Point p) { this(p.row(), p.col()); }
    }

    record Range(Point start, Point end) implements Comparable<Range> {
        public Point min() {
            return start.compareTo(end) < 0 ? start : end;
        }
        public Point max() {
            return start.compareTo(end) > 0 ? start : end;
        }

        @Override
        public int compareTo(Range o) {
            return min().compareTo(o.min());
        }
    }

}
