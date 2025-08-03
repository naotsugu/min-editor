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
package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Caret;
import com.mammb.code.editor.core.CaretGroup;
import com.mammb.code.editor.core.Clipboard;
import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.Context;
import com.mammb.code.editor.core.Decorate;
import com.mammb.code.editor.core.Draw;
import com.mammb.code.editor.core.EditorModel;
import com.mammb.code.editor.core.Files;
import com.mammb.code.editor.core.Find;
import com.mammb.code.editor.core.HoverOn;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Point.Range;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.ScreenScroll;
import com.mammb.code.editor.core.Session;
import com.mammb.code.editor.core.Action;
import com.mammb.code.editor.core.Theme;
import com.mammb.code.editor.core.editing.EditingFunctions;
import com.mammb.code.editor.core.Loc;
import com.mammb.code.editor.core.layout.ScreenLayout;
import com.mammb.code.editor.core.syntax.handler.PasteHandler;
import com.mammb.code.editor.core.syntax.Syntax;
import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.Style.StyleSpan;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.mammb.code.editor.core.model.ActionRecords.*;
import com.mammb.code.piecetable.RowEnding;

/**
 * The TextEditorModel represents the core functionality and state of a text editor.
 * This class implements the editor's behavior for painting, input handling, caret
 * movements, text manipulation, scrolling, and more.
 * <p>
 * It provides methods to interact with the visible content, manage user actions,
 * and store or retrieve editor sessions. The TextEditorModel handles the layout
 * of content on the editor screen and supports interaction features like text
 * selection, scrolling, clipboard operations, and input methods.
 * </p>
 * <p>
 * Fields include various aspects of the editor state such as the screen layout,
 * scrolling positions, carets, content, and user action history.
 * </p>
 * @author Naotsugu Kobayashi
 */
public class TextEditorModel implements EditorModel {

    /** logger. */
    private static final System.Logger log = System.getLogger(TextEditorModel.class.getName());

    /** The margin top. */
    private final double marginTop = 5;
    /** The margin left(garter width). */
    private double marginLeft = 5;
    /** caretVisible?. */
    private boolean caretVisible = true;
    /** The content. */
    private final Content content;
    /** The screen layout. */
    private final ScreenLayout screenLayout;
    /** The carets. */
    private final CaretGroup carets = CaretGroup.of();
    /** The screen scroll. */
    private final ScreenScroll scroll;
    /** The context. */
    private final Context ctx;
    /** The find. */
    private final Find find;
    /** The action history. */
    private final ActionHistory actionHistory = new ActionHistory();
    /** The decorate. */
    private Decorate decorate;

    /**
     * Constructor.
     * @param content the content
     * @param screenLayout the screen layout
     * @param scroll the screen scroll
     * @param ctx the context
     * @param find the find
     * @param decorate the decorate
     */
    private TextEditorModel(
            Content content,
            ScreenLayout screenLayout,
            ScreenScroll scroll,
            Context ctx,
            Find find,
            Decorate decorate) {
        this.content = content;
        this.screenLayout = screenLayout;
        this.scroll = scroll;
        this.ctx = ctx;
        this.find = find;
        this.decorate = decorate;
        this.marginLeft += screenLayout.standardCharWidth() * 8;
    }

    /**
     * Constructor.
     * @param content the content
     * @param fm the font metrics
     * @param scroll the screen scroll
     * @param ctx the context
     */
    public TextEditorModel(Content content, FontMetrics fm, ScreenScroll scroll, Context ctx) {
        this(content, ScreenLayout.of(content, fm), scroll, ctx, content.find(),
            Decorate.of(Syntax.pathOf(content.query(Query.modelName).plain()))
        );
    }

    @Override
    public void paint(Draw draw) {
        if (screenLayout.applyScreenScroll(scroll)) {
            // if scrolling occurs, paint() is called from the scroll event
            return;
        }
        calcScreenLayout();
        draw.clear();
        Paints.selection(draw, marginTop, marginLeft, screenLayout, carets);
        Paints.text(draw, marginTop, marginLeft, screenLayout, decorate, carets);
        Paints.map(draw, marginTop, marginLeft, screenLayout, decorate);
        Paints.caret(draw, marginTop, marginLeft, caretVisible, screenLayout, carets);
        Paints.leftGarter(draw, marginTop, marginLeft, screenLayout, carets);
    }

