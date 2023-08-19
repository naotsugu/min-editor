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

    /** The max width. */
    private double maxWidth = 0;

    private Color fgColor;

    /**
     * Constructor.
     * @param width the screen width
     * @param height the screen height
     * @param fgColor the fgColor
     */
    public EditorModel(double width, double height, Color fgColor) {
        this(width, height, fgColor,
            null, StylingTranslate.passThrough(),
            ScrollBar.vEmpty(), ScrollBar.hEmpty());
    }


    /**
     * Constructor.
     * @param width the screen width
     * @param height the screen height
     * @param fgColor the fgColor
     * @param path the path
     */
    public EditorModel(
            double width, double height, Color fgColor,
            Path path, StylingTranslate styling,
            ScrollBar<Integer> vScroll,
            ScrollBar<Double> hScroll) {
        this.fgColor = fgColor;
        this.buffer = TextBuffer.editBuffer(path, screenRowSize(height));
        this.texts = new LinearTextList(buffer, styling);
        this.gutter = new Gutter();
        this.caret = new Caret(this::layoutLine);
        this.selection = new SelectionImpl();
        this.ime = new ImePalletImpl();
        this.width = width;
        this.height = height;
        this.maxWidth = texts.lines().stream().mapToDouble(TextLine::width).max().orElse(width - gutter.width());
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
        maxWidth = width - gutter.width();
        double offsetY = 0;
        for (TextLine line : texts.lines()) {
            if (line.width() > maxWidth) maxWidth = line.width();
            List<TextRun> runs = line.runs();
            for (TextRun run : runs) {
                drawRun(gc, run, false, offsetY, line.height());
            }
            offsetY += line.leadingHeight();
        }
        showCaret(gc);

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

            if (line.width() > maxWidth) maxWidth = line.width();
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

        double left = run.layout().x() + textLeft();

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
            hideCaret(gc);
        } else {
            showCaret(gc);
        }
    }

    public void showCaret(GraphicsContext gc) {
        if (!ime.enabled()) caret.draw(gc, gutter.width(), hScroll.getValue());
    }

    public void hideCaret(GraphicsContext gc) {
        if (caret.drawn()) draw(gc, caret.clear(gc, textLeft()));
    }

    private void drawSpecialCharacter(GraphicsContext gc, TextRun run, double top, double lineHeight) {
        final String text = run.text();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch != '\r' && ch != '\n' && ch != '\t' && ch != '　') continue;

            double x = textLeft() + run.offsetToX().apply(i);
            double w = (ch == '　')
                ? textLeft() + run.offsetToX().apply(i + 1) - x
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
        adjustVScroll();
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());

        this.hScroll = hScroll;
        adjustHScroll();
        hScroll.setValue(0.0);
    }

    // -- ime behavior  ----------------------------------------------------
    public Rect imeOn(GraphicsContext gc) {
        if (ime.enabled()) new Rect(caret.x(), caret.y(), caret.width(), caret.height());
        vScrollToCaret();
        ime.on(caret.offsetPoint());
        draw(gc, caret.clear(gc, textLeft()));
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
        texts.markDirty();
        caret.markDirty();
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
    }
    public void scrollNext(int n) {
        int size = texts.next(n);
        if (size == 0) return;
        texts.markDirty();
        caret.markDirty();
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
    }
    private void vScrollToCaret() {
        boolean scrolled = texts.scrollAt(caret.row(), caret.offset());
        if (!scrolled) return;
        caret.markDirty();
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
    }
    private void hScrollToCaret() {
        if (texts instanceof WrapTextList) return;
        adjustHScroll();
        double gap = width / 10;
        if (caret.x() <= hScroll.getValue()) {
            hScroll.setValue(Math.max(0, caret.x() - gap));
            return;
        }
        double right = hScroll.getValue() + (width - gutter.width());
        if (caret.x() + gap >= right) {
            double delta = caret.x() + gap - right;
            hScroll.setValue(hScroll.getValue() + delta);
        }
    }
    public void vScrolled(int oldValue, int newValue) {
        int delta = newValue - oldValue;
        if (delta == 0) return;
        int size = (delta > 0) ? texts.next(delta) : texts.prev(Math.abs(delta));
        if (size == 0) return;
        texts.markDirty();
        caret.markDirty();
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
        vScrollToCaret();
        if (caret.y2() + 4 >= height) scrollNext(1);
        caret.right();
        if (caret.y2() > height) scrollNext(1);
        hScrollToCaret();
    }
    public void moveCaretLeft() {
        vScrollToCaret();
        if (texts.head().offset() > 0 && texts.head().offset() == caret.offset()) {
            scrollPrev(1);
        }
        caret.left();
        hScrollToCaret();
    }
    public void moveCaretUp() {
        vScrollToCaret();
        if (texts.head().offset() > 0 && caret.offset() < texts.head().tailOffset()) {
            scrollPrev(1);
        }
        caret.up();
        hScrollToCaret();
    }
    public void moveCaretDown() {
        vScrollToCaret();
        if (caret.y2() + 4 >= height) scrollNext(1);
        caret.down();
        if (caret.y2() > height) scrollNext(1);
        hScrollToCaret();
    }
    public void moveCaretPageUp() {
        vScrollToCaret();
        double x = caret.x();
        double y = caret.y();
        scrollPrev(texts.capacity() - 1);
        caret.at(texts.at(x, y), false);
    }
    public void moveCaretPageDown() {
        vScrollToCaret();
        double x = caret.x();
        double y = caret.y();
        scrollNext(texts.capacity() - 1);
        caret.at(texts.at(x, y), false);
    }
    public void moveCaretLineHome() {
        vScrollToCaret();
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
        hScrollToCaret();
    }
    public void moveCaretLineEnd() {
        vScrollToCaret();
        LayoutLine line = texts.layoutLine(caret.offset());
        int offset = line.tailOffset() - line.endMarkCount();
        caret.at(offset, true);
        hScrollToCaret();
    }
    // -- mouse behavior ------------------------------------------------------
    public void click(double x, double y) {
        if (selection.isDragging()) {
            selection.to(caret.offsetPoint());
            selection.endDragging();
            return;
        }
        selection.clear();
        int offset = texts.at(x - textLeft(), y);
        caret.at(offset, true);
    }
    public void clickDouble(double x, double y) {
        int[] offsets = texts.atAround(x - textLeft(), y);
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
        caret.at(texts.at(x - textLeft(), y), true);
        if (selection.isDragging()) {
            selection.to(caret.offsetPoint());
        } else {
            selection.startDragging(caret.offsetPoint());
        }
    }
    // -- edit behavior -------------------------------------------------------
    public void input(String value) {
        vScrollToCaret();
        selectionDelete();
        OffsetPoint caretPoint = caret.offsetPoint();
        buffer.push(Edit.insert(caretPoint, value));
        texts.markDirty();
        caret.markDirty();
        adjustVScroll();
        for (int i = 0; i < value.length(); i++) moveCaretRight();
    }
    public void delete() {
        vScrollToCaret();
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
        adjustVScroll();
    }
    public void backspace() {
        vScrollToCaret();
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
        adjustVScroll();
    }
    private void selectionDelete() {
        if (selection.length() > 0) {
            Textual text = buffer.subText(selection.min(), selection.length());
            buffer.push(Edit.delete(text.point(), text.text()));
            selection.clear();
            caret.at(text.point().offset(), true);
            texts.markDirty();
            caret.markDirty();
            adjustVScroll();
            vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
            hScrollToCaret();
        }
    }
    public void undo() {
        selection.clear();
        Edit edit = buffer.undo();
        texts.markDirty();
        caret.at(edit.point().offset(), true);
        adjustVScroll();
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
        hScrollToCaret();
    }
    public void redo() {
        selection.clear();
        Edit edit = buffer.redo();
        texts.markDirty();
        caret.at(edit.point().offset(), true);
        adjustVScroll();
        vScroll.setValue(texts.top().point().row() + texts.top().lineIndex());
        hScrollToCaret();
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
        adjustVScroll();
        adjustHScroll();
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
        setScroll(vScroll, hScroll);
    }
    // -- private -------------------------------------------------------------


    int screenRowSize(double height) {
        return (int) Math.ceil(height / Global.fontMetrics.lineHeight());
    }

    private double textLeft() {
        return gutter.width() - hScroll.getValue();
    }

    private LayoutLine layoutLine(int offset) {
        return texts.layoutLine(offset);
    }

    private void adjustVScroll() {
        int lines = buffer.metrics().rowCount();
        if (texts instanceof WrapTextList w) {
            lines += w.wrappedSize() - buffer.maxLineSize();
        }
        int adjustedMax = Math.max(0, lines - buffer.maxLineSize());
        double ratio = (double) adjustedMax / lines; // reduction ratio
        vScroll.setMax(adjustedMax);
        vScroll.setVisibleAmount((int) Math.floor(buffer.maxLineSize() * ratio));
    }

    private void adjustHScroll() {
        if (texts instanceof LinearTextList) {
            double w = Math.max(0, width - gutter.width());
            double adjustedMax = Math.max(0, maxWidth - w);
            double ratio = adjustedMax / maxWidth; // reduction ratio
            hScroll.setMax(adjustedMax);
            hScroll.setVisibleAmount(w * ratio);
        }
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
