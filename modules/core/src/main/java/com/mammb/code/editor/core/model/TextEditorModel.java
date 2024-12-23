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
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.Point.Range;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.ScreenScroll;
import com.mammb.code.editor.core.Session;
import com.mammb.code.editor.core.Theme;
import com.mammb.code.editor.core.layout.Loc;
import com.mammb.code.editor.core.layout.ScreenLayout;
import com.mammb.code.editor.core.syntax.Syntax;
import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.Style.StyleSpan;
import com.mammb.code.editor.core.text.StyledText;
import com.mammb.code.editor.core.text.Symbols;
import com.mammb.code.editor.core.text.Text;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The text editor model.
 * @author Naotsugu Kobayashi
 */
public class TextEditorModel implements EditorModel {

    private double marginTop = 5;
    private double marginLeft = 70;
    /** caretVisible?. */
    private boolean caretVisible = true;
    /** The content. */
    private final Content content;
    /** The screen layout. */
    private final ScreenLayout screenLayout;
    /** The carets. */
    private final CaretGroup carets = CaretGroup.of();
    /** decorate. */
    private final Decorate decorate;
    /** The screen scroll. */
    private final ScreenScroll scroll;
    /** The context. */
    private final Context ctx;

    /**
     * Constructor.
     * @param content the content
     * @param fm the font metrics
     * @param syntax the syntax
     * @param scroll the screen scroll
     * @param ctx the context
     */
    public TextEditorModel(Content content, FontMetrics fm, Syntax syntax, ScreenScroll scroll, Context ctx) {
        this.content = content;
        this.screenLayout = ScreenLayout.of(content, fm);
        this.decorate = Decorate.of(syntax);
        this.scroll = scroll;
        this.ctx = ctx;
    }

    @Override
    public void draw(Draw draw) {
        screenLayout.applyScreenScroll(scroll);
        draw.clear();
        drawSelection(draw);
        drawText(draw);
        drawMap(draw);
        if (caretVisible) drawCaret(draw);
        drawLeftGarter(draw);
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
        screenLayout.scrollAt(line);
    }

