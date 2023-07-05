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

import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.text.Textual;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

/**
 * Screen.
 * @author Naotsugu Kobayashi
 */
public class Screen {

    private final double width;
    private final double height;
    private final Caret caret;
    private TextList texts;


    public Screen(TextBuffer<Textual> editBuffer, double width, double height) {
        this.texts = new LinearTextList(editBuffer);
        this.caret = new Caret(this::layoutLine);
        this.width = width;
        this.height = height;
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
        for (TextLine line : texts.lines()) {
            for (TextRun run : line.runs()) {
                if (run.style().font() instanceof Font font) gc.setFont(font);
                gc.fillText(run.text(), run.layout().x(), offsetY + run.baseline());
            }
            offsetY += line.height();
        }
    }

    private LayoutLine layoutLine(int offset) {
        if (offset < texts.head().point().offset()) {
            return null;
        }
        double offsetY = 0;
        for (TextLine line : texts.lines()) {
            if (line.contains(offset)) return new LayoutLine(line, offsetY);
            offsetY += line.height();
        }
        return null;
    }

    // -- scroll behavior  ----------------------------------------------------

    public void scrollPrev(int n) {
        int size = texts.prev(n);
        if (size > 0) caret.markDirty();
    }

    public void scrollNext(int n) {
        int size = texts.next(n);
        if (size > 0) caret.markDirty();
    }

    private void scrollToCaret() {
        boolean scrolled = texts.at(caret.row(), caret.offset());
        if (scrolled) caret.markDirty();
    }

    // -- arrow behavior ------------------------------------------------------

    public void moveCaretRight() {
        scrollToCaret();
        if (caret.y2() + 4 >= height) scrollNext(1);
        caret.right();
        if (caret.y2() > height) scrollNext(1);
    }

    public void moveCaretLeft() {
        scrollToCaret();
        if (texts.head().start() > 0 && texts.head().start() == caret.offset()) {
            scrollPrev(1);
        }
        caret.left();
    }

    public void moveCaretUp() {
        scrollToCaret();
        if (texts.head().start() > 0 && caret.offset() < texts.head().end()) {
            scrollPrev(1);
        }
        caret.up();
    }

    public void moveCaretDown() {
        scrollToCaret();
        if (caret.y2() + 4 >= height) scrollNext(1);
        caret.down();
        if (caret.y2() > height) scrollNext(1);
    }

    // --  ------------------------------------------------------

    public void toggleWrap() {
        if (texts instanceof LinearTextList linear)  {
            texts = linear.asWrapTextList(width);
        } else if (texts instanceof WrapTextList wrap) {
            texts = wrap.asLinearTextList();
        }
    }

}
