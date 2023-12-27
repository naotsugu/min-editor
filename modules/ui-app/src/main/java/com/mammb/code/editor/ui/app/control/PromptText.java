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

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import static com.mammb.code.editor.ui.app.control.CssProcessor.CSS;
import static javafx.scene.AccessibleAttribute.OFFSET_AT_POINT;

/**
 * The PromptText.
 * @author Naotsugu Kobayashi
 */
public class PromptText extends StackPane {

    /** The text field. */
    private final TextField text;

    /** The prompt icon. */
    private final Group prompt;


    /**
     * Constructor.
     */
    public PromptText() {
        text = new InputText();
        prompt = new Group();
        StackPane.setAlignment(prompt, Pos.CENTER_LEFT);
        StackPane.setMargin(prompt, new Insets(0, 0, 0, 8));
        getChildren().addAll(text, prompt);
        getStyleClass().add(styleClass);
    }


    /**
     * Set the prompt icon.
     * @param icon the prompt icon
     */
    public void setPrompt(Icon icon) {
        prompt.getChildren().clear();
        prompt.getChildren().add(icon);
    }


    /**
     * Get the text offset at point.
     * @param point the point 2D
     * @return the text offset at point, {@code -1} if out of range
     */
    public int getOffsetAtPoint(Point2D point) {
        var offset = (Integer) text.queryAccessibleAttribute(OFFSET_AT_POINT, point);
        return (offset == null || offset > text.getText().length()) ? -1 : offset;
    }


    /**
     * The current position of the caret within the text.
     * @return the current position
     */
    public final int getCaretPosition() { return text.getCaretPosition(); }


    /**
     * Moves the caret to after the last char of the text. This function
     * also has the effect of clearing the selection.
     */
    public void end() {
        text.end();
    }


    /**
     * Get the text field.
     * @return the text field
     */
    public TextField textField() {
        return text;
    }


    /**
     * Get the text.
     * @return the text
     */
    public String getText() {
        var ret = text.getText();
        return (ret == null) ? "" : ret;
    }


    /** The style class. */
    private static final String styleClass = "prompt-field";

    /** The css. */
    static final Css css = st -> CSS."""
        .\{styleClass} > .text-input {
          -fx-padding: 0.333333em 0.583em 0.333333em 2.333333em;
        }
        """;

}
