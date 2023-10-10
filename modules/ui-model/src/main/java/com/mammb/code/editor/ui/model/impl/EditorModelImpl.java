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
package com.mammb.code.editor.ui.model.impl;

import com.mammb.code.editor.model.buffer.Metrics;
import com.mammb.code.editor.model.buffer.TextBuffer;
import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.model.style.StylingTranslate;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.syntax.Syntax;
import com.mammb.code.editor.ui.control.ScrollBar;
import com.mammb.code.editor.ui.model.Caret;
import com.mammb.code.editor.ui.model.EditorModel;
import com.mammb.code.editor.ui.model.ImePallet;
import com.mammb.code.editor.ui.model.ImeRun;
import com.mammb.code.editor.ui.model.LayoutLine;
import com.mammb.code.editor.ui.model.Rect;
import com.mammb.code.editor.ui.model.ScreenText;
import com.mammb.code.editor.ui.model.Selection;
import com.mammb.code.editor.ui.model.StateHandler;
import com.mammb.code.editor.ui.model.behavior.ClipboardBehavior;
import com.mammb.code.editor.ui.model.behavior.SelectBehavior;
import com.mammb.code.editor.ui.model.draw.Draws;
import com.mammb.code.editor.ui.model.screen.PlainScreenText;
import com.mammb.code.editor.ui.model.screen.WrapScreenText;
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
public class EditorModelImpl implements EditorModel, Editor {

    /** logger. */
    private static final System.Logger log = System.getLogger(EditorModelImpl.class.getName());

    /** The Context. */
    private final Context context;
    /** The text buffer. */
    private final TextBuffer<Textual> buffer;
    /** The caret. */
    private final Caret caret;
    /** The selection. */
    private final Selection selection;
    /** The ime. */
    private final ImePallet ime;
    /** The state change. */
    private final StateChange stateChange;
    /** The screen scroll. */
    private final ScreenScroll screen;
    /** The text list. */
    private ScreenText texts;


    /**
     * Constructor.
     * @param context the context
     * @param buffer the text buffer
     * @param styling the styling
     * @param screen the screen scroll
     */
    private EditorModelImpl(
            Context context,
            TextBuffer<Textual> buffer,
            StylingTranslate styling,
            ScreenScroll screen) {

        this.context = context;
        this.buffer = buffer;
        this.texts = new PlainScreenText(context, buffer, styling);
        this.caret = new CaretImpl(this::layoutLine);
        this.selection = new SelectionImpl();
        this.ime = new ImePalletImpl();
        this.stateChange = new StateChangeImpl();
        this.screen = screen;
        screen.updateMaxWith(texts.lines().stream().mapToDouble(TextLine::width).max().orElse(0));
        screen.initScroll(texts, buffer.metrics().rowCount());
    }

    private EditorModelImpl(Context context, ScreenScroll scroll) {
        this(context,
            TextBuffer.editBuffer(null, scroll.pageSize()),
            StylingTranslate.passThrough(), scroll);
    }

    /**
     * Create a new EditorModel.
     * @param context the context
     * @param width the screen width
     * @param height the screen height
     * @return a new EditorModel
     */
    public static EditorModelImpl of(Context context,
            double width, double height, ScrollBar<Integer> vScroll, ScrollBar<Double> hScroll) {
        return new EditorModelImpl(context, new ScreenScroll(context, vScroll, hScroll, width, height));
    }


    /**
     * Create a new partially editor model.
     * @param path the specified path
     * @return a new partially EditorModel
     */
    @Override
    public EditorModelImpl partiallyWith(Path path) {
        return new EditorModelImpl(
            context,
            TextBuffer.fixed(path, screen.pageSize()),
            StylingTranslate.passThrough(),
            screen);
    }


    /**
     * Re-create the model at the specified path.
     * @param path the specified path
     * @return a new EditorModel
     */
    @Override
    public EditorModelImpl with(Path path) {
        return new EditorModelImpl(
            context,
            TextBuffer.editBuffer(path, screen.pageSize()),
            Syntax.of(path, context.preference().fgColor()),
            screen);
    }


