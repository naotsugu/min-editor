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
import com.mammb.code.editor.ui.model.LayoutLine;
import com.mammb.code.editor.ui.model.RangeSupplier;
import javafx.scene.canvas.GraphicsContext;
import java.util.List;

/**
 * Caret implementation.
 * @author Naotsugu Kobayashi
 */
public class CaretImpl implements Caret {

    /** The caretLine. */
    private final CaretLine caretLine;

    public CaretImpl(CaretLine caretLine) {
        this.caretLine = caretLine;
    }

    @Override
    public void draw(GraphicsContext gc, double margin, double hScrolled) {
        caretLine.draw(gc, margin, hScrolled);
    }

    @Override
    public void setHide(boolean hide) {
        caretLine.setHide(hide);
    }

    @Override
    public boolean isHide() {
        return caretLine.isHide();
    }

    @Override
    public void refresh() {
        caretLine.refresh();
    }

    @Override
    public void at(long charOffset, boolean syncLogicalX) {
        caretLine.at(charOffset);
    }

    @Override
    public void right() {
        caretLine.right();
    }

    @Override
    public void left() {
        caretLine.left();
    }

    @Override
    public void up() {
        caretLine.up();
    }

    @Override
    public void down() {
        caretLine.down();
    }

    @Override
    public void home() {
        caretLine.home();
    }

    @Override
    public void end() {
        caretLine.end();
    }

    @Override
    public OffsetPoint offsetPoint() {
        return caretLine.offsetPoint();
    }

    @Override
    public long offset() {
        return caretLine.getBar().offset();
    }

    @Override
    public int row() {
        OffsetPoint caretPoint = offsetPoint();
        return caretPoint == null ? 0 : caretPoint.row();
    }

    @Override
    public LayoutLine layoutLine() {
        return caretLine.getLine();
    }

    @Override
    public RangeSupplier selectionRange() {
        final var anchorCaret = new AnchoredCaret(caretLine);
        return () -> List.of(anchorCaret.offsetPointRange());
    }

    @Override
    public String charAt() {
        return caretLine.charAt();
    }

    @Override
    public double x() {
        return caretLine.getBar().x();
    }

    @Override
    public double y() {
        return caretLine.getBar().y();
    }

    @Override
    public double width() {
        return caretLine.getBar().width();
    }

    @Override
    public double height() {
        return caretLine.getBar().height();
    }
}
