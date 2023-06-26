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
package com.mammb.code.editor2.ui.pane;

import com.mammb.code.editor.javafx.layout.FxLayoutBuilder;
import com.mammb.code.editor.javafx.layout.SpanTranslate;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.layout.LayoutTranslate;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.style.StylingTranslate;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.text.Translate;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Screen.
 * @author Naotsugu Kobayashi
 */
public class Screen {

    private final TextBuffer<Textual> editBuffer;

    private final Translate<Textual, TextLine> translator;
    private final FxLayoutBuilder layout;
    private List<TextLine> lines = new LinkedList<>();


    double margin = 5;

    /** The caret offset */
    private int caretOffset = 0;
    /** The logical caret position x. */
    private double caretLogicalX = margin;



    public Screen(TextBuffer<Textual> editBuffer) {
        this.editBuffer = editBuffer;
        this.layout = new FxLayoutBuilder();
        this.translator = translator();
    }


    public void draw(GraphicsContext gc) {
        drawCaret(gc);
        eachRun(layoutRun -> {
            drawTextRun(gc, layoutRun);
        });
    }


    private void drawTextRun(GraphicsContext gc, LayoutRun layoutRun) {
        if (layoutRun.run().style().font() instanceof Font font && !gc.getFont().equals(font)) {
            gc.setFont(font);
        }
        double x = layoutRun.run().layout().x() + layoutRun.marginX();
        double y = layoutRun.run().layout().y() + layoutRun.y();
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(layoutRun.run().text(), x, y);
    }


    private void drawCaret(GraphicsContext gc) {
        LayoutRun layoutRun = layoutRunAt(caretOffset);
        if (layoutRun != null) {
            int runStart = layoutRun.run().source().point().offset() + layoutRun.run().start();
            double x = layoutRun.run().offsetToX().apply(caretOffset - runStart) + layoutRun.marginX();
            double y = layoutRun.y();
            gc.save();
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(2);
            gc.strokeLine(x, y, x, y + layoutRun.run().textLine().height());
            gc.restore();
        }
    }


    private void eachRun(Consumer<LayoutRun> consumer) {
        double offsetY = 0;
        for (TextLine textLine : lines()) {
            for (TextRun run : textLine.runs()) {
                consumer.accept(new LayoutRun(run, offsetY, margin, margin));
            }
            offsetY += textLine.height();
        }
    }


    public void clear() {
        lines.clear();
    }

    public void scrollPrev(int n) {
        List<TextLine> list = editBuffer.prev(n).stream()
            .map(translator::applyTo).toList();
        if (list.isEmpty()) return;
        lines.addAll(0, list);
        lines.subList(lines.size() - list.size(), lines.size()).clear();
    }

    public void scrollNext(int n) {
        List<TextLine> list = editBuffer.next(n).stream()
            .map(translator::applyTo).toList();
        if (list.isEmpty()) return;
        lines.addAll(list);
        lines.subList(0, list.size()).clear();
    }

    public void moveCaret(int delta) {
        caretOffset += delta;
        if (caretOffset < 0) caretOffset = 0;
    }

    public void moveCaretRight() {

    }

    private List<TextLine> lines() {
        if (lines.isEmpty()) {
            List<TextLine> list = editBuffer.texts().stream()
                .map(translator::applyTo).toList();
            lines.addAll(list);
        }
        return lines;
    }


    private LayoutRun layoutRunAt(int charOffset) {
        double offsetY = 0;
        for (TextLine textLine : lines()) {
            int start = textLine.point().offset();
            int end   = start + textLine.length();
            if (start <= charOffset && charOffset < end) {
                for (TextRun run : textLine.runs()) {
                    if (within(run, charOffset)) {
                        return new LayoutRun(run, offsetY, margin, margin);
                    }
                }
                return null;
            }
            offsetY += textLine.height();
        }
        return null;
    }


    private static boolean within(TextRun run, int caretOffset) {
        if (run.length() == 0) return false;
        int runStart = run.source().point().offset() + run.start();
        int runEnd = runStart + run.length();
        return runStart <= caretOffset && caretOffset < runEnd;
    }

    private Translate<Textual, TextLine> translator() {
        return StylingTranslate.passThrough()
            .compound(SpanTranslate.of())
            .compound(LayoutTranslate.of(layout));
    }

}