    @Override
    public void setSize(double width, double height) {
        screenLayout.setScreenSize(width - marginLeft, height - marginTop);
    }

    @Override
    public void scrollNext(int delta) {
        screenLayout.scrollNext(delta);
    }

    @Override
    public void scrollPrev(int delta) {
        screenLayout.scrollPrev(delta);
    }

    @Override
    public void scrollAt(int line) {
        int before = screenLayout.topRow();
        screenLayout.scrollAt(line);
        decorate.warmApply(before, screenLayout.topRow() - before, content);
    }

    void moveTo(int row) {
        if (decorate.isBlockScoped()) {
            int delta = screenLayout.rowToFirstLine(row) - screenLayout.topLine();
            int page = screenLayout.screenLineSize() - 1;
            int n = Math.abs(delta / page);
            int d = Math.abs(delta % page);
            for (int i = 0; i < n; i++) {
                if (delta > 0) scrollNext(page); else scrollPrev(page);
            }
            if (delta > 0) scrollNext(d); else scrollPrev(d);
        } else {
            scrollAt(screenLayout.rowToFirstLine(row));
        }
        Caret c = carets.getFirst();
        c.at(row, 0);
    }

    @Override
    public void scrollX(double x) {
        screenLayout.scrollX(x);
    }

    /**
     * Scroll to the caret position.
     */
    public void scrollToCaret() {
        scrollToCaretY();
        scrollToCaretX();
    }

    /**
     * Scroll to the caret position y.
     */
    public void scrollToCaretY() {
        scrollToCaretY(0);
    }

    private void scrollToCaretY(int gap) {
        Caret c = carets.getFirst();
        int caretLine = screenLayout.rowToLine(c.row(), c.col());
        if (caretLine - screenLayout.topLine() < 0) {
            int nLine = Math.max(0, caretLine - gap);
            scrollAt(nLine);
        } else if (caretLine - (screenLayout.topLine() + screenLayout.screenLineSize() - 3) > 0) {
            int nLine = caretLine - screenLayout.screenLineSize() + 3 + gap;
            scrollAt(Math.clamp(nLine, 0, screenLayout.lineSize() - screenLayout.screenLineSize() + 3));
        }
    }

    /**
     * Scroll to the caret position x.
     */
    public void scrollToCaretX() {
        Caret c = carets.getFirst();
        int line = screenLayout.rowToLine(c.row(), c.col());
        double caretX = screenLayout.xOnLayout(line, c.col());
        double left = screenLayout.xShift();
        double right = left + screenLayout.screenWidth();
        double gap = screenLayout.standardCharWidth();
        if (!(left <= caretX && caretX <= (right - gap))) {
            // caret present off-screen
            double margin = screenLayout.screenWidth() / 6;
            if (caretX > (right - gap)) {
                screenLayout.scrollX(left + (caretX - right) + margin);
            } else {
                screenLayout.scrollX(left - (left - caretX) - margin);
            }
        }
    }

    private void moveCaretRight(boolean withSelect, boolean withShortcut) {
        for (Caret c : carets.carets()) {
            c.markIf(withSelect);
            var text = screenLayout.rowTextAt(c.row());
            if (text == null) continue;
            int nextCol = withShortcut ? text.indexRightBound(c.col()) : text.indexRight(c.col());
            if (nextCol <= 0) {
                if (c.row() != screenLayout.rowSize() - 1) {
                    int nextRow = Math.min(screenLayout.rowSize() - 1, c.row() + 1);
                    c.at(nextRow, 0);
                }
            } else {
                c.at(c.row(), nextCol);
            }
        }
        scrollToCaretX();
    }

    private void moveCaretLeft(boolean withSelect, boolean withShortcut) {
        for (Caret c : carets.carets()) {
            c.markIf(withSelect);
            if (c.isZero()) continue;
            if (c.col() == 0) {
                var text = screenLayout.rowTextAt(c.row() - 1);
                c.at(c.row() - 1, text.textLength());
            } else {
                var text = screenLayout.rowTextAt(c.row());
                int next = withShortcut ? text.indexLeftBound(c.col()) : text.indexLeft(c.col());
                c.at(c.row(), next);
            }
        }
        scrollToCaretX();
    }

