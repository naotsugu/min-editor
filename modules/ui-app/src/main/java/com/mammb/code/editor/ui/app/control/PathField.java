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

import javafx.animation.Timeline;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * The PathField.
 * @author Naotsugu Kobayashi
 */
public class PathField extends StackPane {

    /** The timeline. */
    private Timeline timeline;

    /** The prompt text. */
    private PromptText text;


    /**
     * Constructor.
     */
    public PathField() {
        text = new PromptText();
        getChildren().add(text);
        initHandler();
    }


    /**
     * Initialize handler.
     */
    private void initHandler() {
        text.disabledProperty().addListener(this::handleTextDisabled);
        text.addEventFilter(KeyEvent.KEY_PRESSED, this::handleTextKeyPressed);
        text.setOnMouseMoved(this::handleTextMouseMoved);
        text.setOnMouseExited(this::handleTextMouseExited);
        text.textProperty().addListener(this::handleTextChanged);

    }


    /**
     * Handle text disabled.
     * @param ob the observable value
     * @param o the old value
     * @param disabled the new value
     */
    private void handleTextDisabled(ObservableValue<? extends Boolean> ob, Boolean o, Boolean disabled) {
        if (disabled) {
            stopTimeline();
        }
    }


    /**
     * Handle key pressed.
     * @param e the key event
     */
    private void handleTextKeyPressed(KeyEvent e) {

    }


    /**
     * Handle text mouse moved.
     * @param e the key event
     */
    private void handleTextMouseMoved(MouseEvent e) {

    }


    /**
     * Handle text mouse moved.
     * @param e the key event
     */
    private void handleTextMouseExited(MouseEvent e) {
        stopTimeline();
    }


    /**
     * Handle text changed.
     * @param ob the observable value
     * @param o the old value
     * @param n the new value
     */
    private void handleTextChanged(ObservableValue<? extends String> ob, String o, String n) {

    }


    /**
     * Get the text property.
     * @return the text property
     */
    public final StringProperty textProperty() {
        return text.textProperty();
    }


    /**
     * Stop timeline.
     */
    private void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

}
