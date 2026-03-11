/*
 * Copyright 2023-2026 the original author or authors.
 * <p>
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
package com.mammb.code.editor.ui.fx;

import com.mammb.code.editor.core.Theme;
import com.mammb.code.editor.ui.base.NotifyListener;
import javafx.animation.PauseTransition;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * The NotificationPane.
 * @author Naotsugu Kobayashi
 */
public class NotificationPane extends VBox implements NotifyListener {

    private static final double MARGIN = 30;
    private static final double WIDTH = 250;

    public NotificationPane(Node parent) {
        setManaged(false);
        setAlignment(Pos.BASELINE_CENTER);
        setSpacing(8);
        setWidth(WIDTH);

        parent.layoutBoundsProperty().addListener(this::handleLayoutBounds);
    }

    private void handleLayoutBounds(ObservableValue<? extends Bounds> ob, Bounds o, Bounds n) {
        setLayoutX(n.getWidth() - WIDTH - MARGIN);
        setLayoutY(n.getHeight() - getHeight() - MARGIN);
    }

    @Override
    public void notify(String message, String... details) {
        var toast = new Toast(message, details);
        getChildren().add(toast);
        var pause = new PauseTransition(Duration.seconds(4));
        pause.setOnFinished(_ -> getChildren().remove(toast));
        pause.play();
        // TODO adjust setHeight()
    }

    static class Toast extends GridPane {
        public Toast(String message, String... details) {
            setBackground(new Background(new BackgroundFill(
                Color.web(Theme.current.baseColor().web()).brighter(),
                new CornerRadii(4),
                Insets.EMPTY)));

            var icon = Icons.exclamationCircle();
            setMargin(icon, new Insets(8, 8, 8, 8));
            GridPane.setConstraints(icon, 0, 0);

            var msg = new Text(message);
            msg.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
            msg.setFill(Color.web(Theme.current.fgColor().web()));
            setMargin(msg, new Insets(4, 4, 4, 4));
            GridPane.setConstraints(msg, 1, 0);

            // TODO add message details

            // TODO add close button

            getChildren().addAll(icon, msg);
        }
    }

}