    @Override
    public void draw(GraphicsContext gc) {
        Canvas canvas = gc.getCanvas();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.save();
        screen.resetMaxWidth();
        double offsetY = 0;
        for (TextLine line : texts.lines()) {
            screen.updateMaxWith(line.width());
            List<TextRun> runs = line.runs();
            fillContextBand(gc, runs, offsetY, line.leadingHeight());
            for (TextRun run : runs) {
                drawRun(gc, run, offsetY);
            }
            offsetY += line.leadingHeight();
        }
        showCaret(gc);

        gc.restore();
        if (screen.gutter().checkWidthChanged()) {
            texts.markDirty();
            caret.markDirty();
            draw(gc);
        }
        stateChange.push(metrics(), caret);
    }

    private void draw(GraphicsContext gc, LayoutLine layoutLine) {
        if (layoutLine == null) {
            return;
        }
        screen.updateMaxWith(layoutLine.width());
        gc.clearRect(screen.gutter().width(), layoutLine.offsetY(), screen.textAreaWidth(), layoutLine.leadingHeight());
        List<TextRun> runs = layoutLine.runs();
        fillContextBand(gc, runs, layoutLine.offsetY(), layoutLine.leadingHeight());
        for (TextRun run : runs) {
            drawRun(gc, run, layoutLine.offsetY());
        }
    }

    private void fillContextBand(GraphicsContext gc, List<TextRun> runs, double top, double lineHeight) {
        if (runs.stream().anyMatch(r -> !r.source().context().isEmpty())) {
            gc.setFill(Color.LIGHTGRAY.deriveColor(0, 0, 0, 0.2));
            gc.fillRect(screen.textLeft(), top, screen.width() - screen.textLeft(), lineHeight);
            gc.setFill(Color.TRANSPARENT);
        }
    }

    private void drawRun(GraphicsContext gc, TextRun run, double top) {

        double left = run.layout().x() + screen.textLeft();
        double lineHeight = run.textLine().leadingHeight();

        if (run.style().background() instanceof Color bg && !bg.equals(Color.TRANSPARENT)) {
            gc.setFill(bg);
            gc.fillRect(left, top, run.layout().width(), lineHeight);
        }

        if (selection.started()) {
            selection.draw(gc, run, top, screen.textLeft());
        }

        if (run.style().font() instanceof Font font) gc.setFont(font);
        if (run.style().color() instanceof Color color) { gc.setFill(color); gc.setStroke(color); }
        final String text = run.text();
        gc.fillText(text, left, top + run.layoutHeight());

        if (!text.isEmpty() && (
                run.textLine().point().row() == caret.row()) ||
                selection.contains(run.textLine().point().row())) {
            Draws.specialCharacter(gc, run, top, lineHeight, screen.textLeft());
        }

        if (run.layout().x() - screen.hScrollValue() <= 0) {
            screen.gutter().draw(gc, run, top, lineHeight);
        }
        if (ime.enabled()) {
            ime.drawCompose(gc, run, top, lineHeight, screen.textLeft());
        }
    }


    @Override
    public void tick(GraphicsContext gc) {
        if (ime.enabled()) return;
        if (caret.drawn()) {
            hideCaret(gc);
        } else {
            showCaret(gc);
        }
    }

    @Override
    public void showCaret(GraphicsContext gc) {
        if (!ime.enabled()) {
            caret.draw(gc, screen.gutter().width(), screen.hScrollValue());
        }
    }

    @Override
    public void hideCaret(GraphicsContext gc) {
        if (caret.flipIfDrawn()) {
            draw(gc, caret.layoutLine());
        }
    }

    /**
     * Get the metrics.
     * @return the metrics
     */
    public Metrics metrics() {
        return buffer.metrics();
    }

    @Override
    public StateHandler stateChange() {
        return stateChange;
    }

    @Override
    public Rect textAreaRect() {
        return screen.textArea();
    }

    @Override
    public boolean peekSelection(Predicate<Textual> predicate) {
        return (selection.length() > 0) &&
            predicate.test(buffer.subText(selection.min(), selection.length()));
    }

