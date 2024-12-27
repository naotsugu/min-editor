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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.StageStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

/**
 * The CommandPalette.
 * @author Naotsugu Kobayashi
 */
public class CommandPalette extends Dialog<Command> {

    /** The logger. */
    private static final System.Logger log = System.getLogger(CommandPalette.class.getName());

    /** The default prompt text. */
    private static final String PROMPT = " <enter command> ";

    private final HBox box;
    private final Label commandLabel;
    private final AcTextField textField;
    private Class<? extends Command> cmdType;

    public CommandPalette(Node node) {
        this(node, null);
    }

    public CommandPalette(Node node, Class<? extends Command> init) {
        super();
        this.box = new HBox();
        this.commandLabel = new Label();
        this.cmdType = init;
        this.textField = new AcTextField(this);

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
            """);

        // init text field
        textField.setOnKeyPressed(e -> {
            if (e.getCode() == ESCAPE) {
                setResult(new Command.Empty());
                close();
                e.consume();
            } else if (e.getCode() == ENTER && cmdType != null) {
                fireCommand();
                e.consume();
            } else if (e.getCode() == BACK_SPACE && textField.getText().isEmpty()) {
                cmdType = null;
                initCommandLabel();
            } else if (e.getCode() == DOWN) {
                textField.popup.requestFocus();
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
        pane.getStyleClass().add("app-command-palette-dialog-pane");

        initCommandLabel();
    }


    private void selectCommand(Class<? extends Command> commandClass) {
        cmdType = commandClass;
        textField.setText("");
        if (Command.RequireArgs.class.isAssignableFrom(commandClass)) {
            initCommandLabel();
        } else {
            fireCommand();
        }
    }

    private void initCommandLabel() {
        var type = cmdType;
        commandLabel.setVisible(type != null);
        commandLabel.setText((type == null) ? "" : type.getSimpleName());
        textField.setPromptText((type == null) ? PROMPT : Command.promptText(type));
    }

    private void fireCommand() {
        if (cmdType == null) {
            setResult(new Command.Empty());
        } else {
            if (Command.RequireArgs1.class.isAssignableFrom(cmdType)) {
                setResult(Command.newInstance(cmdType, textField.getText().trim()));
            } else {
                setResult(Command.newInstance(cmdType));
            }
        }
        close();
    }


    /**
     * Auto Complete TextField.
     */
    static class AcTextField extends TextField {
        final CommandPalette commandPalette;
        final ContextMenu popup;
        SequencedMap<String, CustomMenuItem> items;

        public AcTextField(CommandPalette commandPalette) {
            super();
            this.commandPalette = commandPalette;
            this.popup = new ContextMenu();

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
            if (commandPalette.cmdType != null) {
                popup.hide();
            } else {
                populatePopup(text);
            }
        }
        private void handleTextFocused(ObservableValue<? extends Boolean> ob, Boolean o, Boolean focused) {
            if (focused) {
                popup.hide();
            } else {
                populatePopup(getText());
            }
        }

        private SequencedMap<String, CustomMenuItem> commandTypeItems() {
            SequencedMap<String, CustomMenuItem> keywordMappedItems = new LinkedHashMap<>();
            for (var entry : Command.values().entrySet()) {
                var text = new Text(entry.getKey());
                text.setFill(Color.GRAY);
                TextFlow menuText = new TextFlow(text);
                // TODO displaying description and shortcut labels
                menuText.setPrefWidth(AcTextField.this.getWidth());
                CustomMenuItem item = new CustomMenuItem(menuText, true);
                item.setOnAction(e -> commandPalette.selectCommand(entry.getValue()));
                keywordMappedItems.put(entry.getKey(), item);
            }
            return keywordMappedItems;
        }

        private void populatePopup(String input) {
            if (items == null) {
                 items = commandTypeItems();
            }
            List<CustomMenuItem> menuItems = items.entrySet().stream()
                .filter(e -> e.getKey().toLowerCase().contains(input.toLowerCase()))
                .map(Map.Entry::getValue)
                .toList();
            popup.getItems().clear();
            if (!menuItems.isEmpty()) {
                popup.getItems().addAll(menuItems);
                popup.show(AcTextField.this, Side.BOTTOM, -7, 7);
                focusFirstItem();
            }
        }
        void focusFirstItem() {
            if (popup.getSkin() == null) {
                return;
            }
            Node node = popup.getSkin().getNode().lookup(".menu-item");
            node.requestFocus();
            node.fireEvent(new KeyEvent(KEY_PRESSED, "", "", DOWN, false, false, false, false));
        }
    }

}
