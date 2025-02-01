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

import java.util.Objects;

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

        record PointRecord(int row, int col) implements Point {

            @Override
            public boolean equals(Object object) {
                if (!(object instanceof Point that)) return false;
                return row == that.row() && col == that.col();
            }

            @Override
            public int hashCode() {
                return Objects.hash(row, col);
            }
        }

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

    /**
     * Get whether this point is zero or not.
     * @return {@code true} if this point is zero
     */
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

    /**
     * The range of points.
     * @param start the start point
     * @param end the end point
     */
    record Range(Point start, Point end) implements Comparable<Range> {

        /**
         * Get the min point.
         * @return the min point
         */
        public Point min() {
            return start.compareTo(end) < 0 ? start : end;
        }

        /**
         * Get the max point.
         * @return the max point
         */
        public Point max() {
            return start.compareTo(end) > 0 ? start : end;
        }

        /**
         * Get whether the specified point is included in this range.
         * @param point the specified point
         * @return {@code true}, if the specified point is included in this range
         */
        public boolean contains(Point point) {
            return min().compareTo(point) <= 0 && max().compareTo(point) >= 0;
        }

        /**
         * Get whether this range starts and ends in the ascending direction
         * @return {@code true}, if this range starts and ends in the ascending direction
         */
        public boolean isAsc() {
            return start.compareTo(end) >= 0;
        }

        @Override
        public int compareTo(Range o) {
            return min().compareTo(o.min());
        }
    }

    /**
     * The text with point.
     * @param point the point
     * @param text the text
     */
    record PointText(Point point, String text) {}

}
