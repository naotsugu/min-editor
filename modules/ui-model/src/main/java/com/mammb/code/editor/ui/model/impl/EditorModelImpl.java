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

import com.mammb.code.editor.javafx.layout.FxFonts;
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
import com.mammb.code.editor.ui.model.behavior.MouseBehavior;
import com.mammb.code.editor.ui.model.behavior.ScrollBehavior;
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
    /** The gutter. */
    private final Gutter gutter;
    /** The caret. */
    private final Caret caret;
    /** The selection. */
    private final Selection selection;
    /** The ime. */
    private final ImePallet ime;
    /** The state change. */
    private StateChange stateChange;
    /** The text list. */
    private ScreenText texts;

    /** The vertical scroll. */
    private ScrollBar<Integer> vScroll;
    /** The horizontal scroll. */
    private ScrollBar<Double> hScroll;
    /** The screen width. */
    private double width;
    /** The screen height. */
    private double height;
    /** The max width. */
    private double maxWidth = 0;

    /**
     * Constructor.
     * @param context the context
     * @param buffer the text buffer
     * @param width the screen width
     * @param height the screen height
     * @param styling the styling
     * @param vScroll the vertical scroll
     * @param hScroll the horizontal scroll
     */
    private EditorModelImpl(
            Context context,
            TextBuffer<Textual> buffer,
            double width, double height,
            StylingTranslate styling,
            ScrollBar<Integer> vScroll,
            ScrollBar<Double> hScroll) {

        this.context = context;
        this.buffer = buffer;
        this.texts = new PlainScreenText(context, buffer, styling);
        this.gutter = new GutterImpl(context);
        this.caret = new CaretImpl(this::layoutLine);
        this.selection = new SelectionImpl();
        this.ime = new ImePalletImpl();
        this.stateChange = new StateChangeImpl();
        this.width = width;
        this.height = height;
        this.maxWidth = texts.lines().stream().mapToDouble(TextLine::width).max().orElse(width - gutter.width());
        setScroll(vScroll, hScroll);
    }


    /**
     * Create a new EditorModel.
     * @param context the context
     * @param width the screen width
     * @param height the screen height
     * @return a new EditorModel
     */
    public static EditorModelImpl of(Context context, double width, double height, ScrollBar<Integer> vScroll, ScrollBar<Double> hScroll) {
        return new EditorModelImpl(
            context,
            TextBuffer.editBuffer(null, screenRowSize(height, context)),
            width, height,
            StylingTranslate.passThrough(),
            vScroll, hScroll);
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
            TextBuffer.fixed(path, screenRowSize(height, context)),
            width, height,
            StylingTranslate.passThrough(),
            vScroll, hScroll);
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
            TextBuffer.editBuffer(path, screenRowSize(height, context)),
            width, height,
            Syntax.of(path, context.preference().fgColor()),
            vScroll, hScroll);
    }


    @Override
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
            List<TextRun> runs = line.runs();
            fillContextBand(gc, runs, offsetY, line.leadingHeight());
            for (TextRun run : runs) {
                drawRun(gc, run, offsetY);
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
        stateChange.push(metrics(), caret);
    }

    private void draw(GraphicsContext gc, LayoutLine layoutLine) {
        if (layoutLine == null) {
            return;
        }
        if (layoutLine.width() > maxWidth) {
            maxWidth = layoutLine.width();
        }
        gc.clearRect(gutter.width(), layoutLine.offsetY(), width - gutter.width(), layoutLine.leadingHeight());
        List<TextRun> runs = layoutLine.runs();
        fillContextBand(gc, runs, layoutLine.offsetY(), layoutLine.leadingHeight());
        for (TextRun run : runs) {
            drawRun(gc, run, layoutLine.offsetY());
        }
    }

    private void fillContextBand(GraphicsContext gc, List<TextRun> runs, double top, double lineHeight) {
        if (runs.stream().anyMatch(r -> !r.source().context().isEmpty())) {
            gc.setFill(Color.LIGHTGRAY.deriveColor(0, 0, 0, 0.2));
            gc.fillRect(textLeft(), top, width - textLeft(), lineHeight);
            gc.setFill(Color.TRANSPARENT);
        }
    }

    private void drawRun(GraphicsContext gc, TextRun run, double top) {

        double left = run.layout().x() + textLeft();
        double lineHeight = run.textLine().leadingHeight();

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
        gc.fillText(text, left, top + run.layoutHeight());

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
        if (!ime.enabled()) caret.draw(gc, gutter.width(), hScroll.getValue());
    }

    @Override
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

    @Override
    public StateHandler stateChange() {
        return stateChange;
    }

    @Override
    public Rect textAreaRect() {
        return new Rect(gutter.width(), 0, width - gutter.width(), height);
    }

    @Override
    public boolean peekSelection(Predicate<Textual> predicate) {
        return (selection.length() > 0) &&
            predicate.test(buffer.subText(selection.min(), selection.length()));
    }


    private void setScroll(ScrollBar<Integer> vScroll, ScrollBar<Double> hScroll) {

        this.vScroll = vScroll;
        adjustVScroll();
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());

        this.hScroll = hScroll;
        adjustHScroll();
        hScroll.setValue(0.0);
    }

    // -- ime behavior  ----------------------------------------------------
    @Override
    public Rect imeOn(GraphicsContext gc) {
        if (ime.enabled()) new Rect(caret.x() + textLeft(), caret.y(), caret.width(), caret.height());
        vScrollToCaret();
        ime.on(caret.caretPoint());
        draw(gc, caret.layoutLine());
        return new Rect(caret.x() + textLeft(), caret.y(), caret.width(), caret.height());
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
        ScrollBehavior.scrollPrev(this, n);
    }
    @Override
    public void scrollNext(int n) {
        ScrollBehavior.scrollNext(this, n);
    }
    @Override
    public void vScrolled(int oldValue, int newValue) {
        ScrollBehavior.scrolled(this, oldValue, newValue);
    }
    private void vScrollToCaret() {
        boolean scrolled = texts.scrollAt(caret.row(), caret.offset());
        if (!scrolled) return;
        texts.markDirty();
        caret.markDirty();
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());
    }
    private void hScrollToCaret() {
        if (texts instanceof WrapScreenText) return;
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

    // -- arrow behavior ------------------------------------------------------
    @Override
    public void moveCaretRight() {
        vScrollToCaret();
        if (caret.y2() + 4 >= height) scrollNext(1);
        caret.right();
        if (caret.y2() > height) scrollNext(1);
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
        if (caret.y2() + 4 >= height) scrollNext(1);
        caret.down();
        if (caret.y2() > height) scrollNext(1);
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
        adjustVScroll();

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
        adjustVScroll();
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
        adjustVScroll();
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
    @Override
    public void undo() {
        selection.clear();
        Edit edit = buffer.undo();
        if (edit.isEmpty()) return;
        texts.markDirty();
        caret.at(edit.point().offset(), true);
        adjustVScroll();
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());
        hScrollToCaret();
    }
    @Override
    public void redo() {
        selection.clear();
        Edit edit = buffer.redo();
        if (edit.isEmpty()) return;
        texts.markDirty();
        caret.at(edit.point().offset(), true);
        adjustVScroll();
        vScroll.setValue(texts.head().point().row() + texts.head().lineIndex());
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
        this.width = width;
        this.height = height;
        this.buffer.setPageSize(screenRowSize(height));
        texts.markDirty();
        caret.markDirty();
        if (texts instanceof WrapScreenText wrap) {
            wrap.setWrapWidth(width - gutter.width());
        }
        adjustVScroll();
        adjustHScroll();
    }

    // -- conf behavior -------------------------------------------------------

    @Override
    public void toggleWrap() {
        if (texts instanceof PlainScreenText linear)  {
            texts = linear.asWrapped(width - gutter.width());
            caret.markDirty();
        } else if (texts instanceof WrapScreenText wrap) {
            texts = wrap.asPlain();
            maxWidth = texts.lines().stream().mapToDouble(TextLine::width).max().orElse(width - gutter.width());
            caret.markDirty();
        }
        setScroll(vScroll, hScroll);
    }

    // -- mouse behavior ------------------------------------------------------
    @Override
    public void click(double x, double y) {
        MouseBehavior.click(this, x, y);
    }
    @Override
    public void clickDouble(double x, double y) {
        MouseBehavior.clickDouble(this, x, y);
    }
    @Override
    public void dragged(double x, double y) {
        MouseBehavior.dragged(this, x, y);
    }

    // -- select behavior -----------------------------------------------------
    @Override
    public void selectOn() {
        SelectBehavior.selectOn(this);
    }
    @Override
    public void selectOff() {
        SelectBehavior.selectOff(this);
    }
    @Override
    public void selectTo() {
        SelectBehavior.selectTo(this);
    }
    @Override
    public void selectAll() {
        SelectBehavior.selectAll(this);
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

    int screenRowSize(double height) {
        return screenRowSize(height, context);
    }

    static int screenRowSize(double height, Context context) {
        Font font = Font.font(context.preference().fontName(), context.preference().fontSize());
        double lineHeight = FxFonts.lineHeight(font) + TextLine.DEFAULT_MARGIN_TOP + TextLine.DEFAULT_MARGIN_BOTTOM;
        return (int) Math.ceil(height / lineHeight);
    }

    private double textLeft() {
        return gutter.width() - hScroll.getValue();
    }

    private LayoutLine layoutLine(int offset) {
        return texts.layoutLine(offset);
    }

    private void adjustVScroll() {
        int lines = buffer.metrics().rowCount();
        if (texts instanceof WrapScreenText w) {
            lines += w.wrappedSize() - buffer.pageSize();
        }
        int adjustedMax = Math.max(0, lines - buffer.pageSize());
        double ratio = (double) adjustedMax / lines; // reduction ratio
        vScroll.setMax(adjustedMax);
        vScroll.setVisibleAmount((int) Math.floor(buffer.pageSize() * ratio));
    }

    private void adjustHScroll() {
        if (texts instanceof PlainScreenText) {
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

    // <editor-fold defaultstate="collapsed" desc="editor accessor">

    @Override
    public Context context() {
        return context;
    }
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
    @Override
    public Gutter gutter() {
        return gutter;
    }
    @Override
    public ScrollBar<Integer> vScroll() {
        return vScroll;
    }
    @Override
    public ScrollBar<Double> hScroll() {
        return hScroll;
    }

    // </editor-fold>

}
