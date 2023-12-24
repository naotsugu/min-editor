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

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import static com.mammb.code.editor.ui.app.control.CssProcessor.CSS;

/**
 * The PromptField.
 * @author Naotsugu Kobayashi
 */
public class PromptField extends StackPane {

    /** The text field. */
    private final TextField text;

    /** The prompt icon. */
    private final Icon prompt;


    /**
     * Constructor.
     */
    public PromptField() {
        text = new TextField();
        prompt = Icon.contentOf("");
        var hbox = new HBox(prompt, text);
        hbox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(text, Priority.ALWAYS);
        getChildren().addAll(hbox);
        getStyleClass().add(styleClass);
    }

    /** The style class. */
    private static final String styleClass = "prompt-field";

    static final Css css = st -> CSS."""
        .\{styleClass} {
          -fx-background-color:\{st.back};
          -fx-background-insets: 0;
          -fx-background-radius: 2;
        }
        """;

}
