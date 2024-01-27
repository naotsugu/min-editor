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
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * CaretCore.
 * @author Naotsugu Kobayashi
 */
public class CaretCore implements CaretMulti {

    /** The core caret. */
    private final Caret core;

    /** The planets. */
    private final List<Caret> planets;


    /**
     * Constructor.
     * @param core the core caret
     * @param planets the planets
     */
    private CaretCore(Caret core, List<Caret> planets) {
        this.core = core;
        this.planets = planets;
    }


    /**
     * Create a new CaretMulti.
     * @param offsetToLine the offset to layout line function
     * @return a new CaretMulti
     */
    public static CaretMulti of(Function<Long, LayoutLine> offsetToLine) {
        return new CaretCore(
            new CaretImpl(offsetToLine),
            new ArrayList<>()
        );
    }


    @Override
    public void draw(GraphicsContext gc, double margin, double hScrolled) {
        core.draw(gc, margin, hScrolled);
        planets.forEach(c -> c.draw(gc, margin, hScrolled));
    }


    @Override
    public void markDirty() {
        core.markDirty();
        planets.forEach(Caret::markDirty);
    }


    @Override
    public void at(long charOffset, boolean syncLogicalX) {
        core.at(charOffset, syncLogicalX);
    }


    @Override
    public void right() {
        core.right();
        planets.forEach(Caret::right);
    }

    @Override
    public void left() {
        core.left();
        planets.forEach(Caret::left);
    }


    @Override
    public void up() {
        core.up();
        planets.forEach(Caret::up);
    }


    @Override
    public void down() {
        core.down();
        planets.forEach(Caret::down);
    }


    @Override
    public OffsetPoint caretPoint() {
        return core.caretPoint();
    }


    @Override
    public long offset() {
        return core.offset();
    }


    @Override
    public int row() {
        return core.row();
    }


    @Override
    public double x() {
        return core.x();
    }


    @Override
    public double y() {
        return core.y();
    }


    @Override
    public double y2() {
        return core.y2();
    }


    @Override
    public double height() {
        return core.height();
    }


    @Override
    public double width() {
        return core.width();
    }


    @Override
    public boolean drawn() {
        return core.drawn();
    }


    @Override
    public boolean flipIfDrawn() {
        return core.flipIfDrawn();
    }


    @Override
    public LayoutLine layoutLine() {
        return core.layoutLine();
    }


    @Override
    public boolean isOutOfLines() {
        return core.isOutOfLines();
    }

}
