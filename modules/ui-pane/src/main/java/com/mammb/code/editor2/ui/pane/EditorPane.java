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

import com.mammb.code.editor2.model.layout.fx.FxShapedText;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;

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

        FxShapedText shaped = new FxShapedText();
        shaped.setWrapWidth((float) width);


        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        gc.setFont(new Font(20));
        gc.setFill(Color.GRAY);
        gc.fillText("hello", 10, 25);

        getChildren().add(canvas);
        TextFlow flow = new TextFlow();
    }

}
