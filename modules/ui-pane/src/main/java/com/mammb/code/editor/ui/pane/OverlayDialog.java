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
package com.mammb.code.editor.ui.pane;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.function.Consumer;

/**
 * OverlayDialog.
 * @author Naotsugu Kobayashi
 */
public class OverlayDialog extends StackPane {

    /** The background color. */
    private final Color bgColor;

    /** The foreground color. */
    private final Color fgColor;


    /**
     * Create a new {@link OverlayDialog}.
     * @param headerText the header text
     * @param contentText the content text
     * @param bgColor the background color
     * @param buttons the buttons
     */
    public OverlayDialog(String headerText, String contentText, Color bgColor, Node... buttons) {

        this.bgColor = bgColor;
        this.fgColor = flip(bgColor);

        setBackground(new Background(new BackgroundFill(
            Color.color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0.75),
            CornerRadii.EMPTY, Insets.EMPTY)));

        setOnKeyPressed(Event::consume);
        setOnKeyTyped(Event::consume);
        getChildren().add(dialog(headerText, contentText, buttons));
    }


    /**
     * Show confirm dialog.
     * @param parent the node displaying dialog
     * @param contentText the content text
     * @param ok the ok action
     */
    public static void confirm(Pane parent, String contentText, Runnable ok) {

        Color color = selectBackground(parent);

        Runnable[] ra = new Runnable[1];

        var dialog = new OverlayDialog(
            "Confirmation",
            contentText,
            color,
            button(color, "  OK  ", e -> { e.consume(); ra[0].run(); ok.run(); }),
            button(color, "Cancel", e -> { e.consume(); ra[0].run(); }));

        parent.getChildren().add(dialog);
        ra[0] = () -> parent.getChildren().remove(dialog);

    }


    private Node dialog(String headerText, String contentText, Node... buttons) {

        GridPane grid = new GridPane();

        grid.setCursor(Cursor.DEFAULT);
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
        hText.setFont(new Font(Font.getDefault().getSize() * 1.3));

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
     * @param clickHandler the click handler
     * @return the button
     */
    private static Node button(
            Color bgColor, String caption, EventHandler<MouseEvent> clickHandler) {

        Color fgColor = flip(bgColor);

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
        button.setOnMouseEntered(e -> colorFn.accept(flip(bgColor).deriveColor(0.0, 1.0, 0.5, 1.0)));
        button.setOnMouseExited(e -> colorFn.accept(bgColor));
        button.setOnMouseClicked(clickHandler);

        return button;

    }


    private static Color flip(Color base) {
        return base.deriveColor(0.0, 1.0, (base.getBrightness() > 0.5) ? 0.2 : 6.0, 1.0);
    }


    /**
     * Selects the background color for the specified region.
     * @param region the specified region
     * @return the background color
     */
    private static Color selectBackground(Region region) {

        if (region.getBackground() == null) {
            return Color.TRANSPARENT;
        }

        for (BackgroundFill fill : region.getBackground().getFills()) {
            if (fill.getFill() instanceof Color color) {
                return color;
            }
        }

        return Color.TRANSPARENT;

    }

}
