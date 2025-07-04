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
package com.mammb.code.editor.core.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.Loc;
import com.mammb.code.editor.core.ScreenScroll;
import com.mammb.code.editor.core.text.Text;

/**
 * The basic screen layout.
 * @author Naotsugu Kobayashi
 */
class BasicScreenLayout implements ScreenLayout {

    /** The screen width. */
    private double screenWidth = 0;
    /** The screen height. */
    private double screenHeight = 0;
    /** The x-axis screen scroll size. */
    private double xShift = 0;
    /** The maximum size of the x-axis. */
    private double xMax = 0;
    /** The line number at the top of the screen (not a row number). */
    private int topLine = 0;
    /** The screen buffer of text. */
    private final List<Text> buffer = new ArrayList<>();
    /** The content layout. */
    private ContentLayout layout;

    /**
     * Constructor.
     * @param layout the content layout
     */
    BasicScreenLayout(ContentLayout layout) {
        this.layout = layout;
    }

    @Override
    public void setScreenSize(double width, double height) {
        if (screenWidth == width && screenHeight == height) return;
        screenWidth = width;
        screenHeight = height;
        fillBuffer();
    }

    @Override
    public void scrollNext(int lineDelta) {
        scrollAt(topLine + lineDelta);
    }

    @Override
    public void scrollPrev(int lineDelta) {
        scrollAt(topLine - lineDelta);
    }

    @Override
    public void scrollAt(int line) {

        line = Math.clamp(line, 0, lineSize() - 1);
        int delta = line - topLine;
        if (delta == 0) return;
        int screenLineSize = screenLineSize();
        if (Math.abs(delta) < screenLineSize) {
            topLine = line;
            List<? extends Text> texts;
            if (delta > 0) {
                // scroll next
                buffer.subList(0, delta).clear();
                int startLine = line + buffer.size();
                texts = layout.texts(startLine, startLine + delta);
                buffer.addAll(texts);
            } else {
                // scroll prev
                texts = layout.texts(line, line - delta);
                buffer.addAll(0, texts);
                if (buffer.size() > screenLineSize) {
                    buffer.subList(
                        buffer.size() + screenLineSize - buffer.size(),
                        buffer.size()
                    ).clear();
                }
            }
            texts.stream().mapToDouble(Text::width)
                .filter(w -> w > xMax).max()
                .ifPresent(w -> xMax = w);
        } else {
            this.topLine = line;
            fillBuffer();
        }
    }

    @Override
    public void scrollX(double x) {
        xShift = Math.max(x, 0);
    }

    @Override
    public void refreshBuffer(int startRow, int endRow) {
        layout.refreshAt(startRow, endRow);
        fillBuffer();// TODO optimize
    }

    @Override
    public void refreshBuffer() {
        layout.refresh(0);
        fillBuffer();// TODO optimize
    }

    @Override
    public List<Text> texts() {
        return buffer;
    }

    @Override
    public Text text(int line) {
        return layout.text(line);
    }

    @Override
    public Text rowTextAt(int row) {
        return layout.rowTextAt(row);
    }

    @Override
    public List<Text> lineNumbers() {
        List<Text> ret = new ArrayList<>();
        int prev = -1;
        for (Text text : buffer) {
            if (text.row() >= rowSize()) {
                break;
            }
            if (text.row() == prev) {
                // if the row was wrapped
                ret.add(Text.of(text.row(), "", new double[0], text.height()));
            } else {
                String num = String.valueOf(text.row() + 1);
                double[] advances = new double[num.length()];
                Arrays.fill(advances, layout.standardCharWidth());
                ret.add(Text.of(text.row(), num, advances, text.height()));
            }
        }
        return ret;
    }

    @Override
    public double lineNumberWidth() {
        return layout.standardCharWidth() *
            String.valueOf((buffer.isEmpty() ? 0 : buffer.getLast().row()) + 1).length();
    }

    @Override
    public Optional<Loc> locationOn(int row, int col) {
        return layout.loc(row, col, topLine, topLine + screenLineSize())
            .map(loc -> new Loc(loc.x(), loc.y() - yOnLayout(topLine)));
    }

    @Override
    public int lineSize() {
        return layout.lineSize();
    }

    @Override
    public int rowSize() {
        return layout.rowSize();
    }

    @Override
    public double yOnLayout(int line) {
        return layout.yOnLayout(line);
    }