    private void moveCaretDown(boolean withSelect, boolean withShortcut) {
        for (Caret c : carets.carets()) {
            c.markIf(withSelect);
            int line = screenLayout.rowToLine(c.row(), c.col());
            int max = screenLayout.lineSize() - 1;
            if (line >= max) continue;
            double x = (c.vPos() < 0)
                    ? screenLayout.xOnLayout(line, c.col())
                    : c.vPos();
            line = Math.min(withShortcut ? line + screenLayout.screenLineSize() / 2 : line + 1, max);
            c.at(screenLayout.lineToRow(line), screenLayout.xToCaretCol(line, x), x);
        }
    }

    private void moveCaretUp(boolean withSelect, boolean withShortcut) {
        for (Caret c : carets.carets()) {
            c.markIf(withSelect);
            int line = screenLayout.rowToLine(c.row(), c.col());
            if (line == 0) continue;
            double x = (c.vPos() < 0)
                    ? screenLayout.xOnLayout(line, c.col())
                    : c.vPos();
            line = Math.max(withShortcut ? line - screenLayout.screenLineSize() / 2 : line - 1, 0);
            c.at(screenLayout.lineToRow(line), screenLayout.xToCaretCol(line, x), x);
        }
    }

    private void moveCaretHome(boolean withSelect) {
        for (Caret c : carets.carets()) {
            c.markIf(withSelect);
            int line = screenLayout.rowToLine(c.row(), c.col());
            c.at(c.row(), screenLayout.homeColOnRow(line));
        }
        screenLayout.scrollX(0);
    }

    private void moveCaretEnd(boolean withSelect) {
        for (Caret c : carets.carets()) {
            c.markIf(withSelect);
            int line = screenLayout.rowToLine(c.row(), c.col());
            c.at(c.row(), screenLayout.endColOnRow(line));
        }
        scrollToCaretX();
    }

    private void moveCaretPageUp(boolean withSelect) {
        int n = screenLayout.screenLineSize() - 1;
        scrollPrev(n);
        if (withSelect && carets.size() > 1) carets.unique();
        if (carets.size() == 1) {
            Caret c = carets.getFirst();
            c.markIf(withSelect);
            int line = screenLayout.rowToLine(c.row(), c.col());
            double x = screenLayout.xOnLayout(line, c.col());
            c.at(screenLayout.lineToRow(line - n), screenLayout.xToCol(line, x));
        }
    }

    private void moveCaretPageDown(boolean withSelect) {
        int n = screenLayout.screenLineSize() - 1;
        scrollNext(n);
        if (withSelect && carets.size() > 1) carets.unique();
        if (carets.size() == 1) {
            Caret c = carets.getFirst();
            c.markIf(withSelect);
            int line = screenLayout.rowToLine(c.row(), c.col());
            double x = screenLayout.xOnLayout(line, c.col());
            c.at(screenLayout.lineToRow(line + n), screenLayout.xToCol(line, x));
        }
    }

    private void selectAll() {
        carets.unique().markTo(0, 0,
            screenLayout.rowSize() - 1, screenLayout.endColOnRow(screenLayout.lineSize() - 1));
    }

    @Override
    public void mousePressed(double x, double y) {
        Caret c = carets.getFirst();
        if (c.isFloating()) {
            c.clearFloat();
        }
        c.clearMark();
    }

    @Override
    public void click(double x, double y, boolean withSelect) {
        int line = screenLayout.yToLineOnScreen(y - marginTop);
        int row = screenLayout.lineToRow(line);
        if (x < marginLeft) {
            if (carets.carets().stream().noneMatch(Caret::isFloating)) {
                // select line
                carets.unique().markTo(row, 0, row, screenLayout.endColOnRowAt(row));
            }
        } else {
            carets.unique().at(row, screenLayout.xToMidCol(line, x - marginLeft + screenLayout.xShift()));
        }
    }

    @Override
    public void ctrlClick(double x, double y) {
        if (x < marginLeft) {
            Caret c = carets.unique();
            int clickLine = screenLayout.yToLineOnScreen(y - marginTop);
            int caretLine = screenLayout.rowToLine(c.row(), c.col());
            double caretX = screenLayout.xOnLayout(caretLine, c.col());
            if (clickLine == caretLine) return;
            IntStream stream = (clickLine < caretLine)
                    ? IntStream.rangeClosed(clickLine, caretLine - 1)
                    : IntStream.rangeClosed(caretLine + 1, clickLine);
            carets.add(stream
                    .mapToObj(line -> Point.of(
                            screenLayout.lineToRow(line),
                            screenLayout.xToCaretCol(line, caretX)))
                    .toList());
        } else {
            int line = screenLayout.yToLineOnScreen(y - marginTop);
            var point = Point.of(
                screenLayout.lineToRow(line),
                screenLayout.xToMidCol(line, x - marginLeft + screenLayout.xShift()));
            carets.toggle(point);
        }
    }

