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

import javafx.scene.control.Button;

import static com.mammb.code.editor.ui.app.control.CssProcessor.CSS;

/**
 * The FlatButton.
 * @author Naotsugu Kobayashi
 */
public class UiFlatButton extends Button {

    /**
     * Constructor.
     * @param text the button text
     * @param icon the icon
     */
    public UiFlatButton(String text, UiIcon icon) {
        super(text);
        getStyleClass().add(styleClass);
        setFocusTraversable(false);
        setGraphic(icon);
    }


    /**
     * Constructor.
     * @param icon the icon
     */
    public UiFlatButton(UiIcon icon) {
        this("", icon);
    }


    /** The style class name. */
    static final String styleClass = "flat-button";

    /** The css. */
    static final Css css = st -> CSS."""
        .\{styleClass} {
          -fx-background-color:transparent;
          -fx-background-insets: 0;
          -fx-background-radius: 2;
        }
        .\{styleClass}:hover {
          -fx-background-color:\{st.text}22;
        }
        """;

}
