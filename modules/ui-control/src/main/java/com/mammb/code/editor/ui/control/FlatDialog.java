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
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.util.function.Consumer;

/**
 * FlatDialog.
 * @author Naotsugu Kobayashi
 */
public class FlatDialog extends GridPane {

    /**
     * Constructor.
     *
     * @param headerText  the header text
     * @param contentText the content text
     * @param buttons     the buttons
     */
    private FlatDialog(String headerText, String contentText, Node... buttons) {

        Text hText = new Text(headerText);
        Text cText = new Text(contentText);

        HBox buttonBar = new HBox(buttons);
        buttonBar.setSpacing(20);

        setBorder(new Border(new BorderStroke(
            Color.GRAY,
            BorderStrokeStyle.SOLID,
            new CornerRadii(10),
            BorderWidths.DEFAULT,
            Insets.EMPTY)));

        setPadding(new Insets(15));
        setHgap(10);
        setVgap(10);

        //      0     1     2
        // 0 |     |     |     |
        // 1 |      text       |
        // 2 |     |     | btn |
        add(hText,0, 0, 3, 1);
        add(cText,0, 1, 3, 1);
        add(buttonBar,2, 2);

        ColumnConstraints cca = new ColumnConstraints();
        cca.setHgrow(Priority.ALWAYS);
        ColumnConstraints ccs = new ColumnConstraints();
        ccs.setHgrow(Priority.SOMETIMES);
        getColumnConstraints().addAll(ccs, cca, ccs);

        RowConstraints rca  = new RowConstraints();
        rca.setVgrow(Priority.ALWAYS);
        RowConstraints rcs  = new RowConstraints();
        rcs.setVgrow(Priority.SOMETIMES);
        getRowConstraints().addAll(rcs, rca, rcs);

    }


    /**
     * Create a new confirm FlatDialog.
     * @param contentText the content text
     * @param ok ok action
     * @param close close action
     * @return the FlatDialog
     */
    public static FlatDialog confirmOf(
        String contentText, Runnable ok, Runnable close) {
        return new FlatDialog("Confirmation", contentText,
            button(" OK ", e -> { e.consume(); ok.run(); close.run();}),
            button("Cancel", e -> { e.consume(); close.run(); }));
    }


    /**
     * Create button.
     * @param caption the caption
     * @return the button
     */
    private static Node button(
        String caption,
        EventHandler<MouseEvent> clickHandler) {

        StackPane button = new StackPane(new Text(caption));

        Consumer<String> colorFn = colorString -> button.setBackground(
            new Background(new BackgroundFill(
                Color.web(colorString),
                new CornerRadii(3),
                Insets.EMPTY)));

        button.setAlignment(Pos.CENTER);
        button.setPadding(new Insets(6));
        colorFn.accept("#424445");
        button.setOnMouseEntered(e -> colorFn.accept("#626465"));
        button.setOnMouseExited(e -> colorFn.accept("#424445"));
        button.setOnMouseClicked(clickHandler);
        return button;
    }

}
