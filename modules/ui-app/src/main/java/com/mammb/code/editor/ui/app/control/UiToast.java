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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import static com.mammb.code.editor.ui.app.control.CssProcessor.CSS;

/**
 * The toast.
 * @author Naotsugu Kobayashi
 */
public class UiToast {

    private final Stage stage;

    private Scene scene;

    private Pane pane;


    public UiToast(Stage owner, Node... nodes) {

        pane = new StackPane(nodes);
        pane.getStyleClass().add(styleClass);
        pane.setOpacity(0);

        scene = new Scene(pane);
        ThemeCss.rootWith(css).into(scene);
        scene.setFill(Color.TRANSPARENT);

        stage =  new Stage();
        stage.initOwner(owner);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.NONE);
        stage.setScene(scene);

        stage.setWidth(300);

        stage.setY(owner.getY() + 70);
        stage.setX(owner.getX() + owner.getWidth() - (stage.getWidth()) - 20);

    }

    public static UiToast of(Stage owner, String message) {
        var toast = new UiToast(owner, new Label(message));
        toast.show();
        return toast;
    }


    void show() {
        stage.show();
        FadeTransition ft = new FadeTransition(Duration.millis(500), pane);
        ft.setFromValue(0.0);
        ft.setToValue(0.9);
        ft.play();
    }

    public void close() {
        stage.close();
    }


    /** The style class name. */
    static final String styleClass = "toast";

    /** The css. */
    static final Css css = st -> CSS."""
        .\{styleClass} {
          -fx-background-insets: 0;
          -fx-background-radius: 3;
          -fx-fill:\{st.text};
          -fx-font: 13.5px "Consolas";
        }
        """;


}