    @Override
    public void clickDouble(double x, double y) {
        int line = screenLayout.yToLineOnScreen(y - marginTop);
        var text = screenLayout.text(line);
        double xp = 0;
        for (var word : text.words()) {
            if (xp + word.width() > x - marginLeft + screenLayout.xShift()) {
                Caret c = carets.getFirst();
                int row = screenLayout.lineToRow(line);
                c.markTo(row, screenLayout.xToCol(line, xp),
                        row, screenLayout.xToCol(line, xp + word.width()));
                break;
            }
            xp += word.width();
        }
    }

    @Override
    public void clickTriple(double x, double y) {
        int line = screenLayout.yToLineOnScreen(y - marginTop);
        int row = screenLayout.lineToRow(line);
        Caret c = carets.unique();
        c.markTo(row, screenLayout.xToCol(line, 0), row, screenLayout.xToCol(line, Double.MAX_VALUE));
    }

    @Override
    public void moveDragged(double x, double y) {
        int line = screenLayout.yToLineOnScreen(y - marginTop);
        int row = screenLayout.lineToRow(line);
        int col = screenLayout.xToMidCol(line, x - marginLeft + screenLayout.xShift());
        Caret c = carets.getFirst();
        c.floatAt(row, col);
        c.markIf(true);
        scrollToCaret();
    }

    @Override
    public HoverOn hoverOn(double x, double y) {
        return (x < marginLeft) ? HoverOn.garterRegion : HoverOn.editRegion;
    }

    @Override
    public void setCaretVisible(boolean visible) {
        this.caretVisible = visible;
    }

    private void input(String text) {
        if (carets.size() == 1) {
            Caret c = carets.getFirst();
            if (c.isMarked()) {
                selectionReplace(c, text);
            } else {
                var pos = content.insert(c.point(), text);
                screenLayout.refreshBuffer(c.row(), pos.row());
                c.at(pos);
            }
        } else {
            if (carets.hasMarked()) {
                replace(_ -> text, false);
            } else {
                List<Point> points = content.insert(carets.points(), text);
                refreshPointsRange(points);
            }
        }
    }

    private void delete() {
        if (carets.size() == 1) {
            Caret c = carets.getFirst();
            if (c.isMarked()) {
                selectionReplace(c, "");
            } else {
                var del = content.delete(c.point());
                screenLayout.refreshBuffer(c.row(),
                    c.row() + (int) del.chars().mapToLong(d -> d == '\n' ? 1 : 0).sum());
            }
        } else {
            if (carets.hasMarked()) {
                replace(_ -> "", false);
            } else {
                List<Point> points = content.delete(carets.points());
                refreshPointsRange(points);
            }
        }
    }

    private void backspace() {
        if (carets.size() == 1) {
            Caret c = carets.getFirst();
            if (c.isMarked()) {
                selectionReplace(c, "");
            } else {
                var pos = content.backspace(c.point());
                screenLayout.refreshBuffer(pos.row(), c.row());
                c.at(pos);
            }
        } else {
            if (carets.hasMarked()) {
                replace(_ -> "", false);
            } else {
                List<Point> points = content.backspace(carets.points());
                refreshPointsRange(points);
            }
        }
    }

    private void replace(Function<String, String> fun, boolean keepSelection) {
        List<Range> caretRanges = carets.ranges();
        List<Range> ranges = content.replace(caretRanges, fun);
        Range rangeMin = Collections.min(caretRanges);
        Range rangeMax = Collections.max(caretRanges);
        if (keepSelection) {
            List<Caret> caretList = carets.carets();
            for (int i = 0; i < caretList.size(); i++) {
                Caret c = caretList.get(i);
                Range range = ranges.get(i);
                if (range.isAsc()) {
                    c.markTo(range.end(), range.start());
                } else {
                    c.markTo(range.start(), range.end());
                }
            }
        } else {
            carets.at(ranges.stream().map(r -> r.isAsc() ? r.start() : r.end()).toList());
        }
        screenLayout.refreshBuffer(
            rangeMin.min().row(),
            rangeMax.max().row());
    }