    @Override
    public void moveTo(int row) {
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
            screenLayout.scrollAt(screenLayout.rowToFirstLine(row));
        }
        Caret c = carets.getFirst();
        c.at(row, 0);
    }

    @Override
    public void scrollX(double x) {
        screenLayout.scrollX(x);
    }

    @Override
    public void scrollToCaretY() {
        Caret caret = carets.getFirst();
        int line = screenLayout.rowToLine(caret.row(), caret.col());
        if (line - screenLayout.topLine() < 0) {
            screenLayout.scrollAt(line);
        } else if (line - (screenLayout.topLine() + screenLayout.screenLineSize() - 3) > 0) {
            screenLayout.scrollAt(line - screenLayout.screenLineSize() + 3);
        }
    }

    @Override
    public void scrollToCaretX() {
        Caret caret = carets.getFirst();
        int line = screenLayout.rowToLine(caret.row(), caret.col());
        double caretX = screenLayout.xOnLayout(line, caret.col());
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

    @Override
    public void moveCaretRight(boolean withSelect) {
        for (Caret c : carets.carets()) {
            c.markIf(withSelect);
            var text = screenLayout.rowTextAt(c.row());
            if (text == null) continue;
            int next = text.indexRight(c.col());
            if (next <= 0) {
                c.at(c.row() + 1, 0);
            } else {
                c.at(c.row(), next);
            }
        }
        scrollToCaretX();
    }

    @Override
    public void moveCaretLeft(boolean withSelect) {
        for (Caret c : carets.carets()) {
            c.markIf(withSelect);
            if (c.isZero()) continue;
            if (c.col() == 0) {
                var text = screenLayout.rowTextAt(c.row() - 1);
                c.at(c.row() - 1, text.textLength());
            } else {
                var text = screenLayout.rowTextAt(c.row());
                int next = text.indexLeft(c.col());
                c.at(c.row(), next);
            }
        }
        scrollToCaretX();
    }

    @Override
    public void moveCaretDown(boolean withSelect) {
        for (Caret c : carets.carets()) {
            c.markIf(withSelect);
            int line = screenLayout.rowToLine(c.row(), c.col());
            if (line >= screenLayout.lineSize() - 1) continue;
            double x = (c.vPos() < 0)
                    ? screenLayout.xOnLayout(line, c.col())
                    : c.vPos();
            line++;
            c.at(screenLayout.lineToRow(line), screenLayout.xToCol(line, x), x);
        }
    }

    @Override
    public void moveCaretUp(boolean withSelect) {
        for (Caret c : carets.carets()) {
            c.markIf(withSelect);
            int line = screenLayout.rowToLine(c.row(), c.col());
            if (line == 0) continue;
            double x = (c.vPos() < 0)
                    ? screenLayout.xOnLayout(line, c.col())
                    : c.vPos();
            line--;
            c.at(screenLayout.lineToRow(line), screenLayout.xToCol(line, x), x);
        }
    }

    @Override
    public void moveCaretHome(boolean withSelect) {
        for (Caret c : carets.carets()) {
            c.markIf(withSelect);
            int line = screenLayout.rowToLine(c.row(), c.col());
            c.at(c.row(), screenLayout.homeColOnRow(line));
        }
        screenLayout.scrollX(0);
    }

    @Override
    public void moveCaretEnd(boolean withSelect) {
        for (Caret c : carets.carets()) {
            c.markIf(withSelect);
            int line = screenLayout.rowToLine(c.row(), c.col());
            c.at(c.row(), screenLayout.endColOnRow(line));
        }
        scrollToCaretX();
    }

    @Override
    public void moveCaretPageUp(boolean withSelect) {
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

    @Override
    public void moveCaretPageDown(boolean withSelect) {
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

    @Override
    public void selectAll() {
        Caret c = carets.unique();
        c.at(0, 0);
        c.mark();
        c.at(screenLayout.rowSize(), screenLayout.endColOnRow(screenLayout.lineSize()));
    }

    @Override
    public void click(double x, double y, boolean withSelect) {
        Caret c = carets.unique();
        if (c.isFloating()) {
            c.clearFloat();
            return;
        }
        c.clearMark();
        int line = screenLayout.yToLineOnScreen(y - marginTop);
        c.at(screenLayout.lineToRow(line), screenLayout.xToCol(line, x - marginLeft));
    }

    @Override
    public void ctrlClick(double x, double y) {
        if (x < marginLeft) {
            Caret caret = carets.unique();
            int clickLine = screenLayout.yToLineOnScreen(y - marginTop);
            int caretLine = screenLayout.rowToLine(caret.row(), caret.col());
            double caretX = screenLayout.xOnLayout(caretLine, caret.col());
            if (clickLine == caretLine) return;
            var stream = (clickLine < caretLine)
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
                screenLayout.xToCol(line, x - marginLeft));
            carets.toggle(point);
        }
    }

    @Override
    public void clickDouble(double x, double y) {
        int line = screenLayout.yToLineOnScreen(y - marginTop);
        var text = screenLayout.text(line);
        double xp = 0;
        for (var word : text.words()) {
            if (xp + word.width() > x - marginLeft) {
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
        int col = screenLayout.xToCol(line, x - marginLeft);
        Caret caret = carets.getFirst();
        caret.floatAt(row, col);
        caret.markIf(true);
    }

    @Override
    public void setCaretVisible(boolean visible) {
        this.caretVisible = visible;
    }

    @Override
    public void input(String text) {
        if (carets.size() == 1) {
            Caret caret = carets.getFirst();
            if (caret.isMarked()) {
                selectionReplace(caret, text);
            } else {
                var pos = content.insert(caret.point(), text);
                screenLayout.refreshBuffer(caret.row(), pos.row() + 1);
                caret.at(pos);
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

    @Override
    public void delete() {
        if (carets.size() == 1) {
            Caret caret = carets.getFirst();
            if (caret.isMarked()) {
                selectionReplace(caret, "");
            } else {
                var del = content.delete(caret.point());
                screenLayout.refreshBuffer(caret.row(), caret.row() + 1);
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

    @Override
    public void backspace() {
        if (carets.size() == 1) {
            Caret caret = carets.getFirst();
            if (caret.isMarked()) {
                selectionReplace(caret, "");
            } else {
                var pos = content.backspace(caret.point());
                screenLayout.refreshBuffer(pos.row(), caret.row() + 1);
                caret.at(pos);
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

    @Override
    public void replace(Function<String, String> fun, boolean keepSelection) {
        List<Range> ranges = content.replace(carets.ranges(), fun);
        Range rangeMin = Collections.min(ranges);
        Range rangeMax = Collections.max(ranges);
        if (keepSelection) {
            List<Caret> caretList = carets.carets();
            for (int i = 0; i < caretList.size(); i++) {
                Caret caret = caretList.get(i);
                Range range = ranges.get(i);
                if (range.isAsc()) {
                    caret.markTo(range.start(), range.end());
                } else {
                    caret.markTo(range.end(), range.start());
                }
            }
            screenLayout.refreshBuffer(
                rangeMin.min().row(),
                rangeMax.max().row() + 1);
        } else {
            refreshPointsRange(List.of(rangeMin.min(), rangeMax.max()));
        }
    }

    @Override
    public void undo() {
        List<Point> points = content.undo();
        refreshPointsRange(points);
    }

    @Override
    public void redo() {
        List<Point> points = content.redo();
        refreshPointsRange(points);
    }

    @Override
    public void pasteFromClipboard(Clipboard clipboard) {
        var text = clipboard.getString();
        if (text.isEmpty()) return;
        input(text);
    }

    @Override
    public void copyToClipboard(Clipboard clipboard) {
        String copy = carets.marked().stream()
                .map(range -> content.getText(range.min(), range.max()))
                .collect(Collectors.joining(System.lineSeparator()));
        if (copy.isEmpty()) return;
        clipboard.setPlainText(copy);
    }

    @Override
    public void cutToClipboard(Clipboard clipboard) {
        copyToClipboard(clipboard);
        content.replace(carets.marked(), _ -> "");
    }

    @Override
    public Optional<Path> path() {
        return content.path();
    }

    @Override
    public void save(Path path) {
        content.save(path);
    }

    @Override
    public void escape() {
        carets.unique().clearMark();
        decorate.clear();
    }

    @Override
    public void wrap(int width) {
        carets.unique().at(0, 0);
        decorate.clear();
        screenLayout.wrapWith(width);
    }

    @Override
    public void updateFontMetrics(FontMetrics fontMetrics) {
        screenLayout.updateFontMetrics(fontMetrics);
    }

    @Override
    public void imeOn() {
        Caret caret = carets.getFirst();
        caret.imeFlushAt(caret.point());
    }

    @Override
    public Optional<Loc> imeLoc() {
        Caret caret = carets.getFirst();
        return screenLayout.locationOn(caret.row(), caret.col())
            .map(top -> new Loc(
                top.x() + marginLeft,
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
    public void inputImeComposed(String text) {
        Caret caret = carets.getFirst();
        content.clearFlush();
        var pos = content.insertFlush(caret.point(), text);
        screenLayout.refreshBuffer(caret.row(), pos.row() + 1);
        caret.imeFlushAt(pos);
    }

    @Override
    public void findAll(String text) {
        for (Point point : content.findAll(text)) {
            decorate.addHighlights(point.row(), new StyleSpan(
                    new Style.BgColor("#FDD835"),
                    point.col(),
                    text.length())
            );
        }
    }

    @Override
    public Session getSession() {
        return new SessionRecord(
            content.path().orElse(null),
            content.path().map(p -> {
                try {
                    return Files.getLastModifiedTime(p);
                } catch (IOException e) {
                    return null;
                }
            }).orElse(null),
            carets.getFirst().row(),
            carets.getFirst().col(),
            System.currentTimeMillis());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(Query<R> query) {
        return switch (query) {
            case QueryRecords.CaretPoint q -> (R) carets.getFirst().point();
            case QueryRecords.WidthAsCharacters q -> (R) Integer.valueOf(screenLayout.screenColSize());
            case null -> null;
            default -> content.query(query);
        };

    }

    // -- private -------------------------------------------------------------

    private void drawSelection(Draw draw) {
        for (Range r : carets.marked()) {
            Loc l1 = screenLayout.locationOn(r.min().row(), r.min().col()).orElse(null);
            Loc l2 = screenLayout.locationOn(r.max().row(), r.max().col()).orElse(null);
            if (l1 == null && l2 == null &&
                (screenLayout.rowToLine(r.min().row(), r.min().col()) > screenLayout.topLine() ||
                 screenLayout.rowToLine(r.max().row(), r.max().col()) < screenLayout.topLine())) break;
            if (l1 == null) l1 = new Loc(0, 0);
            if (l2 == null) l2 = new Loc(screenLayout.screenWidth(), screenLayout.screenHeight());
            draw.select(
                l1.x() + marginLeft - scroll.xVal(), l1.y() + marginTop,
                l2.x() + marginLeft - scroll.xVal(), l2.y() + marginTop,
                marginLeft - scroll.xVal(), screenLayout.screenWidth() + marginLeft);
        }
    }

    private void drawText(Draw draw) {
        double x, y = 0;
        for (Text text : screenLayout.texts()) {
            x = 0;
            List<StyleSpan> spans = decorate.apply(text);
            for (StyledText st : StyledText.of(text).putAll(spans).build()) {
                double px = x + marginLeft - scroll.xVal();
                double py = y + marginTop;
                draw.text(st.value(), px, py, st.width(), st.styles());
                for (var p : carets.points()) {
                    if (st.row() == p.row()) {
                        // draw special symbol
                        if (st.isEndWithCrLf()) {
                            draw.line(Symbols.crlf(
                                px + st.width() + screenLayout.standardCharWidth() * 0.2,
                                py,
                                screenLayout.standardCharWidth() * 0.8,
                                screenLayout.lineHeight() * 0.8, "#80808088"));
                        } else if (st.isEndWithLf()) {
                            draw.line(Symbols.lineFeed(
                                px + st.width() + screenLayout.standardCharWidth() * 0.2,
                                py,
                                screenLayout.standardCharWidth() * 0.8,
                                screenLayout.lineHeight() * 0.8, "#80808088"));
                        }
                        for (int i = 0; i < st.value().length(); i++) {
                            i = st.value().indexOf('　', i);
                            if (i < 0) break;
                            draw.line(Symbols.whiteSpace(
                                px + Arrays.stream(st.advances()).limit(i).sum(),
                                py - screenLayout.lineHeight() * 0.1,
                                st.advances()[i],
                                screenLayout.lineHeight(), "#80808088"));
                        }
                        for (int i = 0; i < st.value().length(); i++) {
                            i = st.value().indexOf('\t', i);
                            if (i < 0) break;
                            draw.line(Symbols.tab(
                                px + Arrays.stream(st.advances()).limit(i).sum(),
                                py - screenLayout.lineHeight() * 0.1,
                                st.advances()[i] / 4,
                                screenLayout.lineHeight(), "#80808088"));
                        }
                    }
                }
                x += st.width();
            }
            y += text.height();
        }
    }

    private void drawMap(Draw draw) {
        for (int row : decorate.highlightsRows()) {
            int line = screenLayout.rowToFirstLine(row);
            double y = (screenLayout.screenHeight() - marginTop) * line / (screenLayout.lineSize() + screenLayout.screenLineSize());
            draw.hLine(screenLayout.screenWidth() + marginLeft - 12, y, 12);
        }
    }

    private void drawCaret(Draw draw) {
        for (Caret c : carets.carets()) {
            Point p = c.flushedPoint();
            screenLayout.locationOn(p.row(), p.col()).ifPresent(loc -> {
                draw.caret(loc.x() + marginLeft - scroll.xVal(), loc.y() + marginTop);
                if (c.hasImeFlush()) {
                    screenLayout.locationOn(c.point().row(), c.point().col()).ifPresent(org ->
                        draw.underline(org.x() + marginLeft - scroll.xVal(), org.y() + marginTop,
                            loc.x() + marginLeft - scroll.xVal(), loc.y() + marginTop));
                }
            });
        }
    }

    private void drawLeftGarter(Draw draw) {
        List<Text> lineNumbers = screenLayout.lineNumbers();
        double nw = lineNumbers.stream().mapToDouble(Text::width).max().orElse(0);
        if (nw + 16 * 2 > marginLeft) {
            double newMarginLeft = nw + 8 * 2;
            screenLayout.setScreenSize(screenLayout.screenWidth() + marginLeft - newMarginLeft, screenLayout.screenHeight());
        }
        draw.rect(0, 0, marginLeft - 5, screenLayout.screenHeight() + marginTop);
        double y = 0;
        String prevValue = "";
        for (Text num : lineNumbers) {
            // If the text is wrapped, display the row number only on the first line.
            if (!Objects.equals(prevValue, num.value())) {
                String colorString = carets.points().stream().anyMatch(p -> p.row() == num.row())
                    ? Theme.dark.fgColor()
                    : Theme.dark.fgColor() + "66";
                draw.text(num.value(), marginLeft - 16 - num.width(), y + marginTop, num.width(),
                    List.of(new Style.TextColor(colorString)));
            }
            prevValue = num.value();
            y += num.height();
        }
    }

    private Point selectionReplace(Caret caret, String text) {
        assert caret.isMarked();
        var range = caret.markedRange();
        var pos = content.replace(range.min(), range.max(), text);
        screenLayout.refreshBuffer(range.min().row(), range.max().row() + 1);
        caret.clearMark();
        caret.at(pos);
        return pos;
    }

    private void refreshPointsRange(List<Point> points) {
        if (points == null || points.isEmpty()) return;
        screenLayout.refreshBuffer(
            Collections.min(points).row(),
            Collections.max(points).row() + 1);
        carets.at(points);
    }

}