    // -- ime behavior  ----------------------------------------------------
    @Override
    public Rect imeOn(GraphicsContext gc) {
        if (ime.enabled()) new Rect(caret.x() + screen.textLeft(), caret.y(), caret.width(), caret.height());
        vScrollToCaret();
        ime.on(caret.caretPoint());
        draw(gc, caret.layoutLine());
        return new Rect(caret.x() + screen.textLeft(), caret.y(), caret.width(), caret.height());
    }
    @Override
    public void imeOff() {
        ime.off();
        buffer.flush();
    }
    @Override
    public void imeCommitted(String text) {
        ime.off();
        input(text);
    }
    @Override
    public void imeComposed(List<ImeRun> runs) {
        ime.composed(buffer, runs);
        texts.markDirty();
        caret.markDirty();
    }
    @Override
    public boolean isImeOn() {
        return ime.enabled();
    }

    // -- scroll behavior  ----------------------------------------------------
    @Override
    public void scrollPrev(int n) {
        int size = texts.prev(n);
        caret.markDirty();
        if (size == 0) {
            return;
        }
        texts.markDirty();
        screen.vScrolled(texts.head().point().row() + texts.head().lineIndex());
    }
    @Override
    public void scrollNext(int n) {
        int size = texts.next(n);
        caret.markDirty();
        if (size == 0) {
            return;
        }
        screen.vScrolled(texts.head().point().row() + texts.head().lineIndex());
    }
    @Override
    public void vScrolled(int oldValue, int newValue) {
        int delta = newValue - oldValue;
        if (delta == 0) {
            return;
        }
        int size = (delta > 0)
            ? texts.next(delta)
            : texts.prev(Math.abs(delta));
        if (size == 0) {
            return;
        }
        texts.markDirty();
        caret.markDirty();
    }
    private void vScrollToCaret() {
        boolean scrolled = texts.scrollAt(caret.row(), caret.offset());
        if (!scrolled) return;
        texts.markDirty();
        caret.markDirty();
        screen.vScrolled(texts.head().point().row() + texts.head().lineIndex());
    }
    private void hScrollToCaret() {
        if (texts instanceof WrapScreenText) return;
        screen.adjustHScroll(texts);
        double gap = screen.width() / 10;
        if (caret.x() <= screen.hScrollValue()) {
            screen.hScrolled(caret.x() - gap);
            return;
        }
        double right = screen.textAreaWidth() + screen.hScrollValue();
        if (caret.x() + gap >= right) {
            double delta = caret.x() + gap - right;
            screen.hScrolled(screen.hScrollValue() + delta);
        }
    }

    // -- arrow behavior ------------------------------------------------------
    @Override
    public void moveCaretRight() {
        vScrollToCaret();
        if (caret.y2() + 4 >= screen.height()) scrollNext(1);
        caret.right();
        if (caret.y2() > screen.height()) scrollNext(1);
        hScrollToCaret();
    }
    @Override
    public void moveCaretLeft() {
        vScrollToCaret();
        if (texts.head().offset() > 0 && texts.head().offset() == caret.offset()) {
            scrollPrev(1);
        }
        caret.left();
        hScrollToCaret();
    }
    @Override
    public void moveCaretUp() {
        vScrollToCaret();
        if (texts.head().offset() > 0 && caret.offset() < texts.head().tailOffset()) {
            scrollPrev(1);
        }
        caret.up();
        hScrollToCaret();
    }
    @Override
    public void moveCaretDown() {
        vScrollToCaret();
        if (caret.y2() + 4 >= screen.height()) scrollNext(1);
        caret.down();
        if (caret.y2() > screen.height()) scrollNext(1);
        hScrollToCaret();
    }
    @Override
    public void moveCaretPageUp() {
        vScrollToCaret();
        double x = caret.x();
        double y = caret.y();
        scrollPrev(texts.capacity() - 1);
        caret.at(texts.at(x, y), false);
    }
    @Override
    public void moveCaretPageDown() {
        vScrollToCaret();
        double x = caret.x();
        double y = caret.y();
        scrollNext(texts.capacity() - 1);
        caret.at(texts.at(x, y), false);
    }
    @Override
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
    @Override
    public void moveCaretLineEnd() {
        vScrollToCaret();
        LayoutLine line = texts.layoutLine(caret.offset());
        int offset = line.tailOffset() - line.endMarkCount();
        caret.at(offset, true);
        hScrollToCaret();
    }


