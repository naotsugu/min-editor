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

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.util.function.Consumer;

/**
 * OverlayDialog.
 * @author Naotsugu Kobayashi
 */
public class OverlayDialog extends StackPane {

    private static Color bgColor = Color.WHITE;
    private static Color fgColor = Color.BLACK;


    public OverlayDialog(String headerText, String contentText, Node... buttons) {

        setBackground(new Background(new BackgroundFill(
            Color.color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0.75),
            CornerRadii.EMPTY, Insets.EMPTY)));

        setOnKeyPressed(Event::consume);
        setOnKeyTyped(Event::consume);
        getChildren().add(dialog(headerText, contentText, buttons));
    }


    public static void confirm(Pane parent, String contentText, Runnable ok) {
        Runnable[] ra = new Runnable[1];
        var dialog = new OverlayDialog(
            "Confirmation",
            contentText,
            button(" OK ", e -> { e.consume(); ra[0].run(); ok.run(); }),
            button("Cancel", e -> { e.consume(); ra[0].run(); }));
        parent.getChildren().add(dialog);
        ra[0] = () -> parent.getChildren().remove(dialog);
    }


    private Node dialog(String headerText, String contentText, Node... buttons) {

        GridPane grid = new GridPane();

        grid.setMaxSize(300, 150);
        grid.setMinSize(300, 150);
        grid.setPrefSize(300, 150);

        grid.setBackground(new Background(new BackgroundFill(
            bgColor,
            new CornerRadii(3),
            Insets.EMPTY)));

        grid.setBorder(new Border(new BorderStroke(
            fgColor,
            BorderStrokeStyle.SOLID,
            new CornerRadii(5),
            BorderWidths.DEFAULT,
            Insets.EMPTY)));

        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        HBox buttonBar = new HBox(buttons);
        buttonBar.setSpacing(20);

        var hText = new Text(headerText);
        hText.setFill(fgColor);


        var cText = new Text(contentText);
        cText.setFill(fgColor);

        //      0     1     2
        // 0 |     |     |     |
        // 1 |      text       |
        // 2 |     |     | btn |
        grid.add(hText, 0, 0, 3, 1);
        grid.add(cText, 0, 1, 3, 1);
        grid.add(buttonBar,2, 2);

        ColumnConstraints cca = new ColumnConstraints();
        cca.setHgrow(Priority.ALWAYS);
        ColumnConstraints ccs = new ColumnConstraints();
        ccs.setHgrow(Priority.SOMETIMES);
        grid.getColumnConstraints().addAll(ccs, cca, ccs);

        RowConstraints rca  = new RowConstraints();
        rca.setVgrow(Priority.ALWAYS);
        RowConstraints rcs  = new RowConstraints();
        rcs.setVgrow(Priority.SOMETIMES);
        grid.getRowConstraints().addAll(rcs, rca, rcs);

        return grid;

    }


    /**
     * Create button.
     * @param caption the caption
     * @return the button
     */
    private static Node button(
        String caption,
        EventHandler<MouseEvent> clickHandler) {

        var text = new Text(caption);
        text.setFill(fgColor);

        StackPane button = new StackPane(text);
        button.setBorder(new Border(new BorderStroke(
            fgColor,
            BorderStrokeStyle.SOLID,
            new CornerRadii(3),
            BorderWidths.DEFAULT,
            Insets.EMPTY)));


        Consumer<Color> colorFn = color -> button.setBackground(
            new Background(new BackgroundFill(
                color,
                new CornerRadii(3),
                Insets.EMPTY)));

        button.setAlignment(Pos.CENTER);
        button.setPadding(new Insets(6));
        colorFn.accept(bgColor);
        button.setOnMouseEntered(e -> colorFn.accept(bgColor.darker()));
        button.setOnMouseExited(e -> colorFn.accept(bgColor));
        button.setOnMouseClicked(clickHandler);
        return button;
    }

}
