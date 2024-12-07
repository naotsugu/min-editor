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

    /**
     * Get the number of row.
     * @return the number of row
     */
    int row();

    /**
     * Get the number of column.
     * @return the number of column
     */
    int col();

    /**
     * Create a new caret point.
     * @param row the number of row
     * @param col the number of column
     * @return the new caret point
     */
    static Point of(int row, int col) {
        record PointRecord(int row, int col) implements Point { }
        return new PointRecord(row, col);
    }

    /**
     * Create a new caret point from the other point.
     * @param p the other point
     * @return a new caret point
     * @param <E> the type of other point
     */
    static <E extends Point> Point of(E p) {
        return of(p.row(), p.col());
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

    record PointText(Point point, String text) {}

}
