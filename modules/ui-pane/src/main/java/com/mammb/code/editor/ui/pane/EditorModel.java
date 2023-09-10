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
package com.mammb.code.editor.ui.pane;

import com.mammb.code.editor.ui.control.ScrollBar;
import com.mammb.code.editor.model.buffer.Metrics;
import com.mammb.code.editor.model.buffer.TextBuffer;
import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.ui.pane.impl.CaretImpl;
import com.mammb.code.editor.ui.pane.impl.Draws;
import com.mammb.code.editor.ui.pane.impl.SelectBehaviors;
import com.mammb.code.editor.model.style.StylingTranslate;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.ui.pane.impl.Clipboard;
import com.mammb.code.editor.ui.pane.impl.ImePalletImpl;
import com.mammb.code.editor.ui.pane.impl.SelectionImpl;
import com.mammb.code.editor.ui.prefs.Context;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * EditorModel.
 * @author Naotsugu Kobayashi
 */
public class EditorModel {

    /** logger. */
    private static final System.Logger log = System.getLogger(EditorModel.class.getName());

    /** The Context. */
    private final Context context;
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


    /**
     * Constructor.
     * @param context the context
     * @param width the screen width
     * @param height the screen height
     */
    public EditorModel(Context context, double width, double height) {
        this(context, width, height,
            null, StylingTranslate.passThrough(),
            ScrollBar.vEmpty(), ScrollBar.hEmpty());
    }


    /**
     * Constructor.
     * @param context the context
     * @param width the screen width
     * @param height the screen height
     * @param path the path
     */
    public EditorModel(
            Context context,
            double width, double height,
            Path path,
            StylingTranslate styling,
            ScrollBar<Integer> vScroll,
            ScrollBar<Double> hScroll) {

        this.context = context;
        this.buffer = TextBuffer.editBuffer(path, screenRowSize(height));
        this.texts = new LinearTextList(context, buffer, styling);
        this.gutter = new Gutter(context);
        this.caret = new CaretImpl(this::layoutLine);
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
        maxWidth = width - gutter.width();
        double offsetY = 0;
        for (TextLine line : texts.lines()) {
            if (line.width() > maxWidth) {
                maxWidth = line.width();
            }
            for (TextRun run : line.runs()) {
                drawRun(gc, run, offsetY, line.height());
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

    private void draw(GraphicsContext gc, LayoutLine layoutLine) {
        if (layoutLine == null) {
            return;
        }
        if (layoutLine.width() > maxWidth) {
            maxWidth = layoutLine.width();
        }
        gc.clearRect(gutter.width(), layoutLine.offsetY(), width, layoutLine.height());
        for (TextRun run : layoutLine.runs()) {
            drawRun(gc, run, layoutLine.offsetY(), layoutLine.height());
        }
    }


    private void drawRun(GraphicsContext gc, TextRun run, double top, double lineHeight) {

        double left = run.layout().x() + textLeft();

        if (run.style().background() instanceof Color bg && !bg.equals(Color.TRANSPARENT)) {
            gc.setFill(bg);
            gc.fillRect(left, top, run.layout().width(), lineHeight);
        }

        if (selection.started()) {
            selection.draw(gc, run, top, textLeft());
        }

        if (run.style().font() instanceof Font font) gc.setFont(font);
        if (run.style().color() instanceof Color color) { gc.setFill(color); gc.setStroke(color); }
        final String text = run.text();
        gc.fillText(text, left, top + run.baseline());

        if (!text.isEmpty() && (
                run.textLine().point().row() == caret.row()) ||
                selection.contains(run.textLine().point().row())) {
            Draws.specialCharacter(gc, run, top, lineHeight, textLeft());
        }

        if (run.layout().x() - hScroll.getValue() <= 0) {
            gutter.draw(gc, run, top, lineHeight);
        }
        if (ime.enabled()) {
            ime.drawCompose(gc, run, top, lineHeight, textLeft());
        }
    }


    /**
     * Tick.
     * @param gc the GraphicsContext
     */
    public void tick(GraphicsContext gc) {
        if (ime.enabled()) return;
        if (caret.drawn()) {
            hideCaret(gc);
        } else {
            showCaret(gc);
        }
    }

    /**
     * Show caret.
     * @param gc the GraphicsContext
     */
    public void showCaret(GraphicsContext gc) {
        if (!ime.enabled()) caret.draw(gc, gutter.width(), hScroll.getValue());
    }

    /**
     * Hide caret.
     * @param gc the GraphicsContext
     */
    public void hideCaret(GraphicsContext gc) {
        if (caret.flipIfDrawn()) draw(gc, caret.layoutLine());
    }

    /**
     * Get the metrics.
     * @return the metrics
     */
    public Metrics metrics() {
        return buffer.metrics();
    }

    public Rect textAreaRect() {
        return new Rect(gutter.width(), 0, width - gutter.width(), height);
    }


    public boolean peekSelection(Predicate<Textual> predicate) {
        return (selection.length() > 0) &&
            predicate.test(buffer.subText(selection.min(), selection.length()));
    }


    public void setScroll(ScrollBar<Integer> vScroll, ScrollBar<Double> hScroll) {

        this.vScroll = vScroll;
        adjustVScroll();
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());

        this.hScroll = hScroll;
        adjustHScroll();
        hScroll.setValue(0.0);
    }

    // -- ime behavior  ----------------------------------------------------
    public Rect imeOn(GraphicsContext gc) {
        if (ime.enabled()) new Rect(caret.x() + textLeft(), caret.y(), caret.width(), caret.height());
        vScrollToCaret();
        ime.on(caret.offsetPoint());
        draw(gc, caret.layoutLine());
        return new Rect(caret.x() + textLeft(), caret.y(), caret.width(), caret.height());
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
        caret.markDirty();
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
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());
    }
    public void scrollNext(int n) {
        int size = texts.next(n);
        if (size == 0) return;
        texts.markDirty();
        caret.markDirty();
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());
    }
    private void vScrollToCaret() {
        boolean scrolled = texts.scrollAt(caret.row(), caret.offset());
        if (!scrolled) return;
        texts.markDirty();
        caret.markDirty();
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());
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

