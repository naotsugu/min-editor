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
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
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
    private static final double WIDTH = 260;

    public NotificationPane(Node parent) {
        setManaged(false);
        setAlignment(Pos.BOTTOM_CENTER);
        setSpacing(8);
        setWidth(WIDTH);

        parent.layoutBoundsProperty().addListener(this::handleLayoutBounds);
        layoutBoundsProperty().addListener(_ -> handleLayoutBounds(null, null, parent.getLayoutBounds()));
    }

    private void handleLayoutBounds(ObservableValue<? extends Bounds> ob, Bounds o, Bounds n) {
        if (n == null) return;
        setLayoutX(n.getWidth() - WIDTH - MARGIN);
        setLayoutY(n.getHeight() - getHeight() - MARGIN);
    }

    @Override
    public void accept(String headline, String text) {
        var toast = new Toast(this, headline, text);
        getChildren().add(toast);
        toast.show();
    }

    static class Toast extends GridPane {

        private final Pane parent;
        private final PauseTransition pause = new PauseTransition(Duration.seconds(4));

        public Toast(Pane parent, String headline, String description) {
            this.parent = parent;
            setBackground(new Background(new BackgroundFill(
                Color.web(Theme.current.baseColor().web()).brighter(),
                new CornerRadii(4),
                Insets.EMPTY)));

            getColumnConstraints().addAll(new ColumnConstraints(40), new ColumnConstraints(200), new ColumnConstraints(20));
            getRowConstraints().addAll(new RowConstraints(40), new RowConstraints());
            getRowConstraints().get(1).setVgrow(Priority.ALWAYS);

            var icon = Icons.exclamationCircle();
            setConstraints(icon, 0, 0);
            setValignment(icon, VPos.CENTER);
            setHalignment(icon, HPos.CENTER);

            var headlineText = new Text(headline);
            headlineText.setWrappingWidth(getColumnConstraints().get(1).getPrefWidth());
            headlineText.setFont(Font.font("Consolas", FontWeight.BOLD, 13));
            headlineText.setFill(Color.web(Theme.current.fgColor().web()));
            setMargin(headlineText, new Insets(2, 0, 0, 0));
            setConstraints(headlineText, 1, 0);
            setValignment(headlineText, VPos.CENTER);
            setHalignment(headlineText, HPos.LEFT);

            var closeIcon = Icons.close();
            var closeIconColor = (Color) closeIcon.getStroke();
            var closeButton = new StackPane(closeIcon);
            closeButton.setOnMouseClicked(_ -> dismiss());
            closeButton.setOnMouseEntered(_ -> closeIcon.setStroke(closeIconColor.brighter()));
            closeButton.setOnMouseExited(_ -> closeIcon.setStroke(closeIconColor));
            setMargin(closeButton, new Insets(8, 8, 8, 8));
            setConstraints(closeButton, 2, 0);
            setValignment(closeButton, VPos.TOP);
            setHalignment(closeButton, HPos.RIGHT);

            getChildren().addAll(icon, headlineText, closeButton);

            if (description != null && !description.isBlank()) {
                var descriptionText = new Text(description);
                descriptionText.setWrappingWidth(getColumnConstraints().get(1).getPrefWidth());
                descriptionText.setFont(Font.font("Consolas", FontWeight.NORMAL, 12));
                descriptionText.setFill(Color.web(Theme.current.fgColor().web()));
                setMargin(descriptionText, new Insets(0, 0, 16, 0));
                setConstraints(descriptionText, 1, 1);
                setValignment(descriptionText, VPos.TOP);
                setHalignment(descriptionText, HPos.LEFT);
                getChildren().add(descriptionText);
            }

            setOnMouseEntered(_ -> pause.stop());
            setOnMouseExited(_ -> pause.play());

            pause.setOnFinished(_ -> dismiss());

        }

        public void show() {
            setOpacity(0);
            var fadeIn = new FadeTransition(Duration.millis(300), this);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setOnFinished(_ -> pause.play());
            fadeIn.play();
        }

        private void dismiss() {
            pause.stop();
            var fadeOut = new FadeTransition(Duration.millis(300), this);
            fadeOut.setFromValue(getOpacity());
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(_ -> parent.getChildren().remove(this));
            fadeOut.play();
        }

    }

}
