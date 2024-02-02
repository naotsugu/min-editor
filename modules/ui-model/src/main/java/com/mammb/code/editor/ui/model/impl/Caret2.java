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

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.ui.model.CaretMulti;
import com.mammb.code.editor.ui.model.LayoutLine;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Caret implementation.
 * @author Naotsugu Kobayashi
 */
public class Caret2 implements CaretMulti {

    /** The main caret. */
    private final CaretLine main;

    /** The planets. */
    private final List<CaretLine> moons = new ArrayList<>();

    public Caret2(CaretLine main) {
        this.main = main;
    }

    /**
     * Constructor.
     * @param offsetToLine the offset to layout line function
     */
    public Caret2(Function<Long, LayoutLine> offsetToLine) {
        this(CaretLine.of(offsetToLine));
    }


    @Override
    public void draw(GraphicsContext gc, double margin, double hScrolled) {
        main.draw(gc, margin, hScrolled);
        moons.forEach(c -> c.draw(gc, margin, hScrolled));
    }

    @Override
    public void markDirty() {
        main.refresh();
        moons.forEach(CaretLine::refresh);
    }

    @Override
    public void at(long charOffset, boolean syncLogicalX) {
        main.at(charOffset);
    }

    @Override
    public void right() {
        main.right();
        moons.forEach(CaretLine::right);
    }

    @Override
    public void left() {
        main.left();
        moons.forEach(CaretLine::left);
    }

    @Override
    public void up() {
        main.up();
        moons.forEach(CaretLine::up);
    }

    @Override
    public void down() {
        main.down();
        moons.forEach(CaretLine::down);
    }

    @Override
    public void home() {
        main.home();
        moons.forEach(CaretLine::home);
    }

    @Override
    public void end() {
        main.end();
        moons.forEach(CaretLine::end);
    }

    @Override
    public OffsetPoint offsetPoint() {
        if (offset() == 0) {
            return OffsetPoint.zero;
        }
        LayoutLine line = layoutLine();
        return (line == null) ? null : line.offsetPoint(offset());
    }

    @Override
    public long offset() {
        return main.getBar().offset();
    }

    @Override
    public int row() {
        OffsetPoint caretPoint = offsetPoint();
        return caretPoint == null ? 0 : caretPoint.row();
    }

    @Override
    public double x() {
        return main.getBar().x();
    }

    @Override
    public double y() {
        return main.getBar().y();
    }

    @Override
    public double y2() {
        return main.getBar().bottom();
    }

    @Override
    public double height() {
        return main.getBar().h();
    }

    @Override
    public double width() {
        return main.getBar().w();
    }

    @Override
    public LayoutLine layoutLine() {
        return main.getLine();
    }

    @Override
    public void add(long charOffset) {
        if (main.getBar().offset() == charOffset ||
            moons.stream().anyMatch(m -> m.getBar().offset() == charOffset)) {
            return;
        }
        moons.add(main.cloneAt(charOffset));
    }

    @Override
    public void clear() {
        moons.clear();
    }

    CaretSelections caretSelections() {
        return CaretSelections.of(moons);
    }

}
