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

import javafx.scene.control.Button;
import javafx.scene.layout.Border;

/**
 * The UiButton.
 * @author Naotsugu Kobayashi
 */
public class UiButton extends Button {

    /** The theme color. */
    private final UiColor uiColor;

    /**
     * Constructor.
     * @param text the button label text
     * @param icon the icon image
     * @param themeColor the theme color
     */
    private UiButton(String text, UiIcon icon, UiColor themeColor) {
        super(text);
        uiColor = themeColor;

        setText(text);
        setGraphic(icon);
        setFocusTraversable(false);

        icon.disableProperty().bind(disableProperty());
        setBackground(uiColor.backgroundFill());
        setBorder(Border.EMPTY);
        setTextFill(uiColor.foreground());
        initHandler();
    }


    public UiButton(String text, UiColor tc) {
        this(text, null, tc);
    }

    public UiButton(UiIcon icon, UiColor tc) {
        this(null, icon, tc);
    }

    /**
     * Initialize handler.
     */
    private void initHandler() {
        setOnMouseEntered(e -> setBackground(disabledProperty().get()
            ? uiColor.backgroundFill()
            : uiColor.backgroundActiveFill()));
        setOnMouseExited(e -> setBackground(uiColor.backgroundFill()));
    }

}
