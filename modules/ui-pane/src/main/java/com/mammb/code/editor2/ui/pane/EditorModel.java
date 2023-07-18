/*
 * Copyright 2019-2023 the original author or authors.
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
package com.mammb.code.editor2.ui.pane;

import com.mammb.code.editor.javafx.layout.FxFontMetrics;
import com.mammb.code.editor.javafx.layout.FxFontStyle;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.style.StylingTranslate;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.ui.pane.impl.SelectionTranslate;
import com.mammb.code.editor2.ui.pane.impl.SelectionsImpl;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.nio.file.Path;

/**
 * EditorModel.
 * @author Naotsugu Kobayashi
 */
public class EditorModel {

    /** The text buffer. */
    private final TextBuffer<Textual> editBuffer;
    /** The selection. */
    private final Selections selections;
    /** The screen width. */
    private final double width;
    /** The screen height. */
    private final double height;
    /** The caret. */
    private final Caret caret;
    /** The text list. */
    private TextList texts;


    /**
     * Constructor.
     * @param width the screen width
     * @param height the screen height
     */
    public EditorModel(double width, double height) {
        this(width, height, null);
    }


    /**
     * Constructor.
     * @param width the screen width
     * @param height the screen height
     * @param path the path
     */
    public EditorModel(double width, double height, Path path) {
        this.editBuffer = TextBuffer.editBuffer(screenRowSize(height), path);
        this.selections = new SelectionsImpl();
        this.texts = new LinearTextList(editBuffer,
            StylingTranslate.passThrough().compound(new SelectionTranslate(selections)));
        this.caret = new Caret(texts::layoutLine);
        this.width = width;
        this.height = height;
    }


    /**
     * Draw the screen.
     * @param gc the GraphicsContext
     */
    public void draw(GraphicsContext gc) {
        Canvas canvas = gc.getCanvas();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.save();
        gc.setTextBaseline(VPos.CENTER);
        double offsetY = 0;
        for (TextLine line : texts.lines()) {
            for (TextRun run : line.runs()) {
                if (run.style().background() instanceof Color bg && !bg.equals(Color.TRANSPARENT)) {
                    gc.setFill(bg);
                    gc.fillRect(run.layout().x(), offsetY, run.layout().width(), line.height());
                }
                if (run.style().font() instanceof Font font) gc.setFont(font);
                if (run.style().color() instanceof Color color) gc.setFill(color);
                gc.fillText(run.text(), run.layout().x(), offsetY + run.baseline());
            }
            offsetY += line.height() + 1;
        }
        caret.draw(gc);
        gc.restore();
    }


    /**
     * Draw the screen partly.
     * @param gc the GraphicsContext
     * @param x the x position of dirty area
     * @param y the y position of dirty area
     * @param w the width of dirty area
     * @param h the height of dirty area
     */
    public void draw(GraphicsContext gc, double x, double y, double w, double h) {
        gc.save();
        gc.setTextBaseline(VPos.CENTER);
        double offsetY = 0;
        for (TextLine line : texts.lines()) {
            double top = offsetY;
            double bottom = top + line.height();
            if (bottom < y) {
                offsetY += line.height() + 1;
                continue;
            }
            if (top >= (y + h)) break;
            for (TextRun run : line.runs()) {
                double left = run.layout().x();
                double right = left + run.layout().width();
                if (right < x) continue;
                if (left > (x + w)) break;
                if (run.style().background() instanceof Color bg && !bg.equals(Color.TRANSPARENT)) {
                    gc.setFill(bg);
                    gc.fillRect(left, top, run.layout().width(), line.height());
                } else {
                    gc.clearRect(left, top, run.layout().width(), line.height());
                }
                if (run.style().font() instanceof Font font) gc.setFont(font);
                if (run.style().color() instanceof Color color) gc.setFill(color);
                gc.fillText(run.text(), left, top + run.baseline());
            }
            offsetY += line.height() + 1;
        }
        gc.restore();
    }

    public void draw(GraphicsContext gc, Rect rect) {
        if (rect == null) return;
        draw(gc, rect.x(), rect.y(), rect.w(), rect.h());
    }