    // -- select behavior -----------------------------------------------------
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
        var metrics = buffer.metrics();
        SelectBehaviors.select(
            OffsetPoint.zero,
            OffsetPoint.of(metrics.lfCount() + 1, metrics.chCount(), metrics.cpCount()),
            selection);
    }
    // -- arrow behavior ------------------------------------------------------
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
        int[] offsets = texts.atAroundWord(x - textLeft(), y);
        if (offsets.length == 2) {
            SelectBehaviors.select(offsets, caret, selection);
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
        if (selection.length() > 0) {
            selectionReplace(value);
            return;
        }
        vScrollToCaret();
        OffsetPoint caretPoint = caret.offsetPoint();
        buffer.push(Edit.insert(caretPoint, value));
        texts.markDirty();
        caret.markDirty();
        adjustVScroll();
        for (int i = 0; i < Character.codePointCount(value, 0, value.length()); i++) {
            moveCaretRight();
        }
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
        OffsetPoint caretPoint = caret.offsetPoint();
        moveCaretLeft();
        LayoutLine layoutLine = texts.layoutLine(caret.offset());
        if (layoutLine == null) return;
        buffer.push(Edit.backspace(caretPoint, layoutLine.charStringAt(caret.offset())));
        texts.markDirty();
        caret.markDirty();
        adjustVScroll();
    }
    private void selectionReplace(String string) {
        if (string == null || string.isEmpty()) {
            vScrollToCaret();
            selectionDelete();
            return;
        }
        if (selection.length() <= 0) {
            input(string);
            return;
        }
        vScrollToCaret();
        Textual text = buffer.subText(selection.min(), selection.length());
        buffer.push(Edit.replace(text.point(), text.text(), string));
        SelectBehaviors.select(text.offset(), text.offset() + string.length(), caret, selection);
        texts.markDirty();
        caret.markDirty();
        adjustVScroll();
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());
        hScrollToCaret();
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
            vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());
            hScrollToCaret();
        }
    }
    public void undo() {
        selection.clear();
        Edit edit = buffer.undo();
        texts.markDirty();
        caret.at(edit.point().offset(), true);
        adjustVScroll();
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());
        hScrollToCaret();
    }
    public void redo() {
        selection.clear();
        Edit edit = buffer.redo();
        texts.markDirty();
        caret.at(edit.point().offset(), true);
        adjustVScroll();
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());
        hScrollToCaret();
    }

    public void indent() {
        SelectBehaviors.selectCurrentRow(caret, selection, texts);
        replaceSelection(text -> Arrays.stream(text.split("(?<=\n)"))
            .map(l -> "    " + l)
            .collect(Collectors.joining()));
    }

    public void unindent() {
        SelectBehaviors.selectCurrentRow(caret, selection, texts);
        replaceSelection(text -> Arrays.stream(text.split("(?<=\n)"))
            .map(l -> l.startsWith("\t") ? l.substring(1) : l.startsWith("    ") ? l.substring(4) : l)
            .collect(Collectors.joining()));
    }

    private void replaceSelection(Function<String, String> replace) {
        Textual text = buffer.subText(selection.min(), selection.length());
        selectionReplace(replace.apply(text.text()));
    }

    // -- clipboard behavior --------------------------------------------------
    /**
     * Paste the text from the clipboard.
     */
    public void pasteFromClipboard() {
        var text = Clipboard.get();
        if (!text.isEmpty()) {
            input(metrics().lineEnding().unify(text));
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
        if (texts instanceof WrapTextList wrap) {
            wrap.setWrapWidth(width - gutter.width());
        }
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
            maxWidth = texts.lines().stream().mapToDouble(TextLine::width).max().orElse(width - gutter.width());
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
        } else {
            hScroll.setMax(width - gutter.width());
            hScroll.setVisibleAmount(width - gutter.width());
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