    private void inputTab(boolean sc) {
        if (carets.hasMarked()) {
            replace(sc ? EditingFunctions.unindent : EditingFunctions.indent, true);
        } else {
            int ts = screenLayout.tabSize();
            for (Caret c : carets.carets().stream().sorted().toList()) {
                var text = content.getText(c.row());
                int shift = 0;
                for (int i = 0; i < c.col(); i++, shift++) {
                    if (text.charAt(i) == '\t') {
                        int sp = (shift < ts) ? ts - shift : ts - (shift % ts);
                        shift += (sp - 1);
                    }
                }
                var pos = content.insert(c.point(),
                    " ".repeat((shift < ts) ? ts - shift : ts - (shift % ts)));
                screenLayout.refreshBuffer(c.row(), pos.row());
                c.at(pos);
            }
        }
    }

    private void undo() {
        List<Point> points = content.undo();
        screenLayout.refreshBuffer();
        carets.at(points);
    }

    private void redo() {
        List<Point> points = content.redo();
        screenLayout.refreshBuffer();
        carets.at(points);
    }

    private void pasteFromClipboard(Clipboard clipboard, boolean withOpt) {
        if (!withOpt) {
            PasteHandler handler = decorate.syntaxHandler(PasteHandler.class);
            if (handler != null && handler.handlePaste(clipboard, this::input)) {
                return;
            }
        }
        var text = clipboard.getString();
        text = (text == null || text.isEmpty()) ? clipboard.getHtml() : text;
        if (text == null || text.isEmpty()) return;
        input(EditingFunctions.sanitize.apply(text));
    }

    private void copyToClipboard(Clipboard clipboard) {
        String copy = carets.marked().stream()
                .map(range -> content.getText(range.min(), range.max()))
                .collect(Collectors.joining(System.lineSeparator()));
        if (copy.isEmpty()) return;
        clipboard.setPlainText(copy);
    }

    private void cutToClipboard(Clipboard clipboard) {
        copyToClipboard(clipboard);
        replace(_ -> "", false);
    }

    @Override
    public void save(Path path) {
        boolean syntaxChanged = !Objects.equals(
            Syntax.syntaxName(content.path().orElse(null)),
            decorate.syntaxName());
        content.save(path);
        if (syntaxChanged) {
            decorate = Decorate.of(Syntax.pathOf(path));
        }
    }

    @Override
    public void saveWith(Charset charset, String rowEndingSymbol) {
        Path path = content.path().orElseThrow();
        Charset currentCharset = content.query(Query.charset);
        Charset newCharset = (charset == null) ? currentCharset : charset;

        RowEnding currentRowEnding = RowEnding.of(content.query(Query.rowEndingSymbol));
        RowEnding newRowEnding = (rowEndingSymbol == null || rowEndingSymbol.isBlank())
            ? currentRowEnding : RowEnding.of(rowEndingSymbol);

        if (Objects.equals(currentRowEnding, newRowEnding) && Objects.equals(currentCharset, newCharset)) {
            return;
        }
        save(path);
        close();
        Files.writeWith(path, currentCharset, newCharset, newRowEnding.str());
        reload();
    }

    @Override
    public void reload() {
        content.reload();
        moveTo(0);
        escape();
    }

    @Override
    public void close() {
        content.close();
    }

    @Override
    public Session stash() {
        Path stashPath = ctx.config().stashPath().resolve(
            String.join("_", UUID.randomUUID().toString(),
            query(Query.modelName).plain()));
        content.write(stashPath);
        return Session.of(
            content.path().orElse(null),
            content.path().map(Files::lastModifiedTime).orElse(null),
            stashPath,
            content.readonly(),
            screenLayout.topLine(), screenLayout.charsInLine(),
            carets.getFirst().row(), carets.getFirst().col());
    }

    /**
     * Clear current state.
     */
    private void escape() {
        carets.unique().clearMark();
        decorate.clear();
        find.clear();
        screenLayout.refreshBuffer();
    }

    /**
     * Set the width of line wrap characters.
     */
    private void wrap(int width) {
        screenLayout.setCharsInLine(width);
        scrollToCaret();
    }

    /**
     * Toggle layout.
     */
    private void toggleLayout() {
        screenLayout.toggleLayout(decorate.syntaxName());
        scrollToCaret();
    }

