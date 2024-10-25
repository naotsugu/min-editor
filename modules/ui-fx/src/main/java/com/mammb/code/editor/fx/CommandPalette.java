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
package com.mammb.code.editor.fx;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;

import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;

/**
 * The CommandPalette.
 * @author Naotsugu Kobayashi
 */
public class CommandPalette extends Dialog<CommandPalette.Command> {

    private final TextField textField = new TextField();
    private final HBox box = new HBox(Icons.terminal(), textField);

    public CommandPalette(Node node) {

        super();
        initOwner(node.getScene().getWindow());
        initStyle(StageStyle.TRANSPARENT);

        DialogPane pane = getDialogPane();
        pane.setContent(box);
        pane.setPadding(Insets.EMPTY);

        var bounds = node.localToScreen(node.getBoundsInLocal());
        var width = Math.max(bounds.getWidth() * 1 / 3, 300);
        setOnShowing(e -> {
            setX(bounds.getMinX() + (bounds.getWidth() - width) / 2);
            setY(bounds.getMinY() + bounds.getHeight() * 1 / 5);
        });
        initBox();
        initTextField(width);

    }

    private void initBox() {
        box.setStyle("""
            -fx-background-color: derive(-fx-control-inner-background,10%);
            -fx-padding: 0.5em;
            -fx-alignment: CENTER;
            """);
    }

    private void initTextField(double width) {
        textField.setStyle("""
            -fx-background-color: derive(-fx-control-inner-background,10%);
            """);
        textField.setPrefWidth(width);
        textField.setOnKeyPressed(e -> {
            if (e.getCode() == ESCAPE) {
                setResult(new Empty());
                close();
                e.consume();
            } else if (e.getCode() == ENTER) {
                if (textField.getText().isBlank()) {
                    setResult(new Empty());
                } else {
                    setResult(new FindAll(textField.getText()));
                    close();
                    e.consume();
                }
            }
        });
    }

    interface Command {}
    record Empty() implements Command {}
    record FindAll(String text) implements Command {}

}
