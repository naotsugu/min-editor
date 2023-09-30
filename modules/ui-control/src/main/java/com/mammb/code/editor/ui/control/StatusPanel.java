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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * FloatPanel.
 * @author Naotsugu Kobayashi
 */
public class StatusPanel extends HBox {

    public static final double HEIGHT = 15;
    public static final double WIDTH = 200;

    private Color baseColor;
    private Map<String, Text> texts = new HashMap<>();


    public StatusPanel(Color baseColor) {

        this.baseColor = baseColor;

        setBackground(new Background(new BackgroundFill(
            baseColor.deriveColor(0, 1, 1, 0.1),
            new CornerRadii(3, 0, 0, 0, false),
            Insets.EMPTY)));
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setPrefSize(WIDTH, HEIGHT);
        setSpacing(HEIGHT / 2);
        setPadding(new Insets(0, HEIGHT / 3, 0, HEIGHT / 3));
    }


    public void push(String key, String string) {
        texts.computeIfAbsent(key, k -> createText()).setText(string);
    }


    public void clear() {
        getChildren().clear();
    }


    private Text createText() {
        var text = new Text();
        text.setFill(baseColor);
        text.setFont(new Font(HEIGHT * 0.75));
        getChildren().add(text);
        return text;
    }


    private void adjustFontSize(Text text, double height) {
        double textHeight = text.getBoundsInLocal().getHeight();
        if (Math.abs(height - textHeight) > 1) {
            Font current = text.getFont() ;
            double fontSize = current.getSize() ;
            text.setFont(Font.font(current.getFamily(), height * fontSize / textHeight));
        }
    }

}
