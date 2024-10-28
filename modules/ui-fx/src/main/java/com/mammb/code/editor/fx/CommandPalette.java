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

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.StageStyle;
import java.util.ArrayList;
import java.util.List;
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

    private final HBox box = new HBox();
    private final Label commandLabel = new Label();
    private final TextField textField = new AcTextField();
    private Command command;

    public CommandPalette(Node node) {
        this(node, Command.empty);
    }

    public CommandPalette(Node node, Command init) {

        super();
        this.command = Objects.isNull(init) ? Command.empty : init;
        initOwner(node.getScene().getWindow());
        initStyle(StageStyle.TRANSPARENT);

        var bounds = node.localToScreen(node.getBoundsInLocal());
        var width = Math.max(bounds.getWidth() * 2 / 3, 300);
        setOnShowing(e -> {
            setX(bounds.getMinX() + (bounds.getWidth() - width) / 2);
            setY(bounds.getMinY() + bounds.getHeight() * 1 / 5);
        });

        // init command label
        commandLabel.setVisible(false);
        commandLabel.setStyle("""
            -fx-font-size: 0.95em;
            -fx-border-style: solid;
            -fx-border-color: gray;
            -fx-border-radius: 3;
            -fx-text-fill: gray;
            """);

        // init text field
        textField.setPromptText(PROMPT);
        textField.setOnKeyPressed(e -> {
            if (e.getCode() == ESCAPE) {
                setResult(Command.empty);
                close();
                e.consume();
            } else if (e.getCode() == ENTER) {
                setResult(textField.getText().isBlank() ? Command.empty : new FindAll(textField.getText()));
                close();
                e.consume();
            } else if (e.getCode() == BACK_SPACE && textField.getText().isEmpty()) {
                command = Command.empty;
                initCommandLabel();
            }
        });

        // init box
        box.setPrefWidth(width);
        box.setStyle("""
            -fx-background-color: derive(-fx-control-inner-background,10%);
            -fx-background-radius: 3;
            -fx-padding: 0.5em;
            -fx-spacing: 2;
            -fx-alignment: CENTER;
            """);
        box.getChildren().addAll(Icons.terminal(), new Label(" "), commandLabel, textField);
        HBox.setHgrow(textField, Priority.ALWAYS);

        DialogPane pane = getDialogPane();
        pane.setContent(box);
        pane.setPadding(Insets.EMPTY);

        initCommandLabel();
    }

    private void initCommandLabel() {
        commandLabel.setVisible(!(command instanceof Empty));
        commandLabel.setText(command.name());
    }

    /**
     * Auto Complete TextField.
     */
    static class AcTextField extends TextField {
        private final ContextMenu popup = new ContextMenu();
        private final List<Command> entries = List.of(
            new FindAll(), new ToLowerCase(), new ToUpperCase(), new Sort(), new Unique(), new GoTo());
        public AcTextField() {
            super();
            setStyle("""
            -fx-background-color: derive(-fx-control-inner-background,10%);
            -fx-padding: 0.333333em 0.583em 0.333333em 0.2em;
            -fx-prompt-text-fill:gray;
            """);
            popup.setStyle("""
                -fx-background-color: derive(-fx-control-inner-background,10%);
                """);
            textProperty().addListener(this::handleTextChanged);
            focusedProperty().addListener(this::handleTextFocused);
        }
        private void handleTextChanged(ObservableValue<? extends String> ob, String o, String text) {
            populatePopup(entries.stream().filter(c -> c.match(text)).toList());
        }
        private void handleTextFocused(ObservableValue<? extends Boolean> ob, Boolean o, Boolean focused) {
            if (focused) {
                popup.hide();
            } else {
                if (getText().isBlank()) {
                    populatePopup(entries);
                } else {
                    populatePopup(entries.stream().filter(c -> c.match(getText())).toList());
                }
            }
        }
        private void populatePopup(List<Command> filtered) {

            List<CustomMenuItem> menuItems = new ArrayList<>();
            for (Command cmd : filtered) {
                var text = new Text(cmd.name());
                text.setFill(Color.GRAY);
                TextFlow menuText = new TextFlow(text);
                menuText.setPrefWidth(AcTextField.this.getWidth());
                CustomMenuItem item = new CustomMenuItem(menuText, true);
                menuItems.add(item);
            }

            popup.getItems().clear();
            popup.getItems().addAll(menuItems);
            popup.show(AcTextField.this, Side.BOTTOM, -7, 7);

        }

    }


    interface Command {
        Command empty = new Empty();
        String name();
        default String description() { return ""; }
        default String promptText() { return ""; }
        default boolean match(String text) {
            if (name().toLowerCase().contains(text.toLowerCase())) {
                return true;
            }
            if (description().toLowerCase().contains(text.toLowerCase())) {
                return true;
            }
            return false;
        }
    }
    record Empty() implements Command {
        @Override public String name() { return ""; }
    }
    static class FindAll implements Command {
        String text;
        public FindAll() { this(""); }
        public FindAll(String text) { this.text = text; }
        @Override public String name() { return "FindAll"; }
        public String text() { return text; }
    }
    static class GoTo implements Command {
        @Override public String name() { return "GoTo"; }
    }
    static class ToLowerCase implements Command {
        @Override public String name() { return "ToLowerCase"; }
    }
    static class ToUpperCase implements Command {
        @Override public String name() { return "ToUpperCase"; }
    }
    static class Sort implements Command {
        @Override public String name() { return "Sort"; }
    }
    static class Unique implements Command {
        @Override public String name() { return "Unique"; }
    }
    static class Calc implements Command {
        @Override public String name() { return "Calc"; }
    }

}
