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

import com.mammb.code.editor.model.buffer.TextEdit;
import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.find.Find;
import com.mammb.code.editor.model.find.FoundReset;
import com.mammb.code.editor.model.find.FoundRun;
import com.mammb.code.editor.model.layout.TextLine;
import com.mammb.code.editor.model.layout.TextRun;
import com.mammb.code.editor.model.style.StylingTranslate;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.syntax.Syntax;
import com.mammb.code.editor.ui.model.CaretMulti;
import com.mammb.code.editor.ui.model.Editing;
import com.mammb.code.editor.ui.model.EditorModel;
import com.mammb.code.editor.ui.model.EditorQuery;
import com.mammb.code.editor.ui.model.FindHandle;
import com.mammb.code.editor.ui.model.ImePallet;
import com.mammb.code.editor.ui.model.ImeRun;
import com.mammb.code.editor.ui.model.LayoutLine;
import com.mammb.code.editor.ui.model.Rect;
import com.mammb.code.editor.ui.model.ScreenPoint;
import com.mammb.code.editor.ui.model.ScreenText;
import com.mammb.code.editor.ui.model.ScrollBar;
import com.mammb.code.editor.ui.model.Selection;
import com.mammb.code.editor.ui.model.StateHandler;
import com.mammb.code.editor.ui.model.draw.Draws;
import com.mammb.code.editor.ui.model.helper.Clipboards;
import com.mammb.code.editor.ui.model.helper.Selections;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * EditorModel.
 * @author Naotsugu Kobayashi
 */
public class EditorModelImpl implements EditorModel {

    /** logger. */
    private static final System.Logger log = System.getLogger(EditorModelImpl.class.getName());

    /** The Context. */
    private final Context context;
    /** The text buffer. */
    private final TextEdit buffer;
    /** The caret. */
    private final CaretMulti caret;
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
    /** The find. */
    private Find find;


    /**
     * Constructor.
     * @param context the context
     * @param buffer the text buffer
     * @param styling the styling
     * @param screen the screen scroll
     */
    private EditorModelImpl(
            Context context,
            TextEdit buffer,
            StylingTranslate styling,
            ScreenScroll screen) {

        this.context = context;
        this.buffer = buffer;
        this.find = Find.of(buffer.rowSupplier());
        this.texts = new PlainScreenText(context,
            buffer.createView(screen.pageLineSize()),
            styling.compound(Highlighter.of(find)));
        this.caret = CaretCore.of(offset -> texts.layoutLine(offset));
        this.selection = new SelectionImpl();
        this.ime = new ImePalletImpl();
        this.stateChange = new StateChangeImpl();
        this.screen = screen;
        screen.updateMaxWith(texts.lines().stream().mapToDouble(TextLine::width).max().orElse(0));
        screen.initScroll(texts.headlinesIndex(), totalLines());

    }


    /**
     * Constructor.
     * @param context the context
     * @param screen the screen scroll
     */
    private EditorModelImpl(Context context, ScreenScroll screen) {
        this(context,
            TextEdit.editBuffer(null),
            StylingTranslate.passThrough(), screen);
    }


    /**
     * Create a new EditorModel.
     * @param context the context
     * @param width the screen width
     * @param height the screen height
     * @return a new EditorModel
     */
    public static EditorModelImpl of(Context context,
            double width, double height,
            ScrollBar<Integer> vScroll, ScrollBar<Double> hScroll) {
        return new EditorModelImpl(context,
            new ScreenScroll(context, vScroll, hScroll, width, height));
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
            TextEdit.fixed(path),
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
            TextEdit.editBuffer(path),
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
        stateChange.push(buffer.metrics(), caret, selection);
    }


    private void draw(GraphicsContext gc, LayoutLine layoutLine) {
        if (layoutLine == null) {
            return;
        }
        screen.updateMaxWith(layoutLine.width());
        double gap = 0.01;
        gc.clearRect(screen.textArea().x(), layoutLine.offsetY() + gap, screen.textArea().w(), layoutLine.leadingHeight() - gap);
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

        boolean onCaret = !text.isEmpty() && (
            run.textLine().point().row() == caret.row()) ||
            selection.contains(run.textLine().point().row());
        if (onCaret) {
            Draws.specialCharacter(gc, run, top, lineHeight, screen.textLeft());
        }

        if (run.layout().x() - screen.hScrollValue() <= 0) {
            screen.gutter().draw(gc, run, top, onCaret);
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
            caret.draw(gc, screen.textArea().x(), screen.hScrollValue());
        }
    }

    @Override
    public void hideCaret(GraphicsContext gc) {
        if (caret.flipIfDrawn()) {
            draw(gc, caret.layoutLine());
        }
    }

