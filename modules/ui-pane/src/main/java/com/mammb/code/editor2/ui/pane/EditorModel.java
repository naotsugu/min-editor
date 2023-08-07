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

import com.mammb.code.editor2.model.buffer.Metrics;
import com.mammb.code.editor2.model.buffer.MetricsRecord;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.style.StylingTranslate;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.ui.pane.impl.Clipboard;
import com.mammb.code.editor2.ui.pane.impl.ImePalletImpl;
import com.mammb.code.editor2.ui.pane.impl.SelectionImpl;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.nio.file.Path;
import java.util.List;

import static java.lang.System.Logger.Level.INFO;

/**
 * EditorModel.
 * @author Naotsugu Kobayashi
 */
public class EditorModel {

    /** logger. */
    private static final System.Logger log = System.getLogger(EditorModel.class.getName());

    /** The text buffer. */
    private final TextBuffer<Textual> buffer;
    /** The gutter. */
    private final Gutter gutter;
    /** The caret. */
    private final Caret caret;
    /** The selection. */
    private final Selection selection;
    /** The ime. */
    private final ImePallet ime;
    /** The screen width. */
    private double width;
    /** The screen height. */
    private double height;
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
        this.buffer = TextBuffer.editBuffer(path, screenRowSize(height));
        this.texts = new LinearTextList(buffer, StylingTranslate.passThrough());
        this.gutter = new Gutter();
        this.caret = new Caret(this::layoutLine);
        this.selection = new SelectionImpl();
        this.ime = new ImePalletImpl();
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
            List<TextRun> runs = line.runs();
            for (TextRun run : runs) {
                drawRun(gc, run, false, offsetY, line.height());
            }
            offsetY += line.leadingHeight();
        }
        if (!ime.enabled()) {
            caret.draw(gc, gutter.width());
        }
        gc.restore();
        if (gutter.checkWidthChanged()) {
            texts.markDirty();
            caret.markDirty();
            draw(gc);
        }
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

            List<TextRun> runs = line.runs();
            for (TextRun run : runs) {
                if ((run.layout().right()) < x) continue;
                if (run.layout().x() > (x + w)) break;
                drawRun(gc, run, true, top, line.height());
            }
            offsetY += line.leadingHeight();
        }
        gc.restore();
    }

    private void drawRun(GraphicsContext gc, TextRun run, boolean overlay, double top, double lineHeight) {

        double left = run.layout().x() + gutter.width();

        if (run.style().background() instanceof Color bg && !bg.equals(Color.TRANSPARENT)) {
            gc.setFill(bg);
            gc.fillRect(left, top, run.layout().width(), lineHeight);
        } else if (overlay) {
            gc.clearRect(left - 0.5, top, run.layout().width() + 1, lineHeight);
        }

        if (selection.started()) {
            selection.draw(gc, run, top, left);
        }

        if (run.style().font() instanceof Font font) gc.setFont(font);
        if (run.style().color() instanceof Color color) { gc.setFill(color); gc.setStroke(color); }
        final String text = run.text();
        gc.fillText(text, left, top + run.baseline());
        if (!text.isEmpty() && text.charAt(text.length() - 1) == '\n') {
            drawLf(gc, new Rect(
                left + run.layout().width(),
                top + lineHeight * 0.1,
                Global.numberCharacterWidth(gc.getFont()),
                lineHeight).smaller(0.5));
        }

        if (run.layout().x() == 0) {
            gutter.draw(gc, run, top, lineHeight);
        }
        if (ime.enabled()) {
            ime.drawCompose(gc, run, top, lineHeight, left);
        }
    }


    public void draw(GraphicsContext gc, Rect rect) {
        if (rect == null) return;
        draw(gc, rect.x(), rect.y(), rect.w(), rect.h());
    }

    public void drawLf(GraphicsContext gc, Rect r) {
        double[] xPoints = new double[] { r.x(), r.x() + r.w() / 2, r.x() + r.w() };
        double[] yPoints = new double[] { r.y() + r.h() * 0.75, r.y() + r.h(), r.y() + r.h() * 0.75 };
        gc.setLineWidth(1);
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineDashes(1);
        gc.strokePolyline(xPoints, yPoints, 3);
        gc.strokeLine(r.x() + r.w() / 2, r.y(), r.x() + r.w() / 2, r.y() + r.h());
    }

    public void tick(GraphicsContext gc) {
        if (ime.enabled()) return;
        if (caret.drawn()) {
            Rect rect = caret.clear(gc, gutter.width());
            draw(gc, rect);
        } else {
            caret.draw(gc, gutter.width());
        }
    }

    /**
     * Get the metrics.
     * @return the metrics
     */
    public Metrics metrics() {
        return buffer.metrics();
    }

    /**
     * Get the metrics snapshot.
     * @return the metrics snapshot
     */
    public Metrics metricsSnapshot() {
        return new MetricsRecord(buffer.metrics());
    }

    public int getMaxLineSize() {
        return buffer.maxLineSize();
    }

    // -- focus behavior  --------------------------------------------------
    public void focusIn(GraphicsContext gc) {
        caret.draw(gc, gutter.width());
    }

    public void focusOut(GraphicsContext gc) {
        if (caret.drawn()) draw(gc, caret.clear(gc, gutter.width()));
    }

    // -- ime behavior  ----------------------------------------------------
    public Rect imeOn(GraphicsContext gc) {
        if (ime.enabled()) new Rect(caret.x(), caret.y(), caret.width(), caret.height());
        scrollToCaret();
        ime.on(caret.offsetPoint());
        draw(gc, caret.clear(gc, gutter.width()));
        return new Rect(caret.x(), caret.y(), caret.width(), caret.height());
    }
    public void imeOff() {
        ime.off();
        buffer.flush();
    }
    public void imeCommitted(String text) {
        ime.off();
        input(text);
    }
    public void imeComposed(List<ImePallet.Run> runs) {
        ime.composed(buffer, runs);
        texts.markDirty();
    }
    public boolean isImeOn() {
        return ime.enabled();
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
    public void selectAll() {
        selection.start(OffsetPoint.zero);
        var metrics = buffer.metrics();
        log.log(INFO, metrics);
        selection.to(OffsetPoint.of(metrics.lfCount() + 1, metrics.chCount(), metrics.cpCount()));
    }
    private void selectionDelete() {
        if (selection.length() > 0) {
            Textual text = buffer.subText(selection.min(), selection.length());
            buffer.push(Edit.delete(text.point(), text.text()));
            selection.clear();
            caret.at(text.point().offset(), true);
            texts.markDirty();
            caret.markDirty();
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
        if (texts.head().offset() > 0 && texts.head().offset() == caret.offset()) {
            scrollPrev(1);
        }
        caret.left();
    }
    public void moveCaretUp() {
        scrollToCaret();
        if (texts.head().offset() > 0 && caret.offset() < texts.head().tailOffset()) {
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
        if (line.offset() == caret.offset()) {
            if (Character.isWhitespace(line.charAt(caret.offset()))) {
                while (caret.offset() < line.tailOffset() &&
                    Character.isWhitespace(line.charAt(caret.offset()))) {
                    caret.right();
                }
            }
        } else {
            caret.at(line.offset(), true);
        }
    }
    public void moveCaretLineEnd() {
        scrollToCaret();
        LayoutLine line = texts.layoutLine(caret.offset());
        int offset = line.tailOffset() - line.endMarkCount();
        caret.at(offset, true);
    }
    // -- mouse behavior ------------------------------------------------------
    public void click(double x, double y) {
        if (selection.isDragging()) {
            selection.to(caret.offsetPoint());
            return;
        }
        selection.clear();
        int offset = texts.at(x - gutter.width(), y);
        caret.at(offset, true);
    }
    public void clickDouble(double x, double y) {
        int[] offsets = texts.atAround(x - gutter.width(), y);
        if (offsets.length == 2) {
            caret.at(offsets[0], true);
            selection.start(caret.offsetPoint());
            caret.at(offsets[1], true);
            selection.to(caret.offsetPoint());
        } else {
            caret.at(offsets[0], true);
        }
    }
    public void dragged(double x, double y) {
        caret.at(texts.at(x - gutter.width(), y), true);
        if (selection.isDragging()) {
            selection.to(caret.offsetPoint());
        } else {
            selection.startDragging(caret.offsetPoint());
        }
    }
    // -- edit behavior -------------------------------------------------------
    public void input(String value) {
        scrollToCaret();
        selectionDelete();
        OffsetPoint caretPoint = caret.offsetPoint();
        buffer.push(Edit.insert(caretPoint, value));
        texts.markDirty();
        caret.markDirty();
        for (int i = 0; i < value.length(); i++) moveCaretRight();
    }
    public void delete() {
        scrollToCaret();
        if (selection.length() > 0) {
            selectionDelete();
            return;
        }
        OffsetPoint caretPoint = caret.offsetPoint();
        LayoutLine layoutLine = texts.layoutLine(caretPoint.offset());
        if (layoutLine == null) return;
        buffer.push(Edit.delete(caretPoint, layoutLine.charStringAt(caretPoint.offset())));
        texts.markDirty();
        caret.markDirty();
    }
    public void backspace() {
        scrollToCaret();
        if (selection.length() > 0) {
            selectionDelete();
            return;
        }
        if (caret.offset() == 0) return;
        OffsetPoint caretPoint = caret.offsetPoint();
        LayoutLine layoutLine = texts.layoutLine(caretPoint.offset());
        if (layoutLine == null) return;
        moveCaretLeft();
        buffer.push(Edit.backspace(caretPoint, layoutLine.charStringAt(caret.offset())));
        texts.markDirty();
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
    // -- layout behavior -----------------------------------------------------
    public void layoutBounds(double width, double height) {
        this.width = width;
        this.height = height;
        this.buffer.setMaxLineSize(screenRowSize(height));
        texts.markDirty();
        caret.markDirty();
    }
    // -- conf behavior -------------------------------------------------------
    public void toggleWrap() {
        if (texts instanceof LinearTextList linear)  {
            texts = linear.asWrapped(width - gutter.width());
            caret.markDirty();
        } else if (texts instanceof WrapTextList wrap) {
            texts = wrap.asLinear();
            caret.markDirty();
        }
    }
    // -- private -------------------------------------------------------------

    int screenRowSize(double height) {
        return (int) Math.ceil(height / Global.fontMetrics.lineHeight());
    }

    private LayoutLine layoutLine(int offset) {
        return texts.layoutLine(offset);
    }

    /**
     * Copy the selection text to the clipboard.
     * @param cut need cut?
     */
    private void copyToClipboard(boolean cut) {
        if (selection.length() > 0) {
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

}