    // -- edit behavior -------------------------------------------------------
    @Override
    public void input(String input) {

        if (input == null || input.isEmpty() || buffer.readOnly()) {
            return;
        }

        String value = metrics().lineEnding().unify(input);

        if (selection.length() > 0) {
            selectionReplace(value);
            return;
        }
        vScrollToCaret();
        OffsetPoint caretPoint = caret.caretPoint();
        buffer.push(Edit.insert(caretPoint, value));
        texts.markDirty();
        caret.markDirty();
        screen.adjustVScroll(texts, buffer.metrics().rowCount());

        int count = Character.codePointCount(value, 0, value.length());
        if (metrics().lineEnding().isCrLf()) {
            count -= (int) value.chars().filter(c -> c == '\r').count();
        }
        for (int i = 0; i < count; i++) {
            moveCaretRight();
        }
    }

    @Override
    public void delete() {
        vScrollToCaret();
        if (buffer.readOnly()) {
            return;
        }
        if (selection.length() > 0) {
            selectionDelete();
            return;
        }
        OffsetPoint caretPoint = caret.caretPoint();
        LayoutLine layoutLine = texts.layoutLine(caretPoint.offset());
        if (layoutLine == null) return;
        buffer.push(Edit.delete(caretPoint, layoutLine.charStringAt(caretPoint.offset())));
        texts.markDirty();
        caret.markDirty();
        screen.adjustVScroll(texts, buffer.metrics().rowCount());
    }

    @Override
    public void backspace() {
        vScrollToCaret();
        if (buffer.readOnly()) {
            return;
        }
        if (selection.length() > 0) {
            selectionDelete();
            return;
        }
        if (caret.offset() == 0) return;
        OffsetPoint caretPoint = caret.caretPoint();
        moveCaretLeft();
        LayoutLine layoutLine = texts.layoutLine(caret.offset());
        if (layoutLine == null) return;
        buffer.push(Edit.backspace(caretPoint, layoutLine.charStringAt(caret.offset())));
        texts.markDirty();
        caret.markDirty();
        screen.adjustVScroll(texts, buffer.metrics().rowCount());
    }