    @Override
    public StateHandler stateChange() {
        return stateChange;
    }

    @Override
    public ScreenPoint screenPoint() {
        OffsetPoint caretPoint = caret.caretPoint();
        if (caretPoint == null) {
            caretPoint = OffsetPoint.zero;
        }
        return new ScreenPoint(texts.head().point().row(), caretPoint.offset());
    }

    @Override
    public void apply(ScreenPoint screenPoint) {
        vScrolled(texts.head().point().row(), screenPoint.row());
        caret.at(screenPoint.caretIndex(), true);
        screen.syncScroll(texts.headlinesIndex(), totalLines(), caret.x());
    }

    @Override
    public Rect textAreaRect() {
        return screen.textArea();
    }

    @Override
    public boolean peekSelection(Predicate<String> predicate) {
        return (selection.length() > 0) &&
            predicate.test(buffer.subText(selection.min(), (int) Long.min(selection.length(), Integer.MAX_VALUE)));
    }

    // <editor-fold defaultstate="collapsed" desc="edit behavior">
    @Override
    public void input(String input) {

        if (input == null || input.isEmpty() || buffer.readOnly()) {
            return;
        }

        String value = buffer.metrics().lineEnding().unify(input);

        if (selection.length() > 0) {
            selectionReplace(value);
            return;
        }
        vScrollToCaret();
        OffsetPoint caretPoint = caret.caretPoint();
        buffer.push(Edit.insert(caretPoint, value));
        find.reset();
        texts.markDirty();
        caret.markDirty();
        screen.adjustVScroll(totalLines());

        int count = Character.codePointCount(value, 0, value.length());
        if (buffer.metrics().lineEnding().isCrLf()) {
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
        if (layoutLine == null || layoutLine.containsTailOn(caretPoint.offset())) return;
        buffer.push(Edit.delete(caretPoint, layoutLine.charStringAt(caretPoint.offset())));
        find.reset();
        texts.markDirty();
        caret.markDirty();
        screen.adjustVScroll(totalLines());
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
        find.reset();
        texts.markDirty();
        caret.markDirty();
        screen.adjustVScroll(totalLines());
    }

    @Override
    public void undo() {
        selection.clear();
        Edit edit = buffer.undo();
        if (edit.isEmpty()) return;
        find.reset();
        texts.markDirty();
        caret.at(edit.point().offset(), true);
        screen.syncScroll(texts.headlinesIndex(), totalLines(), caret.x());
    }
    @Override
    public void redo() {
        selection.clear();
        Edit edit = buffer.redo();
        if (edit.isEmpty()) return;
        find.reset();
        texts.markDirty();
        caret.at(edit.point().offset(), true);
        screen.syncScroll(texts.headlinesIndex(), totalLines(), caret.x());
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="special edit behavior">

    @Override
    public boolean applyEditing(Editing editing) {
        return editing.apply(this, editorQuery());
    }

    @Override
    public void selectionReplace(String string) {
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
        OffsetPoint point = selection.min();
        String text = buffer.subText(point, (int) Long.min(selection.length(), Integer.MAX_VALUE));
        buffer.push(Edit.replace(point, text, string));
        Selections.select(point.offset(), point.offset() + string.length(), caret, selection);
        find.reset();
        texts.markDirty();
        caret.markDirty();
        screen.syncScroll(texts.headlinesIndex(), totalLines(), caret.x());
    }

    @Override
    public void indent() {
        if (buffer.readOnly()) {
            return;
        }
        Selections.selectCurrentRow(caret, selection, texts);
        String text = buffer.subText(selection.min(), (int) Long.min(selection.length(), Integer.MAX_VALUE));
        String replace = Arrays.stream(text.split("(?<=\n)"))
            .map(l -> "    " + l)
            .collect(Collectors.joining());
        selectionReplace(replace);
    }

    @Override
    public void unindent() {
        if (buffer.readOnly()) {
            return;
        }
        Selections.selectCurrentRow(caret, selection, texts);
        String text = buffer.subText(selection.min(), (int) Long.min(selection.length(), Integer.MAX_VALUE));
        String replace = Arrays.stream(text.split("(?<=\n)"))
            .map(l -> l.startsWith("\t") ? l.substring(1) : l.startsWith("    ") ? l.substring(4) : l)
            .collect(Collectors.joining());
        selectionReplace(replace);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="caret behavior">
    @Override
    public void moveCaretRight() {
        vScrollToCaret();
        if (caret.y2() + 4 >= screen.height()) scrollNext(1);
        caret.right();
        if (caret.isOutOfLines() || caret.y2() > screen.height()) scrollNext(1);
        screen.hScrollTo(caret.x());
    }
    @Override
    public void moveCaretLeft() {
        vScrollToCaret();
        if (texts.head().offset() > 0 && texts.head().offset() == caret.offset()) {
            scrollPrev(1);
        }
        caret.left();
        screen.hScrollTo(caret.x());
    }
    @Override
    public void moveCaretUp() {
        vScrollToCaret();
        if (texts.head().offset() > 0 && caret.offset() < texts.head().tailOffset()) {
            scrollPrev(1);
        }
        caret.up();
        screen.hScrollTo(caret.x());
    }
    @Override
    public void moveCaretDown() {
        vScrollToCaret();
        if (caret.y2() + 4 >= screen.height()) scrollNext(1);
        caret.down();
        if (caret.y2() > screen.height()) scrollNext(1);
        screen.hScrollTo(caret.x());
    }
    @Override
    public void moveCaretLineHome() {
        vScrollToCaret();
        LayoutLine line = texts.layoutLine(caret.offset());
        boolean skipWhitespace = true;
        if (line.offset() != caret.offset()) {
            if (caret.offset() - line.offset() < 36 &&
                line.text().substring(0, (int) (caret.offset() - line.offset())).trim().isEmpty()) {
                skipWhitespace = false;
            }
            caret.at(line.offset(), true);
        }
        if (skipWhitespace) {
            if (Character.isWhitespace(line.charAt(caret.offset()))) {
                while (caret.offset() < line.tailOffset() &&
                    Character.isWhitespace(line.charAt(caret.offset()))) {
                    caret.right();
                }
            }
        }
        screen.hScrollTo(caret.x());
    }
    @Override
    public void moveCaretLineEnd() {
        vScrollToCaret();
        LayoutLine line = texts.layoutLine(caret.offset());
        long offset = line.tailOffset() - line.endMarkCount();
        caret.at(offset, true);
        screen.hScrollTo(caret.x());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="scroll behavior">
    @Override
    public void scrollPrev(int n) {
        int size = texts.prev(n);
        caret.markDirty();
        if (size == 0) {
            return;
        }
        texts.markDirty();
        screen.vScrolled(texts.headlinesIndex());
    }

    @Override
    public void scrollNext(int n) {
        int size = texts.next(n);
        caret.markDirty();
        if (size == 0) {
            return;
        }
        screen.vScrolled(texts.headlinesIndex());
    }

    @Override
    public void pageUp() {
        vScrollToCaret();
        double x = caret.x();
        double y = caret.y();
        scrollPrev(texts.pageSize() - 1);
        caret.at(texts.at(x, y), false);
    }

    @Override
    public void pageDown() {
        vScrollToCaret();
        double x = caret.x();
        double y = caret.y();
        scrollNext(texts.pageSize() - 1);
        caret.at(texts.at(x, y), false);
    }

    @Override
    public void vScrolled(int oldValue, int newValue) {
        if ((newValue - oldValue) == 0) {
            return;
        }
        boolean scrolled = texts.move(newValue, buffer.metrics());
        if (scrolled) {
            texts.markDirty();
            caret.markDirty();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="mouse behavior">
    @Override
    public void click(double x, double y, boolean isShortcutDown) {
        if (selection.isDragging()) {
            selection.to(caret.caretPoint());
            selection.endDragging();
            return;
        }
        selection.clear();
        long offset = texts.at(x - screen.textLeft(), y);
        if (isShortcutDown) {
            caret.add(offset);
        } else {
            caret.at(offset, true);
        }
    }
    @Override
    public void clickDouble(double x, double y) {
        long[] offsets = texts.atAroundWord(x - screen.textLeft(), y);
        if (offsets.length == 2) {
            Selections.select(offsets, caret, selection);
        } else {
            caret.at(offsets[0], true);
        }
    }
    @Override
    public void clickTriple(double x, double y) {
        long[] offsets = texts.atAroundDelimiter(x - screen.textLeft(), y);
        if (offsets.length == 2) {
            Selections.select(offsets, caret, selection);
        } else {
            caret.at(offsets[0], true);
        }
    }
    @Override
    public void dragged(double x, double y) {
        caret.at(texts.at(Math.max(x - screen.textLeft(), 0), y), true);
        if (selection.isDragging()) {
            selection.to(caret.caretPoint());
        } else {
            selection.startDragging(caret.caretPoint());
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="select behavior">
    @Override
    public void selectOn() {
        if (!selection.started()) selection.start(caret.caretPoint());
    }
    @Override
    public void selectOff() {
        selection.clear();
    }
    @Override
    public void selectTo() {
        if (selection.started()) selection.to(caret.caretPoint());
    }
    @Override
    public void selectAll() {
        selection.selectAll(buffer.metrics());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clipboard behavior">
    @Override
    public void pasteFromClipboard() {
        input(Clipboards.get());
    }
    @Override
    public void copyToClipboard() {
        copyToClipboard(false);
    }
    @Override
    public void cutToClipboard() {
        copyToClipboard(true);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="file behavior">
    @Override
    public void save() {
        buffer.save();
        // for clean modified
        stateChange.push(buffer.metrics(), caret, selection);
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ime behavior">
    @Override
    public Rect imeOn(GraphicsContext gc) {
        if (!ime.enabled()) {
            vScrollToCaret();
            ime.on(caret.caretPoint());
            draw(gc, caret.layoutLine());
        }
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
        find.reset();
        texts.markDirty();
        caret.markDirty();
    }
    @Override
    public boolean isImeOn() {
        return ime.enabled();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="layout behavior">
    @Override
    public void layoutBounds(double width, double height) {
        screen.setSize(width, height);
        texts.setPageSize(screen.pageLineSize());
        texts.markDirty();
        caret.markDirty();
        if (texts instanceof WrapScreenText wrap) {
            wrap.setWrapWidth(screen.textArea().w());
        }
        screen.initScroll(texts.headlinesIndex(), totalLines());
    }

    @Override
    public void toggleWrap() {
        if (texts instanceof PlainScreenText linear)  {
            texts = linear.asWrapped(screen.textArea().w());
            screen.sethScrollEnabled(false);
            caret.markDirty();
        } else if (texts instanceof WrapScreenText wrap) {
            texts = wrap.asPlain();
            screen.sethScrollEnabled(true);
            screen.setMaxWidth(texts.lines().stream().mapToDouble(TextLine::width).max().orElse(0));
            caret.markDirty();
        }
        screen.initScroll(texts.headlinesIndex(), totalLines());
    }

    @Override
    public FindHandle findHandle() {
        return FindHandleImpl.of(
            find,
            caret.caretPoint(),
            buffer.metrics().byteLen() < 32_000_000,
            found -> {
                switch (found) {
                    case FoundRun run -> {
                        // TODO Need to consider if row wrapped.
                        var row = Math.max(0, run.row() - screen.pageLineSize() / 2);
                        var point = new ScreenPoint(row, run.chOffset() + (run.right() ? run.length() : 0));
                        apply(point);
                        texts.markDirty();
                        caret.markDirty();
                    }
                    case FoundReset reset -> {
                        texts.markDirty();
                        caret.markDirty();
                    }
                    default -> {}
                }
        });
    }

    // </editor-fold>

    // -- private -------------------------------------------------------------

    private void selectionDelete() {
        if (selection.length() == 0) {
            return;
        }
        OffsetPoint point = selection.min();
        String text = buffer.subText(point, (int) Long.min(selection.length(), Integer.MAX_VALUE));
        buffer.push(Edit.delete(point, text));
        selection.clear();
        caret.at(point.offset(), true);
        find.reset();
        texts.markDirty();
        caret.markDirty();
        screen.syncScroll(texts.headlinesIndex(), totalLines(), caret.x());
    }

    private void vScrollToCaret() {
        boolean scrolled = texts.scrollAtScreen(caret.row(), caret.offset());
        if (!scrolled) return;
        texts.markDirty();
        caret.markDirty();
        screen.vScrolled(texts.headlinesIndex());
    }

    private int totalLines() {
        return (texts instanceof WrapScreenText w)
            ? buffer.metrics().rowCount() + (w.wrappedSize() - texts.pageSize())
            : buffer.metrics().rowCount();
    }

    /**
     * Copy the selection text to the clipboard.
     * @param cut need cut?
     */
    private void copyToClipboard(boolean cut) {
        if (selection.length() > 0) {
            OffsetPoint point = selection.min();
            String text = buffer.subText(point, (int) Long.min(selection.length(), Integer.MAX_VALUE));
            Clipboards.put(text);
            if (cut && !buffer.readOnly()) {
                buffer.push(Edit.delete(point, text));
                selection.clear();
                caret.at(point.offset(), true);
                texts.markDirty();
                caret.markDirty();
            }
        }
    }

    /**
     * Create the editor query.
     * @return the editor query
     */
    private EditorQuery editorQuery() {
        return new EditorQuery() {
            @Override public String selectedText() {
                return (selection.length() <= 0) ? ""
                    : buffer.subText(selection.min(), (int) Long.min(selection.length(), Integer.MAX_VALUE));
            }
            @Override public String currentLine() {
                return texts.lineAt(caret.offset()).text();
            }
            @Override public String currentRow() {
                return buffer.rowSupplier().at(caret.offset());
            }
            @Override public String caretBefore() {
                return buffer.rowSupplier().before(caret.caretPoint().cpOffset());
            }
        };
    }

}
