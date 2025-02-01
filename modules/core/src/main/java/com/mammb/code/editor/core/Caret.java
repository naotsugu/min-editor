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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import com.mammb.code.editor.core.Point.Range;

/**
 * The Caret.
 * Represents a caret on the content.
 * <p>
 * Treats the starting point of a text selection as a mark.
 * Treats the caret as floating during mouse dragging
 * The caret position is treated as a flush during the IME conversion process.
 * @author Naotsugu Kobayashi
 */
public interface Caret extends Comparable<Caret> {

    /**
     * Get the caret point.
     * @return the caret point
     */
    Point point();

    /**
     * Set the caret position.
     * @param row the number of row
     * @param col the number of column
     */
    void at(int row, int col);

    /**
     * Set the caret position with virtual position.
     * @param row the number of row
     * @param col the number of column
     * @param vPos virtual x-coordinate position of the caret
     */
    void at(int row, int col, double vPos);

    /**
     * Set the caret at the specified position and also sets it floating.
     * @param row the number of row
     * @param col the number of column
     */
    void floatAt(int row, int col);

    /**
     * Set the caret at the specified position and also sets it flush.
     * @param row the number of row
     * @param col the number of column
     */
    void imeFlushAt(int row, int col);
    void markTo(int markRow, int markCol, int row, int col);
    boolean hasImeFlush();

    /**
     * Mark the current caret position.
     */
    void mark();

    /**
     * Get the marked point.
     * @return the marked point
     */
    Point markedPoint();

    /**
     * Get the flushed point.
     * @return the flushed point
     */
    Point flushedPoint();

    boolean marge(Caret other);

    /**
     * Clear marked point.
     */
    void clearMark();

    /**
     * Clear floating.
     */
    void clearFloat();

    /**
     * Clear ime flush.
     */
    void clearImeFlush();

    /**
     * Get whether it is marked or not.
     * @return {@code true}, if it is marked
     */
    boolean isMarked();

    /**
     * Get whether it is floating or not.
     * @return {@code true}, if it is floating
     */
    boolean isFloating();

    /**
     * Get the caret virtual position.
     * @return the caret virtual position
     */
    double vPos();

    default Range markedRange() {
        return isMarked() ? new Range(markedPoint(), Point.of(point())) : null;
    }
    default Range range() {
        return isMarked() ? markedRange() : new Range(point(), point());
    }
    default int direction() {
        return !isMarked() ? 0 : point().compareTo(markedPoint());
    }
    default int row() {
        return point().row();
    }
    default int col() {
        return point().col();
    }
    default void at(Point point) {
        at(point.row(), point.col());
    }
    default void imeFlushAt(Point point) {
        imeFlushAt(point.row(), point.col());
    }
    default void markTo(Point m, Point p) {
        markTo(m.row(), m.col(), p.row(), p.col());
    }
    default boolean isZero() {
        return point().isZero();
    }
    default void markIf(boolean withSelect) {
        if (!withSelect) {
            clearMark();
        } else if (!isMarked()) {
            mark();
        }
    }

    /**
     * Create a new {@link Caret}.
     * @return a new {@link Caret}.
     */
    static Caret of() {
        return new CaretImpl();
    }

    /**
     * Create a new {@link Caret} by specifying its position on the content.
     * @param row the number of row
     * @param col the number of column
     * @return a new {@link Caret}.
     */
    static Caret of(int row, int col) {
        return new CaretImpl(row, col);
    }

    /**
     * The implementation of {@link Caret}.
     */
    class CaretImpl implements Caret {

        private final PointMut point;
        private Point mark;
        private double vPos = -1;
        private boolean floating;
        private Point flush;

        public CaretImpl() {
            this.point = new PointMut(0, 0);
        }

        public CaretImpl(int row, int col) {
            this.point = new PointMut(row, col);
        }

        @Override
        public void mark() {
            mark = new PointMut(point.row(), point.col());
        }

        @Override
        public Point markedPoint() {
            return mark;
        }

        @Override
        public boolean marge(Caret that) {
            int self = direction();
            int other = that.direction();
            if (self == 0 && other == 0) {
                return point.compareTo(that.point()) == 0;
            } else if (self == 0) {
                if (that.range().contains(point())) {
                    markTo(that.markedPoint(), that.point());
                    return true;
                }
                return false;
            } else if (other == 0) {
                return range().contains(that.point());
            } else if (self == other) {
                var r = range();
                if (r.contains(that.point()) || r.contains(that.markedPoint())) {
                    if (self > 0) {
                        markTo(
                            Collections.min(List.of(markedPoint(), that.markedPoint())),
                            Collections.max(List.of(point(), that.point())));
                    } else {
                        markTo(
                            Collections.max(List.of(markedPoint(), that.markedPoint())),
                            Collections.min(List.of(point(), that.point())));
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public void clearMark() {
            mark = null;
        }

        @Override
        public boolean isMarked() {
            return mark != null;
        }

        @Override
        public boolean isFloating() {
            return floating;
        }

        @Override
        public double vPos() {
            return vPos;
        }

        @Override
        public int compareTo(Caret that) {
            return this.point().compareTo(that.point());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Caret caret = (Caret) o;
            return point.compareTo(caret.point()) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(point);
        }

        @Override
        public Point point() {
            return point;
        }

        @Override
        public void at(int row, int col) {
            point.at(row, col);
            vPos = -1;
            flush = null;
        }

        @Override
        public void at(int row, int col, double x) {
            at(row, col);
            vPos = x;
        }

        @Override
        public void floatAt(int row, int col) {
            at(row, col);
            floating = true;
        }

        @Override
        public void imeFlushAt(int row, int col) {
            flush = Point.of(row, col);
        }

        @Override
        public void markTo(int markRow, int markCol, int row, int col) {
            mark = new PointMut(markRow, markCol);
            at(row, col);
        }

        @Override
        public void clearFloat() {
            floating = false;
        }

        @Override
        public void clearImeFlush() {
            flush = null;
        }

        @Override
        public boolean hasImeFlush() {
            return flush != null;
        }

        @Override
        public Point flushedPoint() {
            return hasImeFlush() ? flush :  point;
        }
    }

    /**
     * The implementation of Point as mutable.
     */
    class PointMut implements Point {

        private int row, col;

        PointMut(int row, int col) {
            this.row = row;
            this.col = col;
        }

        void at(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int row() {
            return row;
        }

        public int col() {
            return col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Point point)) return false;
            return row == point.row() && col == point.col();
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", PointMut.class.getSimpleName() + "[", "]")
                    .add("row=" + row)
                    .add("col=" + col)
                    .toString();
        }
    }

}
