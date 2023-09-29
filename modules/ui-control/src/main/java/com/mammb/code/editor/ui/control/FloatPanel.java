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
package com.mammb.code.editor.ui.control;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * FloatPanel.
 * @author Naotsugu Kobayashi
 */
public class FloatPanel extends HBox {

    private Text charset;


    public FloatPanel(Color baseColor) {

        Color color = baseColor.deriveColor(0, 1, 1, 0.1);

        charset = new Text("UTF-8");
        charset.setFill(baseColor);
        charset.setFont(new Font(15 * 0.75));

        setBackground(new Background(new BackgroundFill(
            color,
            new CornerRadii(3, 0, 0, 0, false),
            Insets.EMPTY)));
        setMaxSize(150, 15);
        setPrefSize(150, 15);
        setMinSize(150, 15);
        setSpacing(10);
        setPadding(new Insets(0, 10, 0, 10));

        StackPane.setAlignment(this, Pos.BOTTOM_RIGHT);
        getChildren().add(charset);

    }

}
