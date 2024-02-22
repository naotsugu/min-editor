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
import com.mammb.code.editor.ui.model.Caret;
import com.mammb.code.editor.ui.model.CaretMulti;
import com.mammb.code.editor.ui.model.LayoutLine;
import com.mammb.code.editor.ui.model.RangeSupplier;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Caret implementation.
 * @author Naotsugu Kobayashi
 */
public class CaretMultiImpl implements CaretMulti {

    /** The main caret line. */
    private final CaretLine main;

    /** The planets. */
    private final List<CaretLine> moons = new ArrayList<>();


    /**
     * Constructor.
     * @param main the main caret line
     */
    public CaretMultiImpl(CaretLine main) {
        this.main = main;
    }


    /**
     * Constructor.
     * @param offsetToLine the offset to layout line function
     */
    public CaretMultiImpl(Function<Long, LayoutLine> offsetToLine) {
        this(CaretLine.of(offsetToLine));
    }


    @Override
    public void draw(GraphicsContext gc, double margin, double hScrolled) {
        main.draw(gc, margin, hScrolled);
        moons.forEach(c -> c.draw(gc, margin, hScrolled));
    }

    @Override
    public void setHide(boolean hide) {
        main.setHide(hide);
        moons.forEach(c -> c.setHide(hide));
    }

    @Override
    public boolean isHide() {
        return main.isHide();
    }

    @Override
    public void refresh() {
        main.refresh();
        moons.forEach(CaretLine::refresh);
    }

    @Override
    public void at(long charOffset, boolean syncLogicalX) {
        main.at(charOffset);
        moons.clear();
    }

    @Override
    public void right() {
        main.right();
        moons.forEach(CaretLine::right);
        unique();
    }

    @Override
    public void left() {
        main.left();
        moons.forEach(CaretLine::left);
        unique();
    }

    @Override
    public void up() {
        main.up();
        moons.forEach(CaretLine::up);
        unique();
    }

    @Override
    public void down() {
        main.down();
        moons.forEach(CaretLine::down);
        unique();
    }

    @Override
    public void home() {
        main.home();
        moons.forEach(CaretLine::home);
        unique();
    }

    @Override
    public void end() {
        main.end();
        moons.forEach(CaretLine::end);
        unique();
    }

    @Override
    public OffsetPoint offsetPoint() {
        return main.offsetPoint();
    }

    @Override
    public long offset() {
        return main.offset();
    }

    @Override
    public int row() {
        return main.row();
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
    public double bottom() {
        return main.getBar().bottom();
    }

    @Override
    public double height() {
        return main.getBar().height();
    }

    @Override
    public double width() {
        return main.getBar().width();
    }

    @Override
    public LayoutLine layoutLine() {
        return main.getLine();
    }

    @Override
    public void add(long charOffset) {
        if (main.offset() == charOffset ||
            moons.stream().anyMatch(m -> m.offset() == charOffset)) {
            return;
        }
        moons.add(main.cloneAt(charOffset));
    }

    @Override
    public void clear() {
        moons.clear();
    }


    @Override
    public List<Caret> list() {
        List<Caret> list = new ArrayList<>();
        list.add(new CaretImpl(main));
        moons.forEach(l -> list.add(new CaretImpl(l)));
        return list;
    }

    @Override
    public RangeSupplier selectionRange() {
        return new AnchoredCaretMulti(main, moons);
    }

    @Override
    public String charAt() {
        return main.charAt();
    }


    /**
     * Remove carets with the same offset to make them unique.
     */
    private void unique() {
        if (moons.isEmpty()) {
            return;
        }
        Set<Long> set = new HashSet<>();
        set.add(main.offset());
        moons.removeIf(m -> !set.add(m.offset()));
        moons.removeIf(CaretLine::isOffScreen);
    }

}
