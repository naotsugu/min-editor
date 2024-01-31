package com.mammb.code.editor.ui.model.impl;

import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.ui.model.CaretMulti;
import com.mammb.code.editor.ui.model.LayoutLine;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
    public OffsetPoint caretPoint() {
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
        OffsetPoint caretPoint = caretPoint();
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

    }

    @Override
    public void clear() {
        moons.clear();
    }

}
