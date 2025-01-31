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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import com.mammb.code.editor.core.Action;
import com.mammb.code.editor.core.CaretGroup;
import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.Context;
import com.mammb.code.editor.core.Draw;
import com.mammb.code.editor.core.EditorModel;
import com.mammb.code.editor.core.FloatOn;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.Loc;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.ScreenScroll;
import com.mammb.code.editor.core.Session;
import com.mammb.code.editor.core.text.RowText;
import com.mammb.code.editor.core.text.Text;

/**
 * The csv editor model.
 * @author Naotsugu Kobayashi
 */
public class CsvEditorModel implements EditorModel {

    /** The content. */
    private final Content content;
    /** The font metrics. */
    private FontMetrics fm;
    /** The screen scroll. */
    private final ScreenScroll scroll;
    /** The carets. */
    private final CaretGroup carets = CaretGroup.of();
    /** The buffer of text. */
    private final List<Text> buffer = new ArrayList<>();
    /** The screen width. */
    private double screenWidth = 0;
    /** The screen height. */
    private double screenHeight = 0;
    /** The row number at the top of the screen. */
    private int topRow = 0;
    /** The x-axis screen scroll size. */
    private double xShift = 0;

    public CsvEditorModel(Content content, FontMetrics fm, ScreenScroll scroll, Context ctx) {
        this.content = content;
        this.fm = fm;
        this.scroll = scroll;
    }


    @Override
    public void draw(Draw draw) {
    }

    @Override
    public void setSize(double width, double height) {
        if (screenWidth != width || screenHeight != height) {
            screenWidth = width;
            screenHeight = height;
            fillBuffer();
        }
    }

    @Override
    public void scrollNext(int delta) {
    }

    @Override
    public void scrollPrev(int delta) {
    }

    @Override
    public void scrollAt(int line) {
    }

    @Override
    public void scrollX(double x) {
    }

    @Override
    public void scrollToCaretY() {
    }

    @Override
    public void scrollToCaretX() {
    }

    @Override
    public void mousePressed(double x, double y) {
    }

    @Override
    public void click(double x, double y, boolean withSelect) {
    }

    @Override
    public void ctrlClick(double x, double y) {
    }

    @Override
    public void clickDouble(double x, double y) {
    }

    @Override
    public void clickTriple(double x, double y) {
    }

    @Override
    public void moveDragged(double x, double y) {
    }

    @Override
    public FloatOn floatLoc(double x, double y) {
        return null;
    }

    @Override
    public void setCaretVisible(boolean visible) {
    }

    @Override
    public Optional<Path> path() {
        return Optional.empty();
    }

    @Override
    public void save(Path path) {
    }

    @Override
    public void updateFonts(FontMetrics fontMetrics) {
    }

    @Override
    public void imeOn() {
    }

    @Override
    public Optional<Loc> imeLoc() {
        return Optional.empty();
    }

    @Override
    public void imeOff() {
    }

    @Override
    public boolean isImeOn() {
        return false;
    }

    @Override
    public void imeComposed(String text) {
    }

    @Override
    public Session getSession() {
        return null;
    }

    @Override
    public void apply(Action action) {
    }

    @Override
    public <R> R query(Query<R> query) {
        return null;
    }

    private void fillBuffer() {
        buffer.clear();
        IntStream.rangeClosed(topRow, topRow + screenLineSize())
            .mapToObj(row -> RowText.of(row, content.getText(row), fm))
            .forEach(buffer::addLast);
    }

    private int screenLineSize() {
        return (int) Math.ceil(Math.max(0, screenHeight) / fm.getLineHeight());
    }

}
