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
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.text.Translate;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.LinkedList;
import java.util.List;

public class Screen {

    private final TextBuffer<Textual> editBuffer;

    private final Translate<Textual, TextLine> translator;
    private final FxLayoutBuilder layout;
    private List<TextLine> lines = new LinkedList<>();
    private int caretOffset = 0;

    double margin = 5;

    public Screen(TextBuffer<Textual> editBuffer) {
        this.editBuffer = editBuffer;
        this.layout = new FxLayoutBuilder();
        this.translator = translator();
    }


    public void draw(GraphicsContext gc) {
        double offsetY = 0;
        for (TextLine textLine : lines()) {
            for (TextRun run : textLine.runs()) {
                drawCaret(gc, offsetY, textLine, run);
                drawTextRun(gc, offsetY, run);
            }
            offsetY += textLine.height();
        }
    }

    public void drawTextRun(GraphicsContext gc, double offsetY, TextRun run) {
        if (run.style().font() instanceof Font font && !gc.getFont().equals(font)) {
            gc.setFont(font);
        }
        double y = run.layout().y() + offsetY;
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(run.text(), run.layout().x() + margin, y);
    }

    public void drawCaret(GraphicsContext gc, double offsetY, TextLine textLine, TextRun run) {
        OffsetPoint runPoint = run.source().point();
        if (runPoint.offset() <= caretOffset && caretOffset <= runPoint.offset() + run.text().length()) {
            double x = run.offsetToX().apply(caretOffset - runPoint.offset());
            gc.save();
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(2);
            gc.strokeLine(x + margin, offsetY, x + margin, offsetY + textLine.height());
            gc.restore();
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

    private List<TextLine> lines() {
        if (lines.isEmpty()) {
            List<TextLine> list = editBuffer.texts().stream()
                .map(translator::applyTo).toList();
            lines.addAll(list);
        }
        return lines;
    }

    private Translate<Textual, TextLine> translator() {
        return StylingTranslate.passThrough()
            .compound(SpanTranslate.of())
            .compound(LayoutTranslate.of(layout));
    }

}
