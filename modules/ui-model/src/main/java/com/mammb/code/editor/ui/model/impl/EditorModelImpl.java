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
import com.mammb.code.editor.model.text.LineEnding;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.OffsetPointRange;
import com.mammb.code.editor.syntax.Syntax;
import com.mammb.code.editor.ui.model.Caret;
import com.mammb.code.editor.ui.model.CaretMulti;
import com.mammb.code.editor.ui.model.EditorModel;
import com.mammb.code.editor.ui.model.FindHandle;
import com.mammb.code.editor.ui.model.ImePallet;
import com.mammb.code.editor.ui.model.ImeRun;
import com.mammb.code.editor.ui.model.LayoutLine;
import com.mammb.code.editor.ui.model.ModelQuery;
import com.mammb.code.editor.ui.model.Rect;
import com.mammb.code.editor.ui.model.RectBox;
import com.mammb.code.editor.ui.model.ScreenPoint;
import com.mammb.code.editor.ui.model.ScreenText;
import com.mammb.code.editor.ui.model.ScrollBar;
import com.mammb.code.editor.ui.model.Selection;
import com.mammb.code.editor.ui.model.StateHandler;
import com.mammb.code.editor.ui.model.draw.Draws;
import com.mammb.code.editor.ui.model.editing.Editing;
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
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
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
    private final CaretMulti carets;
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
        this.carets = new CaretMultiImpl(offset -> texts.layoutLine(offset));
        this.selection = new SelectionImpl();
        this.ime = new ImePalletImpl();
        this.stateChange = new StateChangeImpl();
        this.screen = screen;
        screen.updateMaxWith(texts.lines().stream().mapToDouble(TextLine::width).max().orElse(0));
        screen.initScroll(texts.headlinesIndex(), totalLines());
        carets.refresh();
    }


    /**
     * Constructor.
     * @param context the context
     * @param screen the screen scroll
     */
    private EditorModelImpl(Context context, ScreenScroll screen) {
        this(context,
            TextEdit.editBuffer(null),
            Syntax.of(context.preference().fgColor()),
            screen);
    }


    /**
     * Create a new EditorModel.
     * @param context the context
     * @param width the screen width
     * @param height the screen height
     * @return a new EditorModel
     */
    public static EditorModelImpl of(
            Context context,
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
            carets.refresh();
            draw(gc);
        }
        stateChange.push(buffer.metrics(), carets, selection);
    }


    private void draw(GraphicsContext gc, LayoutLine layoutLine) {
        if (layoutLine == null) {
            return;
        }
        screen.updateMaxWith(layoutLine.width());
        double gap = 0.01;
        gc.clearRect(screen.textArea().x(), layoutLine.offsetY() + gap, screen.textArea().width(), layoutLine.leadingHeight() - gap);
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

    /**
     * Draw the text run.
     * @param gc the graphics context
     * @param run the text run
     * @param top the top position
     */
    private void drawRun(GraphicsContext gc, TextRun run, double top) {

        double left = run.layout().x() + screen.textLeft();
        double lineHeight = run.textLine().leadingHeight();

        if (run.style().background() instanceof Color bg && !bg.equals(Color.TRANSPARENT)) {
            gc.setFill(bg);
            gc.fillRect(left, top, run.layout().width(), lineHeight);
        }

        selection.draw(gc, run, top, screen.textLeft());

        if (run.style().font() instanceof Font font) gc.setFont(font);
        if (run.style().color() instanceof Color color) { gc.setFill(color); gc.setStroke(color); }
        final String text = run.text();
        gc.fillText(text, left, top + run.layoutHeight());

        boolean onCaret = !text.isEmpty() && (
            run.textLine().offsetPoint().row() == carets.row()) ||
            (selection.hasSelection() && selection.getRanges().stream()
                .anyMatch(r -> r.containsRow(run.textLine().offsetPoint().row())));
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
        // caret's blinking is useless.
    }

    @Override
    public void showCaret(GraphicsContext gc) {
        if (!ime.enabled()) {
            carets.setHide(false);
            carets.draw(gc, screen.textArea().x(), screen.hScrollValue());
        }
    }

    @Override
    public void hideCaret(GraphicsContext gc) {
        carets.setHide(true);
        carets.list().forEach(c -> draw(gc, c.layoutLine()));
    }

    @Override
    public StateHandler stateChange() {
        return stateChange;
    }

    @Override
    public ScreenPoint screenPoint() {
        OffsetPoint caretPoint = carets.offsetPoint();
        if (caretPoint == null) {
            caretPoint = OffsetPoint.zero;
        }
        return new ScreenPoint(texts.head().offsetPoint().row(), caretPoint.offset());
    }

    @Override
    public void apply(ScreenPoint screenPoint) {
        vScrolled(texts.head().offsetPoint().row(), screenPoint.row());
        carets.at(screenPoint.caretIndex(), true);
        screen.syncScroll(texts.headlinesIndex(), totalLines(), carets.x());
    }

    @Override
    public Rect textAreaRect() {
        return screen.textArea();
    }

    @Override
    public boolean peekSelection(Predicate<String> predicate) {
        if (selection.hasSelection()) {
            OffsetPointRange range = selection.getRanges().getFirst();
            return predicate.test(buffer.subText(range.minOffsetPoint(), (int) Long.min(range.length(), Integer.MAX_VALUE)));
        }
        return false;
    }

    @Override
    public <R> R query(ModelQuery<R> query) {
        return run(query).value();
    }

    // <editor-fold defaultstate="collapsed" desc="edit behavior">
    @Override
    public void input(String input) {
        vScrollToCaret();
        if (input == null || input.isEmpty() || buffer.readOnly()) {
            return;
        }
        String value = buffer.metrics().lineEnding().unify(input);
        if (selection.hasSelection()) {
            selectionsReplace(List.of(value));
        } else {
            input(List.of(value));
        }
    }

    @Override
    public void delete() {
        vScrollToCaret();
        if (buffer.readOnly()) {
            return;
        }
        if (selection.hasSelection()) {
            selectionDelete();
            return;
        }

        var unit = Edit.unit();
        var hoisting = Hoisting.caretOf(carets.list());
        for (Caret caret : hoisting.points()) {
            String ch = caret.charAt();
            unit.put(Edit.delete(ch, caret.offsetPoint()));
            hoisting.shift(caret.offsetPoint().offset(), -ch.length());
        }
        buffer.push(unit.commit());
        hoisting.locateOn(carets);

        find.reset();
        texts.markDirty();
        carets.refresh();
        screen.adjustVScroll(totalLines());
    }

    @Override
    public void backspace() {
        vScrollToCaret();
        if (buffer.readOnly()) {
            return;
        }
        if (selection.hasSelection()) {
            selectionDelete();
            return;
        }
        if (carets.offset() == 0) {
            return;
        }

        var unit = Edit.unit();
        var hoisting = Hoisting.caretOf(carets.list());
        for (Caret caret : hoisting.points()) {
            OffsetPoint offset = caret.offsetPoint();
            caret.left();
            String ch = caret.charAt();
            unit.put(Edit.backspace(ch, offset));
            hoisting.shiftOn(offset.offset(), -ch.length());
        }
        buffer.push(unit.commit());
        hoisting.locateOn(carets);

        find.reset();
        texts.markDirty();
        carets.refresh();
        screen.adjustVScroll(totalLines());
    }

    @Override
    public void undo() {
        selection.selectOff();
        Edit edit = buffer.undo();
        if (edit.isEmpty()) return;
        find.reset();
        texts.markDirty();
        carets.at(edit.point().offset(), true);
        screen.syncScroll(texts.headlinesIndex(), totalLines(), carets.x());
    }

    @Override
    public void redo() {
        selection.selectOff();
        Edit edit = buffer.redo();
        if (edit.isEmpty()) return;
        find.reset();
        texts.markDirty();
        carets.at(edit.point().offset(), true);
        screen.syncScroll(texts.headlinesIndex(), totalLines(), carets.x());
    }

    private void input(List<String> lines) {
        if (lines == null || lines.isEmpty() || buffer.readOnly()) {
            return;
        }
        List<Caret> caretList = carets.list();
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < caretList.size(); i++) {
            if (lines.size() == 1) {
                stack.push(lines.getFirst());
            } else {
                stack.push((lines.size() > i) ? lines.get(i) : "");
            }
        }

        var unit = Edit.unit();
        var hoisting = Hoisting.caretOf(caretList);
        for (Caret caret : hoisting.points()) {
            String value = stack.pop();
            unit.put(Edit.insert(value, caret.offsetPoint()));
            hoisting.shiftOn(caret.offsetPoint().offset(), value.length());
        }
        buffer.push(unit.commit());
        hoisting.locateOn(carets);

        find.reset();
        texts.markDirty();
        carets.refresh();
        screen.adjustVScroll(totalLines());
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="special edit behavior">

    @Override
    public boolean applyEditing(Editing editing) {
        return editing.apply(this);
    }

    @Override
    public void selectionReplace(String string) {
        vScrollToCaret();
        if (buffer.readOnly() || !selection.hasSelection()) {
            return;
        }
        if (string == null || string.isEmpty()) {
            selectionDelete();
        } else {
            selectionsReplace(List.of(string));
        }
    }

    private void selectionsReplace(List<String> lines) {

        if (lines == null || lines.isEmpty() || buffer.readOnly()) {
            return;
        }

        List<OffsetPointRange> ranges = selection.getRanges();
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < ranges.size(); i++) {
            if (lines.size() == 1) {
                stack.push(lines.getFirst());
            } else {
                stack.push((lines.size() > i) ? lines.get(i) : "");
            }
        }

        var unit = Edit.unit();
        var hoisting = Hoisting.rangeOf(ranges);
        for (OffsetPointRange range : hoisting.points()) {
            String value = stack.pop();
            OffsetPoint point = range.minOffsetPoint();
            String text = buffer.subText(point, (int) Long.min(range.length(), Integer.MAX_VALUE));
            unit.put(Edit.replace(text, value, point));
            hoisting.shift(range.minOffsetPoint().offset(), value.length() - text.length());
            hoisting.mv(range.minOffsetPoint().offset(), value.length());
        }
        buffer.push(unit.commit());
        hoisting.locateOn(carets);

        selection.selectOff();
        find.reset();
        texts.markDirty();
        carets.refresh();
        screen.syncScroll(texts.headlinesIndex(), totalLines(), carets.x());

    }

    @Override
    public void indent() {
        if (buffer.readOnly()) {
            return;
        }
        carets.clear();
        OffsetPointRange range = selection.getRanges().getFirst();
        Selections.selectCurrentRow(carets, selection, range, texts);
        String text = buffer.subText(range.minOffsetPoint(), (int) Long.min(range.length(), Integer.MAX_VALUE));
        String replace = Arrays.stream(text.split("(?<=\n)"))
            .map(l -> "    " + l)
            .collect(Collectors.joining());
        buffer.push(Edit.replace(text, replace, range.minOffsetPoint()));
        find.reset();
        texts.markDirty();
        carets.refresh();
        screen.syncScroll(texts.headlinesIndex(), totalLines(), carets.x());
        Selections.select(range.minOffsetPoint().offset(), range.minOffsetPoint().offset() + replace.length(), carets, selection);
    }

    @Override
    public void unindent() {
        if (buffer.readOnly()) {
            return;
        }
        carets.clear();
        OffsetPointRange range = selection.getRanges().getFirst();
        Selections.selectCurrentRow(carets, selection, range, texts);
        String text = buffer.subText(range.minOffsetPoint(), (int) Long.min(range.length(), Integer.MAX_VALUE));
        String replace = Arrays.stream(text.split("(?<=\n)"))
            .map(l -> l.startsWith("\t") ? l.substring(1) : l.startsWith("    ") ? l.substring(4) : l)
            .collect(Collectors.joining());
        buffer.push(Edit.replace(text, replace, range.minOffsetPoint()));
        find.reset();
        texts.markDirty();
        carets.refresh();
        screen.syncScroll(texts.headlinesIndex(), totalLines(), carets.x());
        Selections.select(range.minOffsetPoint().offset(), range.minOffsetPoint().offset() + replace.length(), carets, selection);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="caret behavior">
    @Override
    public void moveCaretRight() {
        vScrollToCaret();
        if (carets.bottom() + 4 >= screen.height()) scrollNext(1);
        carets.right();
        if (carets.layoutLine() == null || carets.bottom() > screen.height()) scrollNext(1);
        screen.hScrollTo(carets.x());
    }
    @Override
    public void moveCaretLeft() {
        vScrollToCaret();
        if (texts.head().offset() > 0 && texts.head().offset() == carets.offset()) {
            scrollPrev(1);
        }
        carets.left();
        screen.hScrollTo(carets.x());
    }
    @Override
    public void moveCaretUp() {
        vScrollToCaret();
        if (texts.head().offset() > 0 && carets.offset() < texts.head().tailOffset()) {
            scrollPrev(1);
        }
        carets.up();
        screen.hScrollTo(carets.x());
    }
    @Override
    public void moveCaretDown() {
        vScrollToCaret();
        if (carets.bottom() + 4 >= screen.height()) scrollNext(1);
        carets.down();
        if (carets.bottom() > screen.height()) scrollNext(1);
        screen.hScrollTo(carets.x());
    }
    @Override
    public void moveCaretLineHome() {
        vScrollToCaret();
        carets.home();
        screen.hScrollTo(carets.x());
    }
    @Override
    public void moveCaretLineEnd() {
        vScrollToCaret();
        carets.end();
        screen.hScrollTo(carets.x());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="scroll behavior">
    @Override
    public void scrollPrev(int n) {
        int size = texts.prev(n);
        carets.refresh();
        if (size == 0) {
            return;
        }
        texts.markDirty();
        screen.vScrolled(texts.headlinesIndex());
    }

    @Override
    public void scrollNext(int n) {
        int size = texts.next(n);
        carets.refresh();
        if (size == 0) {
            return;
        }
        screen.vScrolled(texts.headlinesIndex());
    }

    @Override
    public void pageUp() {
        vScrollToCaret();
        double x = carets.x();
        double y = carets.y();
        scrollPrev(texts.pageSize() - 1);
        carets.at(texts.at(x, y), false);
    }

    @Override
    public void pageDown() {
        vScrollToCaret();
        double x = carets.x();
        double y = carets.y();
        scrollNext(texts.pageSize() - 1);
        carets.at(texts.at(x, y), false);
    }

    @Override
    public void vScrolled(int oldValue, int newValue) {
        if ((newValue - oldValue) == 0) {
            return;
        }
        boolean scrolled = texts.move(newValue, buffer.metrics());
        if (scrolled) {
            texts.markDirty();
            carets.refresh();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="mouse behavior">
    @Override
    public void click(double x, double y, boolean isShortcutDown) {

        if (carets.isHide()) {
            carets.setHide(false);
            return;
        }

        if (selection.isOpened()) {
            // end of dragged
            selection.closeOn(carets.offsetPoint());
            return;
        }

        selection.selectOff();

        if (x < screen.textLeft()) {
            // click on gutter
            clickGutter(y, isShortcutDown);
            return;
        }

        long offset = texts.at(x - screen.textLeft(), y);
        if (isShortcutDown) {
            carets.add(offset);
        } else {
            carets.at(offset, true);
        }
    }

    @Override
    public void clickDouble(double x, double y) {
        long[] offsets = texts.atAroundWord(x - screen.textLeft(), y);
        if (offsets.length == 2) {
            Selections.select(offsets, carets, selection);
        } else {
            carets.at(offsets[0], true);
        }
    }
    @Override
    public void clickTriple(double x, double y) {
        long[] offsets = texts.atAroundDelimiter(x - screen.textLeft(), y);
        if (offsets.length == 2) {
            Selections.select(offsets, carets, selection);
        } else {
            carets.at(offsets[0], true);
        }
    }
    @Override
    public void dragged(double x, double y) {
        carets.at(texts.at(Math.max(x - screen.textLeft(), 0), y), true);
        selection.selectOn(carets.offsetPoint());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="select behavior">
    @Override
    public void selectOn() {
        selection.selectOn(carets);
    }
    @Override
    public void selectOff() {
        selection.selectOff();
    }
    @Override
    public void selectAll() {
        var metrics = buffer.metrics();
        selection.select(OffsetPoint.zero,
            OffsetPoint.of(metrics.lfCount() + 1, metrics.chCount(), metrics.cpCount()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clipboard behavior">
    @Override
    public void pasteFromClipboard() {
        String text = Clipboards.get();
        if (text == null || text.isEmpty() || buffer.readOnly()) {
            return;
        }
        if (carets.hasMulti() && text.contains("\n")) {
            vScrollToCaret();
            List<String> lines = text.lines().toList();
            if (selection.hasSelection()) {
                selectionsReplace(lines);
            } else {
                input(lines);
            }
        } else {
            input(text);
        }
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
        stateChange.push(buffer.metrics(), carets, selection);
    }
    @Override
    public void saveAs(Path path) {
        buffer.saveAs(path);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ime behavior">
    @Override
    public Rect imeOn(GraphicsContext gc) {
        if (!ime.enabled()) {
            vScrollToCaret();
            ime.on(carets.offsetPoint());
            draw(gc, carets.layoutLine());
        }
        return new RectBox(carets.x() + screen.textLeft(), carets.y(), carets.width(), carets.height());
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
        carets.refresh();
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
        carets.refresh();
        if (texts instanceof WrapScreenText wrap) {
            wrap.setWrapWidth(screen.textArea().width());
        }
        screen.initScroll(texts.headlinesIndex(), totalLines());
    }

    @Override
    public void toggleWrap() {
        if (texts instanceof PlainScreenText linear)  {
            texts = linear.asWrapped(screen.textArea().width());
            screen.setHScrollEnabled(false);
            carets.refresh();
        } else if (texts instanceof WrapScreenText wrap) {
            texts = wrap.asPlain();
            screen.setHScrollEnabled(true);
            screen.setMaxWidth(texts.lines().stream().mapToDouble(TextLine::width).max().orElse(0));
            carets.refresh();
        }
        screen.initScroll(texts.headlinesIndex(), totalLines());
    }

    @Override
    public void clear() {
        selection.selectOff();
        carets.clear();
    }

    @Override
    public FindHandle findHandle() {
        return FindHandleImpl.of(
            find,
            carets.offsetPoint(),
            buffer.metrics().byteLen() < 32_000_000,
            found -> {
                switch (found) {
                    case FoundRun run -> {
                        // TODO Need to consider if row wrapped.
                        var row = Math.max(0, run.row() - screen.pageLineSize() / 2);
                        var point = new ScreenPoint(row, run.chOffset() + (run.right() ? run.length() : 0));
                        apply(point);
                        texts.markDirty();
                        carets.refresh();
                    }
                    case FoundReset reset -> {
                        texts.markDirty();
                        carets.refresh();
                    }
                    default -> {}
                }
        });
    }

    // </editor-fold>

    // -- private -------------------------------------------------------------

    /**
     * Delete the selection text.
     */
    private void selectionDelete() {

        if (!selection.hasSelection()) {
            return;
        }

        var unit = Edit.unit();
        var hoisting = Hoisting.rangeOf(selection.getRanges());
        for (OffsetPointRange range : hoisting.points()) {
            OffsetPoint point = range.minOffsetPoint();
            String text = buffer.subText(point, (int) Long.min(range.length(), Integer.MAX_VALUE));
            unit.put(Edit.delete(text, point));
            hoisting.shift(range.minOffsetPoint().offset(), -text.length());
        }
        buffer.push(unit.commit());
        hoisting.locateOn(carets);
        selection.selectOff();
        find.reset();
        texts.markDirty();
        carets.refresh();
        screen.syncScroll(texts.headlinesIndex(), totalLines(), carets.x());
    }

    private void clickGutter(double y, boolean isShortcutDown) {
        if (isShortcutDown) {
            carets.clear();
            TextLine caretLine = texts.lineAt(carets.offset());
            TextLine clickLine = texts.at(y).orElse(null);
            if (caretLine != null && clickLine != null) {
                double x = caretLine.offsetToX(carets.offset());
                TextLine start = caretLine;
                TextLine end = clickLine;
                if (start.offset() == end.offset()) {
                    Selections.select(start.offset(), start.tailOffset(), carets, selection);
                    return;
                } else if (start.offset() > end.offset()) {
                    start = clickLine;
                    end = caretLine;
                }
                for (TextLine line : texts.lines()) {
                    if (line.offset() < start.offset() ||
                        line.offset() > end.offset() ||
                        line.offset() == caretLine.offset()) {
                        continue;
                    }
                    carets.add(line.xToOffset(x));
                }
            }
            return;
        }
        // select line
        texts.at(y).ifPresent(textLine ->
            Selections.select(textLine.offset(), textLine.tailOffset(), carets, selection)
        );
    }

    private void vScrollToCaret() {

        // In multi carets, all carets must fit within the screen
        List<Caret> list = carets.list();
        Caret min = list.stream().min(Comparator.naturalOrder()).get();
        Caret max = list.stream().max(Comparator.naturalOrder()).get();
        Caret base = (min.offset() < texts.head().offset()) ? min : max;

        boolean scrolled = texts.scrollAtScreen(base.row(), base.offset());
        if (!scrolled) return;

        texts.markDirty();
        carets.refresh();
        screen.vScrolled(texts.headlinesIndex());
    }


    /**
     * Get the number of total lines.
     * @return the number of total lines
     */
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
        if (!selection.hasSelection()) {
            return;
        }
        String text = selection.getRanges().stream()
            .sorted(Comparator.comparing(OffsetPointRange::minOffsetPoint))
            .map(range -> {
                OffsetPoint point = range.minOffsetPoint();
                return buffer.subText(point, (int) Long.min(range.length(), Integer.MAX_VALUE));
            })
            .collect(Collectors.joining(LineEnding.platform().str()));
        Clipboards.put(text);
        if (cut && !buffer.readOnly()) {
            selectionDelete();
        }
    }


    /**
     * Run query.
     * @param <R> the type of result value
     * @param query a query
     * @return the result value
     */
    private <R> ModelQuery.Result<R> run(ModelQuery<R> query) {
        @SuppressWarnings("unchecked")
        ModelQuery.Result<R> r = (ModelQuery.Result<R>) switch (query) {
            case ModelQuery.SelectedText q -> {
                if (!selection.hasSelection()) {
                    yield q.on("");
                }
                OffsetPointRange range = selection.getRanges().getFirst();
                yield q.on(buffer.subText(range.minOffsetPoint(), (int) Long.min(range.length(), Integer.MAX_VALUE)));
            }
            case ModelQuery.CurrentLineText q ->
                q.on(texts.lineAt(carets.offset()).text());
            case ModelQuery.CurrentRowText q ->
                q.on(buffer.rowSupplier().at(carets.offset()));
            case ModelQuery.CaretBeforeText q ->
                q.on(buffer.rowSupplier().before(carets.offsetPoint().cpOffset()));
            case ModelQuery.Modified q ->
                q.on(buffer.metrics().modified());
            case ModelQuery.ContentPath q ->
                q.on(buffer.metrics().path());
        };
        return  r;
    }

}
