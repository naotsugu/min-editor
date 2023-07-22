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
import com.mammb.code.editor2.ui.pane.impl.Clipboard;
import com.mammb.code.editor2.ui.pane.impl.SelectionImpl;
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
    private final TextBuffer<Textual> buffer;
    /** The screen width. */
    private final double width;
    /** The screen height. */
    private final double height;
    /** The caret. */
    private final Caret caret;
    /** The selection. */
    private final Selection selection;
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
        this.buffer = TextBuffer.editBuffer(screenRowSize(height), path);
        this.texts = new LinearTextList(buffer, StylingTranslate.passThrough());
        this.caret = new Caret(texts::layoutLine);
        this.selection = new SelectionImpl();
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
                if (selection.started()) selection.draw(gc, run, offsetY);

                if (run.style().font() instanceof Font font) gc.setFont(font);
                if (run.style().color() instanceof Color color) gc.setFill(color);
                gc.fillText(run.text(), run.layout().x(), offsetY + run.baseline());
            }
            offsetY += line.leadingHeight();
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
                offsetY += line.leadingHeight();
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
                if (selection.started()) selection.draw(gc, run, offsetY);

                if (run.style().font() instanceof Font font) gc.setFont(font);
                if (run.style().color() instanceof Color color) gc.setFill(color);
                gc.fillText(run.text(), left, top + run.baseline());
            }
            offsetY += line.leadingHeight();
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
        if (!selection.started()) {
            selection.start(caret.offsetPoint());
        }
    }
    public void selectOff() {
        selection.clear();
    }
    public void selectTo() {
        if (selection.started()) {
            selection.to(caret.offsetPoint());
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
    public void moveCaretLineHome() {
        scrollToCaret();
        LayoutLine line = texts.layoutLine(caret.offset());
        if (line.start() == caret.offset()) {
            if (Character.isWhitespace(line.charAt(caret.offset()))) {
                while (caret.offset() < line.end() &&
                    Character.isWhitespace(line.charAt(caret.offset()))) {
                    caret.right();
                }
            }
        } else {
            caret.at(line.start(), true);
        }
    }
    public void moveCaretLineEnd() {
        scrollToCaret();
        LayoutLine line = texts.layoutLine(caret.offset());
        int offset = line.end() - line.endMarkCount();
        caret.at(offset, true);
    }
    // -- mouse behavior ------------------------------------------------------
    public void click(double x, double y) {
        int offset = texts.at(x, y);
        caret.at(offset, true);
    }
    public void clickDouble(double x, double y) {
        int[] offsets = texts.atAround(x, y);
        if (offsets.length == 2) {
            caret.at(offsets[0], true);
            selection.start(caret.offsetPoint());
            caret.at(offsets[1], true);
            selection.to(caret.offsetPoint());
        } else {
            caret.at(offsets[0], true);
        }
    }
    // -- edit behavior -------------------------------------------------------
    public void input(String value) {
        scrollToCaret();
        OffsetPoint caretPoint = caret.offsetPoint();
        buffer.push(Edit.insert(caretPoint, value));
        texts.markDirty();
        caret.markDirty();
        for (int i = 0; i < value.length(); i++) moveCaretRight();
    }
    public void delete() {
        scrollToCaret();
        OffsetPoint caretPoint = caret.offsetPoint();
        LayoutLine layoutLine = texts.layoutLine(caretPoint.offset());
        if (layoutLine == null) return;
        buffer.push(Edit.delete(caretPoint, layoutLine.charStringAt(caretPoint.offset())));
        texts.markDirty();
        caret.markDirty();
    }
    public void backspace() {
        moveCaretLeft();
        delete();
        // TODO
    }
    public void undo() {
        selection.clear();
        Edit edit = buffer.undo();
        texts.markDirty();
        caret.at(edit.point().offset(), true);
    }
    public void redo() {
        selection.clear();
        Edit edit = buffer.redo();
        texts.markDirty();
        caret.at(edit.point().offset(), true);
    }
    /**
     * Paste the text from the clipboard.
     */
    public void pasteFromClipboard() {
        var text = Clipboard.get();
        if (!text.isEmpty()) {
            input(text);
        }
    }
    /**
     * Copy the selection text to the clipboard.
     */
    public void copyToClipboard() {
        copyToClipboard(false);
    }
    /**
     * Cut the selection text to the clipboard.
     */
    public void cutToClipboard() {
        copyToClipboard(true);
    }

    // -- file behavior -------------------------------------------------------
    public void save() {
        buffer.save();
    }
    public void saveAs(Path path) {
        buffer.saveAs(path);
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

    /**
     * Copy the selection text to the clipboard.
     * @param cut need cut?
     */
    private void copyToClipboard(boolean cut) {
        if (selection.started() && selection.length() > 0) {
            Textual text = buffer.subText(selection.min(), selection.length());
            Clipboard.put(text.text());
            if (cut) {
                buffer.push(Edit.delete(text.point(), text.text()));
                selection.clear();
                caret.at(text.point().offset(), true);
                texts.markDirty();
                caret.markDirty();
            }
        }
    }


    public record Rect(double x, double y, double w, double h) { }

}
