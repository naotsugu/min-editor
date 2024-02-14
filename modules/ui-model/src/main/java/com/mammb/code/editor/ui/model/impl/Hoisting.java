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
package com.mammb.code.editor.ui.model.impl;

import com.mammb.code.editor.model.text.OffsetPointRange;
import com.mammb.code.editor.ui.model.Caret;
import com.mammb.code.editor.ui.model.CaretMulti;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hoisting.
 * @author Naotsugu Kobayashi
 */
public class Hoisting<E> {

    private final Point<E> main;

    private final List<Point<E>> points;


    private Hoisting(Point<E> main, List<Point<E>> points) {
        this.main = main;
        this.points = points;
    }


    public static Hoisting<OffsetPointRange> rangeOf(List<OffsetPointRange> ranges) {
        List<Point<OffsetPointRange>> points = ranges.stream()
            .map(range -> new Point<>(range, range.minOffsetPoint().offset()))
            .collect(Collectors.toList());
        Point<OffsetPointRange> main = points.getFirst();
        points.sort(Collections.reverseOrder());
        return new Hoisting<>(main, points);
    }


    public static Hoisting<Caret> caretOf(List<Caret> carets) {
        List<Point<Caret>> points = carets.stream()
            .map(range -> new Point<>(range, range.offsetPoint().offset()))
            .collect(Collectors.toList());
        Point<Caret> main = points.getFirst();
        points.sort(Collections.reverseOrder());
        return new Hoisting<>(main, points);
    }


    public List<E> points() {
        return points.stream().map(Point::value).toList();
    }


    public void shift(long offset, int delta) {
        for (Point<E> point : points) {
            if (point.offset > offset) {
                point.shift(delta);
            }
        }
    }

    public void shiftOn(long offset, int delta) {
        for (Point<E> point : points) {
            if (point.offset >= offset) {
                point.shift(delta);
            }
        }
    }

    public void locateOn(CaretMulti carets) {
        locateOn(carets, 0);
    }

    public void locateOn(CaretMulti carets, int delta) {
        carets.clear();
        carets.at(main.shifted() + delta, true);
        points.stream().filter(p -> p != main).mapToLong(Point::shifted).forEach(n -> carets.add(n + delta));
    }

    static class Point<E> implements Comparable<Point<E>> {

        private final E value;
        private final long offset;
        private long shifted;

        public Point(E value, long offset) {
            this.value = value;
            this.offset = this.shifted = offset;
        }

        public void shift(long delta) {
            shifted += delta;
        }

        public E value() {
            return value;
        }

        public long shifted() {
            return shifted;
        }

        @Override
        public int compareTo(Point<E> o) {
            return Long.compare(offset, o.offset);
        }
    }

}
