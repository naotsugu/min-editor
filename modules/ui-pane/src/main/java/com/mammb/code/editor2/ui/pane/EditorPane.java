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

import com.mammb.code.editor.javafx.layout.FxSpanStyle;
import com.mammb.code.editor.javafx.layout.LayoutBuilder;
import com.mammb.code.editor2.model.buffer.Buffers;
import com.mammb.code.editor2.model.layout.Span;
import com.mammb.code.editor2.model.layout.TextLine;
import com.mammb.code.editor2.model.layout.TextRun;
import com.mammb.code.editor2.model.style.StyledText;
import com.mammb.code.editor2.model.text.Translate;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;

import java.nio.file.Path;

/**
 * EditorPane.
 * @author Naotsugu Kobayashi
 */
public class EditorPane extends StackPane {

    private Canvas canvas;
    private GraphicsContext gc;

    public EditorPane(double width, double height) {
        setWidth(width);
        setHeight(height);


        LayoutBuilder layoutBuilder = new LayoutBuilder();
        layoutBuilder.setWrapWidth(400);

        var buffer = Buffers.of(30,
            Path.of("build.gradle.kts"),
            layoutBuilder,
            new Translate<StyledText, Span>() {
                public Span applyTo(StyledText input) {
                    return Span.of(input, FxSpanStyle.of(Font.getDefault()));
                }
            });

        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        gc.setFont(Font.getDefault());
        gc.setFill(Color.BLACK);

        for (TextLine textLine : buffer.texts()) {
            for (TextRun run : textLine.runs()) {
                gc.fillText(run.text(), run.layout().x(), run.layout().y());
            }
        }
        //gc.fillText("hello", 10, 25);

        getChildren().add(canvas);
        TextFlow flow = new TextFlow();
    }

}
