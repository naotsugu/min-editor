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
    private Caret caret = new Caret();


    public Screen(TextBuffer<Textual> editBuffer) {
        this.editBuffer = editBuffer;
        this.layout = new FxLayoutBuilder();
        this.translator = translator();
    }


    public void draw(GraphicsContext gc) {
        drawCaret(gc);
        eachRun(run -> drawTextRun(gc, run));
    }


    private void drawTextRun(GraphicsContext gc, LayoutRun run) {
        if (run.style().font() instanceof Font font && !gc.getFont().equals(font)) {
            gc.setFont(font);
        }
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(run.text(), run.x(), run.y());
    }


    private void drawCaret(GraphicsContext gc) {
        if (caret.height() == 0) calcCaret();
        gc.save();
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(caret.x() == 0 ? 4 : 2);
        gc.strokeLine(caret.x(), caret.y(), caret.x(), caret.y2());
        gc.restore();
    }


    private void calcCaret() {
        LayoutRun run = layoutRunAt(caret.charOffset());
        if (run != null) {
            caret.moveTo(
                run.offsetToX().apply(caret.charOffset() - run.offset()),
                run.offsetY(),
                run.textLine().height()
            );
        }
    }


    private void eachRun(Consumer<LayoutRun> consumer) {
        double offsetY = 0;
        for (TextLine line : lines()) {
            for (TextRun run : line.runs()) {
                consumer.accept(new LayoutRun(run, offsetY));
            }
            offsetY += line.height();
        }
    }

    private void eachLine(Consumer<LayoutLine> consumer) {
        double offsetY = 0;
        for (TextLine line : lines()) {
            consumer.accept(new LayoutLine(line, offsetY));
            offsetY += line.height();
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
        // TODO if it is the last line, add an empty TextLine
        lines.subList(0, list.size()).clear();
    }


    public void moveCaretRight() {
        Caret old = caret.copy();
        caret.plusOffset();
        calcCaret();
        if (caret.equalPoint(old)) {
            caret.plusOffset();
            calcCaret();
        }
        caret.syncLogical();
    }


    public void moveCaretLeft() {
        if (caret.charOffset() == 0) return;
        Caret old = caret.copy();
        caret.minusOffset();
        calcCaret();
        if (caret.equalPoint(old) && caret.charOffset() > 0) {
            caret.minusOffset();
            calcCaret();
        }
        caret.syncLogical();
    }

    public void moveCaretDown() {
        LayoutRun run = layoutRunAt(caret.logicalX(), caret.y2() + 1);
        double x = caret.logicalX() - run.x();
        int offset = run.xToOffset().apply(x);
        TextLine line = run.textLine();
        offset += line.point().offset();
        for (TextRun textRun : line.runs()) {
            if (textRun == run.run()) break;
            offset += textRun.length();
        }
        caret.moveTo(offset);
        calcCaret();
    }

    public void moveCaretUp() {
        if (caret.y() - 2 < 0) {
            return;
        }
        LayoutRun run = layoutRunAt(caret.logicalX(), caret.y() - 2);
        double x = caret.logicalX() - run.x();
        int offset = run.xToOffset().apply(x);
        TextLine line = run.textLine();
        offset += line.point().offset();
        for (TextRun textRun : line.runs()) {
            if (textRun == run.run()) break;
            offset += textRun.length();
        }
        caret.moveTo(offset);
        calcCaret();
    }

    private List<TextLine> lines() {
        if (lines.isEmpty()) {
            List<TextLine> list = editBuffer.texts().stream()
                .map(translator::applyTo)
                .toList();
            lines.addAll(list);
        }
        return lines;
    }


    private LayoutRun layoutRunAt(int charOffset) {
        double offsetY = 0;
        for (TextLine line : lines()) {
            int start = line.point().offset();
            int end   = start + line.length();
            if (start <= charOffset && charOffset < end) {
                for (TextRun run : line.runs()) {
                    if (within(run, charOffset)) {
                        return new LayoutRun(run, offsetY);
                    }
                }
                return null;
            }
            offsetY += line.height();
        }
        return null;
    }


    private LayoutRun layoutRunAt(double x, double y) {
        double offsetY = 0;
        List<TextLine> textLines = lines();
        for (int i = 0; i < textLines.size(); i++) {
            TextLine line = textLines.get(i);
            double lineStart = offsetY;
            double lineEnd   = offsetY + line.height();
            if (lineStart <= y && y < lineEnd) {
                for (int j = 0; j < line.runs().size(); j++) {
                    TextRun run = line.runs().get(j);
                    double runStart = run.layout().x();
                    double runEnd   = runStart + run.layout().width();
                    if (runStart <= x && x < runEnd || j == line.runs().size() - 1) {
                        return new LayoutRun(run, offsetY);
                    }
                }
            }
            offsetY += line.height();
        }
        TextLine line = textLines.get(textLines.size() - 1);
        TextRun run = line.runs().get(line.runs().size() - 1);
        return new LayoutRun(run, offsetY - line.height());
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