    private void selectionReplace(String string) {
        vScrollToCaret();
        if (buffer.readOnly()) {
            return;
        }
        if (string == null || string.isEmpty()) {
            selectionDelete();
            return;
        }
        if (selection.length() <= 0) {
            input(string);
            return;
        }
        Textual text = buffer.subText(selection.min(), selection.length());
        buffer.push(Edit.replace(text.point(), text.text(), string));
        SelectBehavior.select(text.offset(), text.offset() + string.length(), caret, selection);
        texts.markDirty();
        caret.markDirty();
        screen.adjustVScroll(texts, buffer.metrics().rowCount());
        screen.vScrolled(texts.head().point().row() + texts.head().lineIndex());
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
            screen.adjustVScroll(texts, buffer.metrics().rowCount());
            screen.vScrolled(texts.head().point().row() + texts.head().lineIndex());
            hScrollToCaret();
        }
    }
    @Override
    public void undo() {
        selection.clear();
        Edit edit = buffer.undo();
        if (edit.isEmpty()) return;
        texts.markDirty();
        caret.at(edit.point().offset(), true);
        screen.adjustVScroll(texts, buffer.metrics().rowCount());
        screen.vScrolled(texts.head().point().row() + texts.head().lineIndex());
        hScrollToCaret();
    }
    @Override
    public void redo() {
        selection.clear();
        Edit edit = buffer.redo();
        if (edit.isEmpty()) return;
        texts.markDirty();
        caret.at(edit.point().offset(), true);
        screen.adjustVScroll(texts, buffer.metrics().rowCount());
        screen.vScrolled(texts.head().point().row() + texts.head().lineIndex());
        hScrollToCaret();
    }

    @Override
    public void indent() {
        if (buffer.readOnly()) {
            return;
        }
        SelectBehavior.selectCurrentRow(caret, selection, texts);
        replaceSelection(text -> Arrays.stream(text.split("(?<=\n)"))
            .map(l -> "    " + l)
            .collect(Collectors.joining()));
    }

    @Override
    public void unindent() {
        if (buffer.readOnly()) {
            return;
        }
        SelectBehavior.selectCurrentRow(caret, selection, texts);
        replaceSelection(text -> Arrays.stream(text.split("(?<=\n)"))
            .map(l -> l.startsWith("\t") ? l.substring(1) : l.startsWith("    ") ? l.substring(4) : l)
            .collect(Collectors.joining()));
    }

    private void replaceSelection(Function<String, String> replace) {
        Textual text = buffer.subText(selection.min(), selection.length());
        selectionReplace(replace.apply(text.text()));
    }

    // -- layout behavior -----------------------------------------------------

    @Override
    public void layoutBounds(double width, double height) {
        screen.setSize(width, height);
        buffer.setPageSize(screen.pageSize());
        texts.markDirty();
        caret.markDirty();
        if (texts instanceof WrapScreenText wrap) {
            wrap.setWrapWidth(screen.textAreaWidth());
        }
        screen.initScroll(texts, buffer.metrics().rowCount());
    }

    // -- conf behavior -------------------------------------------------------

    @Override
    public void toggleWrap() {
        if (texts instanceof PlainScreenText linear)  {
            texts = linear.asWrapped(screen.textAreaWidth());
            caret.markDirty();
        } else if (texts instanceof WrapScreenText wrap) {
            texts = wrap.asPlain();
            screen.setMaxWidth(texts.lines().stream().mapToDouble(TextLine::width).max().orElse(0));
            caret.markDirty();
        }
        screen.initScroll(texts, buffer.metrics().rowCount());
    }

    // -- mouse behavior ------------------------------------------------------
    @Override
    public void click(double x, double y) {
        if (selection.isDragging()) {
            selection.to(caret.caretPoint());
            selection.endDragging();
            return;
        }
        selection.clear();
        double textLeft = screen.gutter().width() - screen.hScrollValue();
        int offset = texts.at(x - textLeft, y);
        caret.at(offset, true);
    }
    @Override
    public void clickDouble(double x, double y) {
        double textLeft = screen.gutter().width() - screen.hScrollValue();
        int[] offsets = texts.atAroundWord(x - textLeft, y);
        if (offsets.length == 2) {
            SelectBehavior.select(offsets, caret, selection);
        } else {
            caret.at(offsets[0], true);
        }
    }
    @Override
    public void dragged(double x, double y) {
        double textLeft = screen.gutter().width() - screen.hScrollValue();
        caret.at(texts.at(x - textLeft, y), true);
        if (selection.isDragging()) {
            selection.to(caret.caretPoint());
        } else {
            selection().startDragging(caret.caretPoint());
        }
    }

    // -- select behavior -----------------------------------------------------
    @Override
    public void selectOn() {
    if (!selection.started()) {
        selection.start(caret.caretPoint());
    }

    }
    @Override
    public void selectOff() {
        selection.clear();
    }
    @Override
    public void selectTo() {
        if (selection.started()) {
            selection.to(caret.caretPoint());
        }
    }
    @Override
    public void selectAll() {
        var metrics = buffer.metrics();
        SelectBehavior.select(
                OffsetPoint.zero,
                OffsetPoint.of(metrics.lfCount() + 1, metrics.chCount(), metrics.cpCount()),
                selection);
    }

    // -- clipboard behavior --------------------------------------------------
    @Override
    public void pasteFromClipboard() {
        ClipboardBehavior.pasteFromClipboard(this);
    }
    @Override
    public void copyToClipboard() {
        ClipboardBehavior.copyToClipboard(this);
    }
    @Override
    public void cutToClipboard() {
        ClipboardBehavior.cutToClipboard(this);
    }

    // -- file behavior -------------------------------------------------------
    @Override
    public void save() {
        buffer.save();
    }
    @Override
    public void saveAs(Path path) {
        buffer.saveAs(path);
    }
    @Override
    public Path path() {
        return buffer.metrics().path();
    }
    @Override
    public boolean modified() {
        return buffer.metrics().modified();
    }

    // -- private -------------------------------------------------------------

    private LayoutLine layoutLine(int offset) {
        return texts.layoutLine(offset);
    }

    // <editor-fold defaultstate="collapsed" desc="editor accessor">

    @Override
    public TextBuffer<Textual> buffer() {
        return buffer;
    }
    @Override
    public Caret caret() {
        return caret;
    }
    @Override
    public Selection selection() {
        return selection;
    }
    @Override
    public ScreenText texts() {
        return texts;
    }
    // </editor-fold>

}
