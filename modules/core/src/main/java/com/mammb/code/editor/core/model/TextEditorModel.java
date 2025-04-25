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
import com.mammb.code.editor.core.Find;
import com.mammb.code.editor.core.FindSpec;
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
import com.mammb.code.editor.core.syntax.Syntax;
import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.Style.StyleSpan;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.mammb.code.editor.core.model.ActionRecords.*;

/**
 * The text editor model.
 * @author Naotsugu Kobayashi
 */
public class TextEditorModel implements EditorModel {

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
     * @param fm the font metrics
     * @param scroll the screen scroll
     * @param ctx the context
     */
    public TextEditorModel(Content content, FontMetrics fm, ScreenScroll scroll, Context ctx) {
        this.content = content;
        this.screenLayout = ScreenLayout.of(content, fm);
        this.decorate = Decorate.of(content.path().map(Syntax::of).orElse(Syntax.of("md"))); // default syntax is md
        this.scroll = scroll;
        this.ctx = ctx;
        this.find = content.find();
    }

    /**
     * Constructor.
     * @param session the session
     * @param fm the font metrics
     * @param scroll the screen scroll
     * @param ctx the context
     */
    public TextEditorModel(Session session, FontMetrics fm, ScreenScroll scroll, Context ctx, double width, double height) {
        this(session.hasPath() ? Content.of(session.path()) : Content.of(), fm, scroll, ctx);
        if (session.lineWidth() > 0) wrap(session.lineWidth());
        setSize(width, height);
        scrollAt(session.topLine());
        carets.getFirst().at(session.caretRow(), session.caretCol());
    }

