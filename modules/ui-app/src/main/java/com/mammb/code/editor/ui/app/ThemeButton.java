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

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;

/**
 * The ThemeButton.
 * @author Naotsugu Kobayashi
 */
public class ThemeButton extends Button {

    /** The theme color. */
    private final ThemeColor themeColor;

    /**
     * Constructor.
     * @param text
     * @param icon
     * @param themeColor
     */
    private ThemeButton(String text, Node icon, ThemeColor themeColor) {
        super(text);
        this.themeColor = themeColor;
        setText(text);
        setGraphic(icon);
        setFocusTraversable(false);

        icon.disableProperty().bind(disableProperty());
        setBackground(themeColor.backgroundFill());
        setBorder(Border.EMPTY);
        setTextFill(themeColor.foreground());
        initHandler();
    }


    public ThemeButton(String text, ThemeColor tc) {
        this(text, null, tc);
    }

    public ThemeButton(Node icon, ThemeColor tc) {
        this(null, icon, tc);
    }

    /**
     * Initialize handler.
     */
    private void initHandler() {
        setOnMouseEntered(e -> setBackground(themeColor.backgroundActiveFill()));
        setOnMouseExited(e -> setBackground(themeColor.backgroundFill()));
    }

}