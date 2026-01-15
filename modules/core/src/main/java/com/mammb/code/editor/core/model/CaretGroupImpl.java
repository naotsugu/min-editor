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
package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Caret;
import com.mammb.code.editor.core.CaretAlterGroup;
import com.mammb.code.editor.core.CaretGroup;
import com.mammb.code.editor.core.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * The implementation of {@link CaretGroup}.
 * @author Naotsugu Kobayashi
 */
public class CaretGroupImpl implements CaretGroup {

    /** The primary caret. */
    private Caret primary;

    /** The carets. */
    private final List<Caret> carets = new ArrayList<>();

    /** The CaretAlterGroup. */
    private CaretAlterGroup alterGroup;

    /**
     * Constructor.
     */
    public CaretGroupImpl() {
        primary = Caret.of();
        carets.add(primary);
    }

    @Override
    public Caret getPrimaryOne() {
        return primary;
    }

    @Override
    public Caret unique() {
        carets.clear();
        carets.add(primary);
        alterGroup = null;
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
    public List<Point.Range> marked() {
        normalize();
        return carets.stream().filter(Caret::isMarked).map(Caret::markedRange).toList();
    }

    @Override
    public List<Point.Range> ranges() {
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
        alterGroup = null;
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

    @Override
    public CaretAlterGroup alterGroup() {
        var ret = alterGroup;
        if (ret == null) {
            ret = alterGroup = new CaretAlterGroupImpl(carets);
        }
        return ret;
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
