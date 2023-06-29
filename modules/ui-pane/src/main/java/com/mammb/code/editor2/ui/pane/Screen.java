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
import com.mammb.code.editor2.model.layout.LineLayout;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.style.StylingTranslate;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.text.Translate;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import java.util.LinkedList;
import java.util.List;

/**
 * Screen.
 * @author Naotsugu Kobayashi
 */
public class Screen {

    private final TextBuffer<Textual> editBuffer;
    private final FxLayoutBuilder layout = new FxLayoutBuilder();
    private final Translate<Textual, TextLine> translator;
    private final List<TextLine> lines = new LinkedList<>();
    private final Caret caret;


    public Screen(TextBuffer<Textual> editBuffer) {
        this.editBuffer = editBuffer;
        this.translator = translator(layout);
        this.caret = new Caret(this::layoutLine);
    }


    public void draw(GraphicsContext gc) {
        gc.save();
        drawText(gc);
        caret.draw(gc);
        gc.restore();
    }

    private void drawText(GraphicsContext gc) {
        gc.setTextBaseline(VPos.CENTER);
        double offsetY = 0;
        for (TextLine line : lines()) {
            for (TextRun run : line.runs()) {
                if (run.style().font() instanceof Font font) gc.setFont(font);
                gc.fillText(run.text(), run.layout().x(), offsetY + run.layout().y());
            }
            offsetY += line.height();
        }
    }

    private List<TextLine> lines() {
        if (lines.isEmpty()) {
            for (Textual textual : editBuffer.texts()) {
                lines.add(translator.applyTo(textual));
            }
            // TODO if it is the last line, add an empty TextLine
        }
        return lines;
    }

    private LayoutLine layoutLine(int offset) {
        if (offset < lines().get(0).point().offset()) {
            return null;
        }
        double offsetY = 0;
        for (TextLine line : lines()) {
            if (line.contains(offset)) return new LayoutLine(line, offsetY);
            offsetY += line.height();
        }
        return null;
    }

    private static Translate<Textual, TextLine> translator(LineLayout layout) {
        return StylingTranslate.passThrough()
            .compound(SpanTranslate.of())
            .compound(LayoutTranslate.of(layout));
    }

    // -- scroll behavior  ----------------------------------------------------

    public void scrollPrev(int n) {
        if (n <= 0) return;

        List<Textual> added = editBuffer.prev(n); // row based
        if (added.isEmpty()) return;

        if (added.size() >= editBuffer.maxLineSize()) {
            // if N exceeds the number of page lines, clear and add all.
            // forward scrolling can skip re-parsing syntax.
            added.subList(editBuffer.maxLineSize(), added.size()).clear();
            lines.clear();
            lines.addAll(added.stream().map(translator::applyTo).toList());
        } else {
            removeTailRow(lines, added.size());
            List<TextLine> list = added.stream().map(translator::applyTo).toList();
            lines.addAll(0, list);
        }

        caret.markDirty();
    }


    public void scrollNext(int n) {
        if (n <= 0) return;

        List<Textual> added = editBuffer.next(n); // row based
        if (added.isEmpty()) return;

        // delete rows to avoid inadvertently increasing the list size
        removeHeadRow(lines, added.size());

        List<TextLine> list = added.stream().map(translator::applyTo).toList();
        removeHeadRow(list, added.size() - editBuffer.maxLineSize());

        lines.addAll(list);
        // TODO if it is the last line, add an empty TextLine
        caret.markDirty();
    }


    private void scrollToCaret() {
        int size = lines().size();
        int head = lines.get(0).start();
        int tail = lines.get(Math.min(size - 1, size - 2)).end();
        if (head <= caret.offset() && caret.offset() < tail) {
            return;
        }
        if (caret.offset() < head) {
            scrollPrev(0); // TODO
        } else {
            scrollNext(0); // TODO
        }
    }

    // -- arrow behavior ------------------------------------------------------

    public void moveCaretRight() {
        scrollToCaret();
        caret.right();
    }

    public void moveCaretLeft() {
        scrollToCaret();
        caret.left();
    }

    public void moveCaretUp() {
        scrollToCaret();
        caret.up();
    }

    public void moveCaretDown() {
        scrollToCaret();
        caret.down();
    }

    // -- helper --------------------------------------------------------------

    private static void removeHeadRow(List<TextLine> lines, int n) {
        removeRow(lines, n, true);
    }

    private static void removeTailRow(List<TextLine> lines, int n) {
        removeRow(lines, n, false);
    }

    private static void removeRow(List<TextLine> lines, int n, boolean asc) {

        if (n <= 0) return;

        if (n >= lines.size()) {
            lines.clear();
            return;
        }

        int prev = -1;
        while (true) {
            int index = asc ? 0 : lines.size() - 1;
            TextLine line = lines.get(index);
            if (prev != -1 && prev != line.point().row()) {
                n--;
            }
            if (n == 0) break;
            prev = line.point().row();
            lines.remove(index);
        }
    }

}
