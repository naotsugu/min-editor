/*
 * Copyright 2023-2025 the original author or authors.
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
import com.mammb.code.editor.core.Decorate;
import com.mammb.code.editor.core.Draw;
import com.mammb.code.editor.core.Loc;
import com.mammb.code.editor.core.Point;
import com.mammb.code.editor.core.ScreenScroll;
import com.mammb.code.editor.core.Theme;
import com.mammb.code.editor.core.layout.ScreenLayout;
import com.mammb.code.editor.core.text.Style;
import com.mammb.code.editor.core.text.StyledText;
import com.mammb.code.editor.core.text.Symbols;
import com.mammb.code.editor.core.text.Text;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The paints.
 * @author Naotsugu Kobayashi
 */
public class Paints {

    /**
     * Paint the selection.
     * @param draw the draw
     * @param marginTop the margin top
     * @param marginLeft the margin left
     * @param screenLayout the screen layout
     * @param scroll the screen scroll
     * @param carets the carets
     */
    static void selection(Draw draw,
            double marginTop, double marginLeft,
            ScreenLayout screenLayout, ScreenScroll scroll, CaretGroup carets) {
        for (Point.Range r : carets.marked()) {
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

    /**
     * Paint the text.
     * @param draw the draw
     * @param marginTop the margin top
     * @param marginLeft the margin left
     * @param screenLayout the screen layout
     * @param decorate the decorate
     * @param scroll the screen scroll
     * @param carets the carets
     */
    static void text(Draw draw,
            double marginTop, double marginLeft,
            ScreenLayout screenLayout, Decorate decorate,
            ScreenScroll scroll, CaretGroup carets) {
        double x, y = 0;
        double prevRow = -1;
        List<Style.StyleSpan> spans = List.of();
        for (Text text : screenLayout.texts()) {
            x = 0;
            if (text.row() != prevRow) {
                // update the spans only if the row is different from the previous one
                // reuse the previous style for the same row
                spans = decorate.apply(text);
                prevRow = text.row();
            }
            for (StyledText st : StyledText.of(text, spans)) {
                double px = x + marginLeft - scroll.xVal();
                double py = y + marginTop;
                draw.text(st, px, py, st.width(), st.styles());
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
                                screenLayout.standardCharWidth(),
                                screenLayout.lineHeight(), "#80808088"));
                        }
                    }
                }
                x += st.width();
            }
            y += text.height();
        }
    }

    /**
     * Paint the map.
     * @param draw the draw
     * @param marginTop the margin top
     * @param marginLeft the margin left
     * @param screenLayout the screen layout
     * @param decorate the decorate
     */
    static void map(Draw draw,
            double marginTop, double marginLeft,
            ScreenLayout screenLayout, Decorate decorate) {
        for (int row : decorate.highlightsRows()) {
            int line = screenLayout.rowToFirstLine(row);
            double y = (screenLayout.screenHeight() - marginTop) * line / (screenLayout.lineSize() + screenLayout.screenLineSize());
            draw.hLine(screenLayout.screenWidth() + marginLeft - 12, y, 12);
        }
    }

    /**
     * Paint the caret.
     * @param draw the draw
     * @param marginTop the margin top
     * @param marginLeft the margin left
     * @param caretVisible caretVisible?
     * @param screenLayout the screen layout
     * @param scroll the screen scroll
     * @param carets the carets
     */
    static void caret(Draw draw,
            double marginTop, double marginLeft, boolean caretVisible,
            ScreenLayout screenLayout, ScreenScroll scroll, CaretGroup carets) {
        if (!caretVisible) return;
        for (Caret c : carets.carets()) {
            Point p = c.flushedPoint();
            screenLayout.locationOn(p.row(), p.col()).ifPresent(loc -> {
                draw.caret(loc.x() + marginLeft - scroll.xVal(), loc.y() + marginTop);
                if (c.hasImeFlush()) {
                    screenLayout.locationOn(c.point().row(), c.point().col()).ifPresent(org ->
                        draw.underline(org.x() + marginLeft - scroll.xVal(),
                            loc.x() + marginLeft - scroll.xVal(),
                            loc.y() + marginTop));
                }
            });
        }
    }

    /**
     * Paint the left garter.
     * @param draw the draw
     * @param marginTop the margin top
     * @param marginLeft the margin left
     * @param screenLayout the screen layout
     * @param carets the carets
     */
    static void leftGarter(Draw draw,
            double marginTop, double marginLeft,
            ScreenLayout screenLayout,
            CaretGroup carets) {
        draw.rect(0, 0, marginLeft - 5, screenLayout.screenHeight() + marginTop);
        double y = 0;
        String prevValue = "";
        for (Text num : screenLayout.lineNumbers()) {
            // if the text is wrapped, display the row number only on the first line.
            if (!Objects.equals(prevValue, num.value())) {
                String colorString = carets.points().stream().anyMatch(p -> p.row() == num.row())
                    ? Theme.dark.fgColor()
                    : Theme.dark.fgColor() + "66";
                draw.text(num, marginLeft - 16 - num.width(), y + marginTop, num.width(),
                    List.of(new Style.TextColor(colorString)));
            }
            prevValue = num.value();
            y += num.height();
        }
    }

}