    @Override
    public void updateFonts(FontMetrics fontMetrics) {
        screenLayout.updateFontMetrics(fontMetrics);
    }

    @Override
    public void imeOn() {
        Caret c = carets.getFirst();
        c.imeFlushAt(c.point());
    }

    @Override
    public Optional<Loc> imeLoc() {
        Caret c = carets.getFirst();
        return screenLayout.locationOn(c.row(), c.col())
            .map(top -> new Loc(
                top.x() + marginLeft - screenLayout.xShift(),
                top.y() + marginTop + screenLayout.lineHeight() + 5));
    }

    @Override
    public void imeOff() {
        content.clearFlush();
        carets.getFirst().clearImeFlush();
    }

    @Override
    public boolean isImeOn() {
        return carets.getFirst().hasImeFlush();
    }

    @Override
    public void imeComposed(String text) {
        Caret c = carets.getFirst();
        content.clearFlush();
        var pos = content.insertFlush(c.point(), text);
        screenLayout.refreshBuffer(c.row(), pos.row());
        c.imeFlushAt(pos);
    }

    private void findNext(Find.Spec spec) {
        Caret c = carets.unique();
        var point = c.isMarked() ? Collections.max(List.of(c.point(), c.markedPoint())) : c.point();
        var found = spec.isEmpty() ? find.next(point) : find.nextOne(point, spec);
        found.ifPresent(p -> c.markTo(p.row(), p.col(), p.row(), p.col() + p.len()));
        scrollToCaretY(screenLayout.screenLineHalfSize());
    }

    private void findPrev(Find.Spec spec) {
        Caret c = carets.unique();
        var point = c.isMarked() ? Collections.min(List.of(c.point(), c.markedPoint())) : c.point();
        var found = spec.isEmpty() ? find.prev(point) : find.prevOne(point, spec);
        found.ifPresent(p -> c.markTo(p.row(), p.col(), p.row(), p.col() + p.len()));
        scrollToCaretY(screenLayout.screenLineHalfSize());
    }

    private void findAll(Find.Spec spec) {
        Caret c = carets.getFirst();
        Style style = new Style.UnderColor(Theme.dark.cautionColor());
        find.all(spec).forEach(p -> {
            if (!c.isMarked()) {
                c.markTo(p.row(), p.col(), p.row(), p.col() + p.len());
                scrollToCaretY(screenLayout.screenLineHalfSize());
            }
            decorate.addHighlights(p.row(), new StyleSpan(style, p.col(), p.len()));
        });
    }

    private void select(Find.Spec spec) {
        boolean found = false;
        for (Point.PointLen pl : find.all(spec)) {
            if (!found) {
                var c = carets.unique();
                c.markTo(pl.row(), pl.col(), pl.row(), pl.col() + pl.len());
                found = true;
            } else {
                var c = carets.add(pl.row(), pl.col());
                c.mark();
                c.at(pl.row(), pl.col() + pl.len());
            }
        }
        find.clear();
        if (found) {
            scrollToCaretY(screenLayout.screenLineHalfSize());
        }
    }

    @Override
    public Session getSession() {
        return Session.of(
            content.path().orElse(null),
            content.path().map(Files::lastModifiedTime).orElse(null),
            null,
            content.readonly(),
            screenLayout.topLine(), screenLayout.charsInLine(),
            carets.getFirst().row(), carets.getFirst().col());
    }

    @Override
    public TextEditorModel with(Session session) {
        var model = new TextEditorModel(Content.of(session), screenLayout.fontMetrics(), scroll, ctx);
        if (session.lineWidth() > 0) {
            model.wrap(session.lineWidth());
        }
        model.screenLayout.setScreenSize(screenLayout.screenWidth(), screenLayout.screenHeight());
        model.scrollAt(session.topLine());
        model.carets.getFirst().at(session.caretRow(), session.caretCol());
        return model;
    }

