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

import com.mammb.code.editor.core.Context;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;

/**
 * FxMenuItem.
 * @author Naotsugu Kobayashi
 */
public class FxMenuItem extends MenuItem {

    /** The style. */
    private static final String style = switch (Context.platform) {
        case "windows" -> "-fx-font: normal 10pt System;";
        default -> "-fx-font: normal 11pt System;";
    };

    /**
     * Constructs a new FxMenuItem with the specified text, accelerator key combination,
     * disable state, and action event handler.
     *
     * @param text the text to display on the menu item
     * @param accelerator the key combination that serves as a shortcut for the menu item
     * @param disable a boolean indicating whether the menu item is initially disabled
     * @param eventHandler the event handler to handle action events for the menu item
     */
    public FxMenuItem(
            String text,
            KeyCombination accelerator,
            boolean disable,
            EventHandler<ActionEvent> eventHandler) {
        super(text);
        setAccelerator(accelerator);
        setOnAction(eventHandler);
        setDisable(disable);
        setStyle(style);
    }

    /**
     * Constructs a new FxMenuItem with the specified text, accelerator key combination,
     * and action event handler. The menu item is enabled by default.
     *
     * @param text the text to display on the menu item
     * @param accelerator the key combination that serves as a shortcut for the menu item
     * @param eventHandler the event handler to handle action events for the menu item
     */
    public FxMenuItem(
            String text,
            KeyCombination accelerator,
            EventHandler<ActionEvent> eventHandler) {
        this(text, accelerator, false, eventHandler);
    }

}
