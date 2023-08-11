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

import com.mammb.code.editor.ui.control.ScrollBar;
import com.mammb.code.editor2.model.buffer.Metrics;
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
import com.mammb.code.editor2.ui.pane.impl.SpecialCharacter;
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
    /** The vertical scroll. */
    private ScrollBar<Integer> vScroll;
    /** The horizontal scrollBar. */
    private ScrollBar<Double> hScroll;
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
        this(width, height, null, ScrollBar.vEmpty(), ScrollBar.hEmpty());
    }


    /**
     * Constructor.
     * @param width the screen width
     * @param height the screen height
     * @param path the path
     */
    public EditorModel(double width, double height, Path path,
            ScrollBar<Integer> vScroll, ScrollBar<Double> hScroll) {
        this.buffer = TextBuffer.editBuffer(path, screenRowSize(height));
        this.texts = new LinearTextList(buffer, StylingTranslate.passThrough());
        this.gutter = new Gutter();
        this.caret = new Caret(this::layoutLine);
        this.selection = new SelectionImpl();
        this.ime = new ImePalletImpl();
        this.width = width;
        this.height = height;
        setScroll(vScroll, hScroll);
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

        if (!text.isEmpty() && run.textLine().point().row() == caret.row()) {
            drawSpecialCharacter(gc, run, top, lineHeight);
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


    public void tick(GraphicsContext gc) {
        if (ime.enabled()) return;
        if (caret.drawn()) {
            Rect rect = caret.clear(gc, gutter.width());
            draw(gc, rect);
        } else {
            caret.draw(gc, gutter.width());
        }
    }

    private void drawSpecialCharacter(GraphicsContext gc, TextRun run, double top, double lineHeight) {
        final String text = run.text();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch != '\r' && ch != '\n' && ch != '\t' && ch != '　') continue;

            double x = gutter.width() + run.offsetToX().apply(i);
            double w = (ch == '　')
                ? gutter.width() + run.offsetToX().apply(i + 1) - x
                : Global.numberCharacterWidth(gc.getFont());
            var rect = new Rect(x, top + lineHeight * 0.1, w, lineHeight).smaller(0.8);

            gc.setStroke(Color.LIGHTGRAY);
            if (ch == '\r') {
                SpecialCharacter.CRLF.draw(gc, rect);
                break;
            } else if (ch == '\n') {
                SpecialCharacter.LF.draw(gc, rect);
                break;
            } else if (ch == '\t') {
                SpecialCharacter.TAB.draw(gc, rect);
            } else {
                SpecialCharacter.WSP.draw(gc, rect);
            }
        }

    }

    /**
     * Get the metrics.
     * @return the metrics
     */
    public Metrics metrics() {
        return buffer.metrics();
    }


    public void setScroll(ScrollBar<Integer> vScroll, ScrollBar<Double> hScroll) {

        this.vScroll = vScroll;
        vScroll.setMax(scrollMax());
        vScroll.setVisibleAmount(buffer.maxLineSize());
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());

        this.hScroll = hScroll;
        if (texts instanceof LinearTextList linear) {
            hScroll.setMax(linear.highWaterWidth());
            hScroll.setVisibleAmount(width - gutter.width());
            hScroll.setValue(0.0);
        }
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
    public void scrollPrev(int n) {
        int size = texts.prev(n);
        if (size == 0) return;
        caret.markDirty();
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
    }
    public void scrollNext(int n) {
        int size = texts.next(n);
        if (size == 0) return;
        caret.markDirty();
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
    }
    public void scrollToCaret() {
        boolean scrolled = texts.scrollAt(caret.row(), caret.offset());
        if (!scrolled) return;
        caret.markDirty();
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
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
            selection.endDragging();
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
        vScroll.setMax(scrollMax());
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
        vScroll.setMax(scrollMax());
    }
    public void backspace() {
        scrollToCaret();
        if (selection.length() > 0) {
            selectionDelete();
            return;
        }
        if (caret.offset() == 0) return;
        // TODO backspace at the leading position
        OffsetPoint caretPoint = caret.offsetPoint();
        LayoutLine layoutLine = texts.layoutLine(caretPoint.offset());
        if (layoutLine == null) return;
        moveCaretLeft();
        buffer.push(Edit.backspace(caretPoint, layoutLine.charStringAt(caret.offset())));
        texts.markDirty();
        vScroll.setMax(scrollMax());
    }
    private void selectionDelete() {
        if (selection.length() > 0) {
            Textual text = buffer.subText(selection.min(), selection.length());
            buffer.push(Edit.delete(text.point(), text.text()));
            selection.clear();
            caret.at(text.point().offset(), true);
            texts.markDirty();
            caret.markDirty();
            vScroll.setMax(scrollMax());
            vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
        }
    }
    public void undo() {
        selection.clear();
        Edit edit = buffer.undo();
        texts.markDirty();
        caret.at(edit.point().offset(), true);
        vScroll.setMax(scrollMax());
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
    }
    public void redo() {
        selection.clear();
        Edit edit = buffer.redo();
        texts.markDirty();
        caret.at(edit.point().offset(), true);
        vScroll.setMax(scrollMax());
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
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
        vScroll.setMax(scrollMax());
        vScroll.setVisibleAmount(buffer.maxLineSize());
        if (texts instanceof LinearTextList linear) {
            hScroll.setMax(linear.highWaterWidth());
            hScroll.setVisibleAmount(width - gutter.width());
        }
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
        vScroll.setMax(scrollMax());
        vScroll.setVisibleAmount(buffer.maxLineSize());
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
        if (texts instanceof LinearTextList linear) {
            hScroll.setMax(linear.highWaterWidth());
            hScroll.setVisibleAmount(width - gutter.width());
            hScroll.setValue(0.0);
        }
    }
    // -- private -------------------------------------------------------------


    int screenRowSize(double height) {
        return (int) Math.ceil(height / Global.fontMetrics.lineHeight());
    }

    private double textLeft() {
        return gutter.width() - hScroll.getValue();
    }

    private int scrollMax() {
        int max = buffer.metrics().rowCount() - buffer.maxLineSize();
        if (texts instanceof WrapTextList w) {
            max += w.wrappedSize() - buffer.maxLineSize();
        }
        return max;
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