    public void tick(GraphicsContext gc) {
        if (caret.drawn()) {
            Rect rect = caret.clear(gc);
            draw(gc, rect);
        } else {
            caret.draw(gc);
        }
    }

    // -- scroll behavior  ----------------------------------------------------
    public int scrollPrev(int n) {
        int size = texts.prev(n);
        if (size > 0) caret.markDirty();
        return size;
    }
    public int scrollNext(int n) {
        int size = texts.next(n);
        if (size > 0) caret.markDirty();
        return size;
    }
    public void scrollToCaret() {
        boolean scrolled = texts.scrollAt(caret.row(), caret.offset());
        if (scrolled) caret.markDirty();
    }
    // -- arrow behavior ------------------------------------------------------
    public void selectOn() {
        selections.put(caret.offset());
    }
    public void selectOff() {
        selections.clear();
        texts.markDirty();
    }
    public void selectTo() {
        if (selections.length() > 0) {
            selections.get(0).to(caret.offset());
            texts.markDirty();
        }
    }
    public void moveCaretRight() {
        scrollToCaret();
        if (caret.y2() + 4 >= height) scrollNext(1);
        caret.right();
        if (caret.y2() > height) scrollNext(1);
    }
    public void moveCaretLeft() {
        scrollToCaret();
        if (texts.head().start() > 0 && texts.head().start() == caret.offset()) {
            scrollPrev(1);
        }
        caret.left();
    }
    public void moveCaretUp() {
        scrollToCaret();
        if (texts.head().start() > 0 && caret.offset() < texts.head().end()) {
            scrollPrev(1);
        }
        caret.up();
    }
    public void moveCaretDown() {
        scrollToCaret();
        if (caret.y2() + 4 >= height) scrollNext(1);
        caret.down();
        if (caret.y2() > height) scrollNext(1);
    }
    public void moveCaretPageUp() {
        scrollToCaret();
        double x = caret.x();
        double y = caret.y();
        scrollPrev(texts.capacity() - 1);
        caret.at(texts.at(x, y), false);
    }
    public void moveCaretPageDown() {
        scrollToCaret();
        double x = caret.x();
        double y = caret.y();
        scrollNext(texts.capacity() - 1);
        caret.at(texts.at(x, y), false);
    }
    // -- mouse behavior ------------------------------------------------------
    public void click(double x, double y) {
        int offset = texts.at(x, y);
        caret.at(offset, true);
    }
    public void clickDouble(double x, double y) {
        int offset = texts.at(x, y);
        caret.at(offset, true);
        // TODO
    }
    // -- edit behavior -------------------------------------------------------
    public void input(String value) {
        scrollToCaret();
        OffsetPoint caretPoint = caret.offsetPoint();
        editBuffer.push(Edit.insert(caretPoint, value));
        for (int i = 0; i < value.length(); i++) moveCaretRight();
        texts.markDirty();
        caret.markDirty();
    }
    public void delete() {
        scrollToCaret();
        OffsetPoint caretPoint = caret.offsetPoint();
        LayoutLine layoutLine = texts.layoutLine(caretPoint.offset());
        if (layoutLine == null) return;
        editBuffer.push(Edit.delete(caretPoint, layoutLine.charStringAt(caretPoint.offset())));
        texts.markDirty();
        caret.markDirty();
    }
    public void backspace() {
        moveCaretLeft();
        delete();
    }
    // -- conf behavior -------------------------------------------------------
    public void toggleWrap() {
        if (texts instanceof LinearTextList linear)  {
            texts = linear.asWrapped(width);
            caret.markDirty();
        } else if (texts instanceof WrapTextList wrap) {
            texts = wrap.asLinear();
            caret.markDirty();
        }
    }
    // -- private -------------------------------------------------------------

    private int screenRowSize(double height) {
        var fontMetrics = new FxFontMetrics(FxFontStyle.of().font());
        return (int) Math.ceil(height / fontMetrics.lineHeight());
    }

    public record Rect(double x, double y, double w, double h) { }

}