    @Override
    public void paint(Draw draw) {
        screenLayout.applyScreenScroll(scroll);
        calcScreenLayout();
        draw.clear();
        Paints.selection(draw, marginTop, marginLeft, screenLayout, scroll, carets);
        Paints.text(draw, marginTop, marginLeft, screenLayout, decorate, scroll, carets);
        Paints.map(draw, marginTop, marginLeft, screenLayout, decorate);
        Paints.caret(draw, marginTop, marginLeft, caretVisible, screenLayout, scroll, carets);
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
        int line = screenLayout.rowToLine(c.row(), c.col());
        if (line - screenLayout.topLine() < 0) {
            int nLine = Math.max(0, line - gap);
            scrollAt(nLine);
        } else if (line - (screenLayout.topLine() + screenLayout.screenLineSize() - 3) > 0) {
            int nLine = line - screenLayout.screenLineSize() + 3 + gap;
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
            c.at(screenLayout.lineToRow(line), screenLayout.xToCol(line, x), x);
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
            c.at(screenLayout.lineToRow(line), screenLayout.xToCol(line, x), x);
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
        Caret c = carets.unique();
        c.at(0, 0);
        c.mark();
        c.at(screenLayout.rowSize() - 1, screenLayout.endColOnRow(screenLayout.lineSize() - 1));
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
        Caret c = carets.unique();
        int line = screenLayout.yToLineOnScreen(y - marginTop);
        c.at(screenLayout.lineToRow(line), screenLayout.xToMidCol(line, x - marginLeft + screenLayout.xShift()));
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
                            screenLayout.xToCol(line, caretX)))
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
        preEditing();
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
        postEditing();
    }

    private void delete() {
        preEditing();
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
        postEditing();
    }

    private void backspace() {
        preEditing();
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
        postEditing();
    }

    private void replace(Function<String, String> fun, boolean keepSelection) {
        preEditing();
        List<Range> ranges = content.replace(carets.ranges(), fun);
        Range rangeMin = Collections.min(ranges);
        Range rangeMax = Collections.max(ranges);
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
        postEditing();
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
        preEditing();
        List<Point> points = content.undo();
        screenLayout.refreshBuffer();
        carets.at(points);
        postEditing();
    }

    private void redo() {
        preEditing();
        List<Point> points = content.redo();
        screenLayout.refreshBuffer();
        carets.at(points);
        postEditing();
    }

    private void pasteFromClipboard(Clipboard clipboard, boolean withOpt) {
        if (!withOpt && Objects.equals(decorate.syntaxName(), "md")) {
            var html = clipboard.getHtml();
            if (html != null && html.contains("<table") && html.contains("</table>")) {
                var mdTable = EditingFunctions.markdownTable.apply(html);
                if (!mdTable.isEmpty()) {
                    input(mdTable);
                    return;
                }
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
        Path oldPath = content.path().orElse(null);
        content.save(path);
        if (!Objects.equals(path, oldPath)) {
            decorate = Decorate.of(Syntax.of(path));
        }
    }

    private void escape() {
        carets.unique().clearMark();
        decorate.clear();
        find.clear();
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
        Style style = new Style.BgColor(Theme.dark.cautionColor());
        find.all(spec).forEach(p -> {
            if (!c.isMarked()) {
                c.markTo(p.row(), p.col(), p.row(), p.col() + p.len());
                scrollToCaretY(screenLayout.screenLineHalfSize());
            }
            decorate.addHighlights(p.row(), new StyleSpan(style, p.col(), p.len()));
        });
    }

    private void select(Find.Spec spec) {
        boolean finded = false;
        for (Point.PointLen pl : find.all(spec)) {
            if (!finded) {
                var c = carets.unique();
                c.markTo(pl.row(), pl.col(), pl.row(), pl.col() + pl.len());
                finded = true;
            } else {
                var c = carets.add(pl.row(), pl.col());
                c.mark();
                c.at(pl.row(), pl.col() + pl.len());
            }
        }
        find.clear();
        if (finded) {
            scrollToCaretY(screenLayout.screenLineHalfSize());
        }
    }

    @Override
    public Session getSession() {
        return new Session.SessionRecord(
            content.path().orElse(null),
            content.path().map(TextEditorModel::lastModifiedTime).orElse(null),
            screenLayout.topLine(),
            screenLayout.charsInLine(),
            carets.getFirst().row(),
            carets.getFirst().col(),
            System.currentTimeMillis());
    }

    @Override
    public void apply(Action action) {

        if (isImeOn() || action.isEmpty()) return;

        switch (action) {
            case Input a        -> input(a.attr());
            case Delete _       -> delete();
            case Backspace _    -> backspace();
            case Undo _         -> undo();
            case Redo _         -> redo();
            case Home a         -> moveCaretHome(a.withSelect());
            case End a          -> moveCaretEnd(a.withSelect());
            case Tab a          -> inputTab(a.withSelect());
            case CaretRight a   -> moveCaretRight(a.withSelect(), a.withShortcut());
            case CaretLeft a    -> moveCaretLeft(a.withSelect(), a.withShortcut());
            case CaretUp a      -> moveCaretUp(a.withSelect(), a.withShortcut());
            case CaretDown a    -> moveCaretDown(a.withSelect(), a.withShortcut());
            case PageUp a       -> moveCaretPageUp(a.withSelect());
            case PageDown a     -> moveCaretPageDown(a.withSelect());
            case Copy a         -> copyToClipboard(a.attr());
            case Cut a          -> cutToClipboard(a.attr());
            case Paste a        -> pasteFromClipboard(a.attr(), a.withOpt());
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
            case Replace a      -> replace(a.attr(), a.keepSelect());
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
            case QueryRecords.SelectedCounts _    -> (R) selectedCounts();
            case QueryRecords.LineSize _          -> (R) Integer.valueOf(screenLayout.lineSize());
            case QueryRecords.RowSize _           -> (R) Integer.valueOf(screenLayout.rowSize());
            case QueryRecords.SelectedText _      -> (R) carets.marked().stream().findFirst()
                                                               .map(range -> content.getText(range.min(), range.max())).orElse("");
            case null -> null;
            default -> content.query(query);
        };
    }

    // -- private -------------------------------------------------------------

    private void preEditing() {
        decorate.clear();
    }

    private void postEditing() {
        Style style = new Style.BgColor(Theme.dark.cautionColor());
        find.founds().forEach(p -> {
            decorate.addHighlights(p.row(), new StyleSpan(style, p.col(), p.len()));
        });
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

    private Integer selectedCounts() {
        if (carets.size() > 1) {
            return carets.size();
        } else {
            var c = carets.getFirst();
            if (c.isMarked()) {
                var p = c.point();
                var m = c.markedPoint();
                // TODO count characters seriously
                if (p.row() == m.row()) {
                    return Math.abs(p.col() - m.col());
                }
            }
        }
        return 0;
    }

    private static FileTime lastModifiedTime(Path path) {
        try {
            return Files.getLastModifiedTime(path);
        } catch (IOException ignore) { }
        return null;
    }

}
