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

        List<TextLine> list = editBuffer.prev(n).stream()
            .map(translator::applyTo).toList();
        if (list.isEmpty()) return;

        lines.addAll(0, list);
        lines.subList(lines.size() - list.size(), lines.size()).clear();
        caret.markDirty();
    }

    public void scrollNext(int n) {

        List<TextLine> added = editBuffer.next(n).stream()
            .map(translator::applyTo).toList();
        if (added.isEmpty()) return;

        lines.addAll(added);
        lines.subList(0, added.size()).clear(); // TODO if it is the last line, add an empty TextLine
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

}
