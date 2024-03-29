/*
 * Copyright 2023-2024 the original author or authors.
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
package com.mammb.code.editor.ui.app.control;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * The toast.
 * @author Naotsugu Kobayashi
 */
public class Toast {

    public static Stage of(Stage owner, String message) {

        var stage = new Stage();
        stage.initOwner(owner);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.NONE);

        var text = new Text(message);
        text.setFill(Color.LIGHTGRAY);

        var pane = new StackPane(text);
        pane.setOpacity(0);
        pane.setBackground(new Background(
            new BackgroundFill(Color.color(0, 0, 0, 0.8),
                new CornerRadii(5), Insets.EMPTY)));

        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setWidth(200);
        stage.setHeight(80);

        stage.setY(owner.getY() + 70);
        stage.setX(owner.getX() + owner.getWidth() - (stage.getWidth()) - 20);

        stage.show();

        FadeTransition ft = new FadeTransition(Duration.millis(500), pane);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        return stage;

    }

}
