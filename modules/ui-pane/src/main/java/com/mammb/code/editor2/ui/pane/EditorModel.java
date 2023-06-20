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
import com.mammb.code.editor.javafx.layout.LayoutBuilder;
import com.mammb.code.editor2.model.buffer.Buffers;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.layout.Span;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.text.Translate;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import java.nio.file.Path;

/**
 * EditorModel.
 * @author Naotsugu Kobayashi
 */
public class EditorModel {

    private FxFontStyle fontStyle = FxFontStyle.of(Font.font(20));

    private TextBuffer<TextLine> buffer;


    public EditorModel(double width, double height) {
        this(width, height, null);
    }


    public EditorModel(double width, double height, Path path) {
        int rows = (int) Math.ceil(height / new FxFontMetrics(fontStyle.font()).lineHeight());
        Translate<StyledText, Span> translate = in -> Span.of(in, fontStyle);
        buffer = Buffers.of(rows, path, new LayoutBuilder(width), translate);
    }


    public void draw(GraphicsContext gc) {

        for (TextLine textLine : buffer.texts()) {
            for (TextRun run : textLine.runs()) {
                if (run.style().font() instanceof Font font &&
                    !gc.getFont().equals(font)) {
                    gc.setFont(fontStyle.font());
                }
                gc.fillText(run.text(), run.layout().x(), run.layout().y());
            }
        }
    }

    public void up(int n) { buffer.prev(n); }

    public void down(int n) { buffer.next(n); }

}
