/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.ui.fx;

import com.mammb.code.editor.core.Action;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.ui.Command;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyEvent;

/**
 * AppContextMenu.
 * @author Naotsugu Kobayashi
 */
public class AppContextMenu extends ContextMenu {

    /** The target {@link EditorPane}. */
    private final EditorPane editorPane;

    /**
     * Constructor.
     * @param editorPane the target {@link EditorPane}
     */
    public AppContextMenu(EditorPane editorPane) {
        super();
        this.editorPane = editorPane;
        buildMenuItems();
        buildEventHandler();
    }


    /**
     * Build the menu items.
     */
    private void buildMenuItems() {

        boolean textSelected = editorPane.query(Query.selectedCounts) > 0;
        String style = "-fx-font: normal 10pt System;";

        var cut = new MenuItem("Cut");
        cut.setStyle(style);
        cut.setAccelerator(CommandKeys.SC_X);
        cut.setOnAction(e -> {
            e.consume();
            editorPane.execute(new Command.ActionCommand(Action.cut(FxClipboard.instance)));
        });
        cut.setDisable(!textSelected);

        var copy = new MenuItem("Copy");
        copy.setStyle(style);
        copy.setAccelerator(CommandKeys.SC_C);
        copy.setOnAction(e -> editorPane.execute(new Command.ActionCommand(Action.copy(FxClipboard.instance))));
        copy.setDisable(!textSelected);

        var paste = new MenuItem("Paste");
        paste.setStyle(style);
        paste.setAccelerator(CommandKeys.SC_V);
        paste.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.paste(FxClipboard.instance, false))));
        paste.setDisable(!FxClipboard.instance.hasContents());

        var pasteAs = new MenuItem("Paste with context");
        pasteAs.setStyle(style);
        pasteAs.setAccelerator(CommandKeys.SC_SV);
        pasteAs.setOnAction(_ -> editorPane.execute(new Command.ActionCommand(Action.paste(FxClipboard.instance, true))));
        pasteAs.setDisable(!FxClipboard.instance.hasContents());

        var backward = new MenuItem("Backward");
        backward.setStyle(style);
        backward.setAccelerator(CommandKeys.SC_BW);
        backward.setOnAction(_ -> editorPane.execute(new Command.Backward()));
        backward.setDisable(!editorPane.sessionHistory().hasBackward());

        var forward = new MenuItem("Forward");
        forward.setStyle(style);
        forward.setAccelerator(CommandKeys.SC_FW);
        forward.setOnAction(_ -> editorPane.execute(new Command.Forward()));
        forward.setDisable(!editorPane.sessionHistory().hasForward());

        var searchInBrowser = new MenuItem("Search In Browser");
        searchInBrowser.setStyle(style);
        searchInBrowser.setOnAction(_ -> editorPane.execute(new Command.SearchInBrowser()));
        searchInBrowser.setDisable(!textSelected);

        var translateInBrowser = new MenuItem("Translate In Browser");
        translateInBrowser.setStyle(style);
        translateInBrowser.setOnAction(_ -> editorPane.execute(new Command.TranslateInBrowser()));
        translateInBrowser.setDisable(!textSelected);

        getItems().addAll(
            cut, copy, paste, pasteAs,
            new SeparatorMenuItem(),
            backward, forward,
            new SeparatorMenuItem(),
            searchInBrowser, translateInBrowser);
    }

    private void buildEventHandler() {
        final EventHandler<KeyEvent> blockKeyPressed = keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP, DOWN, ESCAPE -> {}
                case ENTER -> {
                    keyEvent.consume();
                    EventTarget eventTarget = keyEvent.getTarget();
                    if (eventTarget instanceof Node node) {
                        MenuItem menuItem = (MenuItem) node.getProperties().get(MenuItem.class);
                        if (menuItem != null && !menuItem.isDisable() && menuItem.isVisible()) {
                            menuItem.fire();
                        }
                    }
                    Platform.runLater(this::hide);
                }
                default -> keyEvent.consume();
            }
        };
        final EventHandler<KeyEvent> blockKeyTyped = KeyEvent::consume;

        setOnShown(_ -> {
            if (getScene() != null) {
                getScene().addEventFilter(KeyEvent.KEY_PRESSED, blockKeyPressed);
                getScene().addEventFilter(KeyEvent.KEY_TYPED, blockKeyTyped);
            }
        });
        setOnHidden(_ -> {
            if (getScene() != null) {
                getScene().removeEventFilter(KeyEvent.KEY_PRESSED, blockKeyPressed);
                getScene().removeEventFilter(KeyEvent.KEY_TYPED, blockKeyTyped);
            }
        });
    }
}
