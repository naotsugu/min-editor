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
package com.mammb.code.editor.ui.app;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

import static javafx.scene.AccessibleAttribute.OFFSET_AT_POINT;

/**
 * The UiAddressField.
 * @author Naotsugu Kobayashi
 */
public class UiAddressField extends UiPromptField {

    /**
     * Constructor.
     *
     * @param themeColor the theme color
     */
    public UiAddressField(UiColor themeColor) {
        super(themeColor);
        initHandler();
    }

    /**
     * Initialize handler.
     */
    private void initHandler() {
        text().textProperty().addListener(this::handleTextProperty);
        text().setOnMouseMoved(this::handleMouseMoved);
    }


    private String extension(String string) {
        int index = string.lastIndexOf(".") + 1;
        return (index > 0 && index < string.length()) ? string.substring(index) : "";
    }


    private void handleTextProperty(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
        setPrompt(UiIcon.contentOf(uiColor(), extension(newValue)));
    }


    private void handleMouseMoved(MouseEvent e) {

        String text = text().getText();
        if (text == null || text.isBlank()) return;

        Integer attr = (Integer) text().queryAccessibleAttribute(OFFSET_AT_POINT, new Point2D(e.getScreenX(), e.getScreenY()));
        if (attr == null) return;
        int index = Math.clamp(attr, 0, text.length() - 1);

        // TODO

    }

}