    @Override
    public FontMetrics fontMetrics() {
        return layout.fontMetrics();
    }

    @Override
    public double standardCharWidth() {
        return layout.standardCharWidth();
    }

    @Override
    public int tabSize() {
        return layout.tabSize();
    }

    @Override
    public double xOnLayout(int line, int col) {
        return layout.xOnLayout(line, col);
    }

    @Override
    public int rowToFirstLine(int row) {
        return layout.rowToFirstLine(row);
    }

    @Override
    public int rowToLastLine(int row) {
        return layout.rowToLastLine(row);
    }

    @Override
    public int rowToLine(int row, int col) {
        return layout.rowToLine(row, col);
    }

    @Override
    public int lineToRow(int line) {
        return layout.lineToRow(line);
    }

    @Override
    public int xToCol(int line, double x) {
        return layout.xToCol(line, x);
    }

    @Override
    public int yToLineOnScreen(double y) {
        return Math.clamp(topLine + (int) (y / layout.lineHeight()), 0, lineSize() - 1);
    }

    @Override
    public int homeColOnRow(int line) {
        return layout.homeColOnRow(line);
    }

    @Override
    public int endColOnRow(int line) {
        return layout.endColOnRow(line);
    }

    @Override
    public double screenWidth() {
        return screenWidth;
    }

    @Override
    public double screenHeight() {
        return screenHeight;
    }

    @Override
    public int screenLineSize() {
        return (int) Math.ceil(Math.max(0, screenHeight) / layout.lineHeight());
    }

    @Override
    public int screenColSize() {
        return (int) Math.floor(screenWidth / layout.standardCharWidth());
    }

    @Override
    public double lineHeight() {
        return layout.lineHeight();
    }

    @Override
    public int topLine() {
        return topLine;
    }

    @Override
    public int topRow() {
        return buffer.isEmpty() ? 0 : buffer.getFirst().row();
    }

    @Override
    public double xShift() {
        return xShift;
    }

    @Override
    public boolean applyScreenScroll(ScreenScroll scroll) {
        boolean valChanged = scroll.yVal() != topLine || scroll.xVal() != xShift;
        scroll.vertical(0, layout.lineSize() - 1, topLine, screenLineSize());
        double max = xMax - Math.min(xMax, screenWidth / 2);
        scroll.horizontal(0, max, xShift, screenWidth * max / xMax);
        return valChanged;
    }

    @Override
    public void setCharsInLine(int n) {
        if (n <= 0) {
            toggleLayout("");
            return;
        }
        if (!(layout instanceof WrapLayout)) {
            // TODO reduce duplicate codes
            if (layout.rowSize() > 50_000) return; // large files are not allowed to wrap.
            layout = new WrapLayout(layout.content(), layout.fontMetrics());
        }
        layout.setCharsInLine(n);
        fillBuffer();
    }

    @Override
    public void toggleLayout(String layoutName) {
        if ("csv".equals(layoutName) || "tsv".equals(layoutName)) {
            if (layout instanceof CsvLayout) {
                layout = new RowLayout(layout.content(), layout.fontMetrics());
            } else {
                layout = "tsv".equals(layoutName)
                    ? CsvLayout.tsvOf(layout.content(), layout.fontMetrics())
                    : CsvLayout.csvOf(layout.content(), layout.fontMetrics());
            }
        } else {
            if (layout instanceof RowLayout) {
                if (layout.rowSize() > 50_000) return; // large files are not allowed to wrap.
                layout = new WrapLayout(layout.content(), layout.fontMetrics());
                layout.setCharsInLine((int) Math.floor((screenWidth - 16 /* margin right */) / layout.standardCharWidth()));
            } else {
                layout = new RowLayout(layout.content(), layout.fontMetrics());
            }
        }
        fillBuffer();
    }

    @Override
    public int charsInLine() {
        return layout.charsInLine();
    }

    @Override
    public void updateFontMetrics(FontMetrics fontMetrics) {
        layout.updateFontMetrics(fontMetrics);
        fillBuffer();
    }

    /**
     * Fill buffer.
     */
    private void fillBuffer() {
        topLine = Math.clamp(topLine, 0, layout.lineSize() - 1);
        xMax = 0;
        buffer.clear();
        buffer.addAll(layout.texts(topLine, topLine + screenLineSize()));
        buffer.stream().mapToDouble(Text::width)
            .filter(w -> w > xMax).max()
            .ifPresent(w -> xMax = w);
    }

}
