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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;

import java.util.Objects;

import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;

/**
 * The CommandPalette.
 * @author Naotsugu Kobayashi
 */
public class CommandPalette extends Dialog<CommandPalette.Command> {

    private static final String PROMPT = "<select command>";

    private final HBox box;
    private final Label commandLabel;
    private final TextField textField;
    private Command command;

    public CommandPalette(Node node) {
        this(node, Command.empty);
    }

    public CommandPalette(Node node, Command command) {

        super();
        this.command = Objects.isNull(command) ? Command.empty : command;
        initOwner(node.getScene().getWindow());
        initStyle(StageStyle.TRANSPARENT);

        var bounds = node.localToScreen(node.getBoundsInLocal());
        var width = Math.max(bounds.getWidth() * 2 / 3, 300.0);
        setOnShowing(e -> {
            setX(bounds.getMinX() + (bounds.getWidth() - width) / 2);
            setY(bounds.getMinY() + bounds.getHeight() * 1 / 5);
        });
        commandLabel = createCommandLabel();
        textField = createTextField();
        box = createBox(width, Icons.terminal(), new Label(" "), commandLabel, textField);
        HBox.setHgrow(textField, Priority.ALWAYS);
        DialogPane pane = getDialogPane();
        pane.setContent(box);
        pane.setPadding(Insets.EMPTY);

        initCommandLabel();
    }

    private HBox createBox(double width, Node... nodes) {
        var box = new HBox(nodes);
        box.setPrefWidth(width);
        box.setStyle("""
            -fx-background-color: derive(-fx-control-inner-background,10%);
            -fx-background-radius: 3;
            -fx-padding: 0.5em;
            -fx-spacing: 2;
            -fx-alignment: CENTER;
            """);
        return box;
    }

    private Label createCommandLabel() {
        var label = new Label();
        label.setVisible(false);
        label.setStyle("""
            -fx-font-size: 0.95em;
            -fx-border-style: solid;
            -fx-border-color: gray;
            -fx-border-radius: 3;
            -fx-text-fill: gray;
            """);
        return label;
    }

    private void initCommandLabel() {
        if (command instanceof Empty) {
            commandLabel.setText("");
            commandLabel.setVisible(false);
        } else {
            commandLabel.setText(command.name());
            commandLabel.setVisible(true);
        }
    }

    private TextField createTextField() {
        var textField = new AcTextField();
        textField.setPromptText(PROMPT);
        textField.setOnKeyPressed(e -> {
            if (e.getCode() == ESCAPE) {
                setResult(Command.empty);
                close();
                e.consume();
            } else if (e.getCode() == ENTER) {
                var command = textField.getText().isBlank()
                    ? Command.empty
                    : new FindAll(textField.getText());
                setResult(command);
                close();
                e.consume();
            } else if (e.getCode() == BACK_SPACE && textField.getText().isEmpty()) {
                command = Command.empty;
                initCommandLabel();
            }
        });
        return textField;
    }


    /**
     * Auto Complete TextField.
     */
    static class AcTextField extends TextField {
        public AcTextField() {
            super();
            setStyle("""
            -fx-background-color: derive(-fx-control-inner-background,10%);
            -fx-padding: 0.333333em 0.583em 0.333333em 0.2em;
            -fx-prompt-text-fill:gray;
            """);
        }
    }

    interface Command {
        Command empty = new Empty();
        String name();
    }
    record Empty() implements Command {
        @Override
        public String name() {
            return "";
        }
    }
    record FindAll(String text) implements Command {
        @Override
        public String name() {
            return "FindAll";
        }
    }

}