    @Override
    public void apply(Action action) {

        if (isImeOn() || action.isEmpty()) return;

        switch (action) {
            case Input a        -> aroundEdit(() -> input(a.attr()));
            case Delete _       -> aroundEdit(this::delete);
            case Backspace _    -> aroundEdit(this::backspace);
            case Undo _         -> aroundEdit(this::undo);
            case Redo _         -> aroundEdit(this::redo);
            case Home a         -> moveCaretHome(a.withSelect());
            case End a          -> moveCaretEnd(a.withSelect());
            case Tab a          -> aroundEdit(() -> inputTab(a.withSelect()));
            case CaretRight a   -> moveCaretRight(a.withSelect(), a.withShortcut());
            case CaretLeft a    -> moveCaretLeft(a.withSelect(), a.withShortcut());
            case CaretUp a      -> moveCaretUp(a.withSelect(), a.withShortcut());
            case CaretDown a    -> moveCaretDown(a.withSelect(), a.withShortcut());
            case PageUp a       -> moveCaretPageUp(a.withSelect());
            case PageDown a     -> moveCaretPageDown(a.withSelect());
            case Copy a         -> copyToClipboard(a.attr());
            case Cut a          -> aroundEdit(() -> cutToClipboard(a.attr()));
            case Paste a        -> aroundEdit(() -> pasteFromClipboard(a.attr(), a.withOpt()));
            case SelectAll _    -> selectAll();
            case WrapLine a     -> wrap(a.attr());
            case ToggleLayout _ -> toggleLayout();
            case Goto a         -> moveTo(a.attr());
            case FindNext a     -> findNext(a.attr());
            case FindPrev a     -> findPrev(a.attr());
            case FindAll a      -> findAll(a.attr());
            case Select a       -> select(a.attr());
            case Escape _       -> escape();
            case Repeat _       -> actionHistory.repetition().forEach(this::apply);
            case Replace a      -> aroundEdit(() -> replace(a.attr(), a.keepSelect()));
            case Save a         -> save(a.attr());
            case Empty _        -> { }
        }
        switch (action) {
            case Escape _ -> {}
            case CaretUp _, CaretDown _, PageUp _, PageDown _ -> scrollToCaretY();
            default -> scrollToCaret();
        }
        actionHistory.offer(action);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(Query<R> query) {
        return switch (query) {
            case QueryRecords.CaretPoint _        -> (R) carets.getFirst().point();
            case QueryRecords.WidthAsCharacters _ -> (R) Integer.valueOf(screenLayout.screenColSize());
            case QueryRecords.FoundCounts _       -> (R) Integer.valueOf(decorate.highlightCounts());
            case QueryRecords.SelectedCounts _    -> (R) Contents.countCodePoints(content, carets.marked());
            case QueryRecords.LineSize _          -> (R) Integer.valueOf(screenLayout.lineSize());
            case QueryRecords.RowSize _           -> (R) Integer.valueOf(screenLayout.rowSize());
            case QueryRecords.HasSelected _       -> (R) Boolean.valueOf(carets.hasMarked());
            case QueryRecords.SelectedText _      -> (R) Contents.text(content, carets.marked());
            case QueryRecords.CharAtCaret _       -> (R) Contents.lrTextAt(content, carets.getFirst().point());
            case QueryRecords.BytesAtCaret _      -> (R) Contents.bytesAt(content, carets.getFirst().point());
            case null -> null;
            default -> content.query(query);
        };
    }

    // -- private -------------------------------------------------------------

    private void aroundEdit(Runnable runnable) {
        decorate.clear();
        runnable.run();
        Style style = new Style.UnderColor(Theme.dark.cautionColor());
        find.founds().forEach(p ->
            decorate.addHighlights(p.row(), new StyleSpan(style, p.col(), p.len())));
    }

    /**
     * Calculate the screenLayout.
     */
    private void calcScreenLayout() {
        double charWidth = screenLayout.standardCharWidth();
        double minWidth = charWidth * 8;
        double width = Math.max(minWidth, screenLayout.lineNumberWidth() + charWidth * 2);
        if (marginLeft != width) {
            screenLayout.setScreenSize(
                screenLayout.screenWidth() + marginLeft - width,
                screenLayout.screenHeight());
            marginLeft = width;
        }
    }

    private Point selectionReplace(Caret caret, String text) {
        assert caret.isMarked();
        Range range = caret.markedRange();
        Point pos = content.replace(range.min(), range.max(), text);
        screenLayout.refreshBuffer(range.min().row(), range.max().row());
        caret.clearMark();
        caret.at(pos);
        return pos;
    }

    private void refreshPointsRange(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return;
        }
        screenLayout.refreshBuffer(
            Collections.min(points).row(),
            Collections.max(points).row());
        carets.at(points);
    }

}
