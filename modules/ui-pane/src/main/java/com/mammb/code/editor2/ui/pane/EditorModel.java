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

import com.mammb.code.editor.javafx.layout.FxFontMetrics;
import com.mammb.code.editor.javafx.layout.FxFontStyle;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.text.Textual;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.nio.file.Path;

/**
 * EditorModel.
 * @author Naotsugu Kobayashi
 */
public class EditorModel {

    private Screen screen;


    public EditorModel(double width, double height) {
        this(width, height, null);
    }


    public EditorModel(double width, double height, Path path) {
        TextBuffer<Textual> editBuffer = TextBuffer.editBuffer(screenRowSize(height), path);
        screen = new Screen(editBuffer, width, height);
    }

    public void draw(GraphicsContext gc) {
        screen.draw(gc);
    }

    public void clearAndDraw(GraphicsContext gc) {
        Canvas canvas = gc.getCanvas();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        screen.draw(gc);
    }

    // -- scroll behavior  ----------------------------------------------------
    public void up(int n) { screen.scrollPrev(n); }
    public void down(int n) { screen.scrollNext(n); }
    // -- arrow behavior ------------------------------------------------------
    public void moveCaretRight() { screen.moveCaretRight(); }
    public void moveCaretLeft() { screen.moveCaretLeft(); }
    public void moveCaretUp() { screen.moveCaretUp(); }
    public void moveCaretDown() { screen.moveCaretDown(); }
    public void moveCaretPageUp() { screen.moveCaretPageUp(); }
    public void moveCaretPageDown() { screen.moveCaretPageDown(); }
    // -- mouse behavior ------------------------------------------------------
    public void click(double x, double y) { screen.click(x, y); }
    public void clickDouble(double x, double y) { screen.clickDouble(x, y); }
    // -- edit behavior -------------------------------------------------------
    public void input(String value) { screen.input(value); }
    public void delete() { screen.delete(); }
    public void backspace() { screen.backspace(); }
    // -- conf behavior -------------------------------------------------------
    public void toggleWrap() { screen.toggleWrap(); }

    // -- private -------------------------------------------------------------

    private int screenRowSize(double height) {
        var fontMetrics = new FxFontMetrics(FxFontStyle.of().font());
        return (int) Math.ceil(height / fontMetrics.lineHeight());
    }

}
