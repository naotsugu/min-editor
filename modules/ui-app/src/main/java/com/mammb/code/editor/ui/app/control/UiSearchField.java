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
package com.mammb.code.editor.ui.app.control;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import java.util.function.Consumer;

/**
 * The search field.
 * @author Naotsugu Kobayashi
 */
public class UiSearchField extends StackPane {

    /** The prompt text. */
    private final UiPromptText text;


    /**
     * Constructor.
     */
    public UiSearchField() {
        text = new UiPromptText();
        text.setPrompt(UiIcon.search());
        text.textField().setFocusTraversable(false);
        getChildren().add(text);
        initHandler();
    }


    /**
     * Initialize handler.
     */
    private void initHandler() {
        text.textField().addEventFilter(KeyEvent.KEY_PRESSED, this::handleTextKeyPressed);
    }


    /**
     * Handle key pressed.
     * @param e the key event
     */
    private void handleTextKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            e.consume();
            setVisible(false);
        }
    }

    public void requestTextFieldFocus() {
        text.textField().requestFocus();
    }


    /**
     * Set the text committed handler.
     * @param consumer the consumer
     */
    public void setOnTextCommitted(Consumer<String> consumer) {
        text.setOnTextCommitted(consumer);
    }

}
