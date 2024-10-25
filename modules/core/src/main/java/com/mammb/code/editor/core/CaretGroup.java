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

import com.mammb.code.editor.core.Caret.Range;
import com.mammb.code.editor.core.Caret.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * The caret group.
 * @author Naotsugu Kobayashi
 */
public interface CaretGroup {

    Caret getFirst();
    Caret unique();
    List<Point> points();
    List<Caret> carets();
    List<Range> marked();
    List<Range> ranges();
    boolean hasMarked();
    void at(List<Point> points);
    void add(List<Point> points);
    int size();

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
            return carets.stream().map(Caret::pointFlush).toList();
        }

        @Override
        public List<Caret> carets() {
            return carets;
        }

        @Override
        public List<Range> marked() {
            return carets.stream().filter(Caret::isMarked).map(Caret::markedRange).toList();
        }

        @Override
        public List<Range> ranges() {
            return carets.stream().map(c -> c.isMarked() ? c.markedRange() : new Range(c.point(), c.point())).toList();
        }

        @Override
        public boolean hasMarked() {
            return carets.stream().anyMatch(Caret::isMarked);
        }

        @Override
        public void at(List<Point> points) {
            carets.clear();
            add(points);
        }

        @Override
        public void add(List<Point> points) {
            points.stream().distinct()
                    .forEach(p -> carets.add(Caret.of(p.row(), p.col())));
        }

        @Override
        public int size() {
            return carets.size();
        }
    }

}
