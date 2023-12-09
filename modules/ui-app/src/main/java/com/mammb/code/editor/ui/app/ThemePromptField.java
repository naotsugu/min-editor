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

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * The ThemeCommandField.
 * @author Naotsugu Kobayashi
 */
public class ThemePromptField extends StackPane {

    /** The theme color. */
    private final ThemeColor themeColor;

    /** The text field. */
    private final ThemeTextField text;

    /** The prompt. */
    private final Group prompt;


    /**
     * Constructor.
     * @param tc the theme color
     */
    public ThemePromptField(ThemeColor tc) {

        themeColor = tc;
        text = new ThemeTextField(tc);
        prompt = new Group();

        setPrompt(ThemeIcon.alt(themeColor));
        setFocusTraversable(false);
        setBackground(themeColor.backgroundFill());
        setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT)));

        var hbox = new HBox();
        hbox.getChildren().addAll(prompt, text);
        hbox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(text, Priority.ALWAYS);
        HBox.setMargin(prompt, new Insets(0, 0, 0, 8));
        getChildren().addAll(hbox);

        initHandler();
    }


    /**
     * Get the text property.
     * @return the text property
     */
    public StringProperty textProperty() {
        return text.textProperty();
    }


    /**
     * Set the prompt icon.
     * @param icon the prompt icon
     */
    public void setPrompt(ThemeIcon icon) {
        if (prompt.getChildren().isEmpty()) {
            prompt.getChildren().add(icon.smaller());
        } else {
            prompt.getChildren().set(0, icon.smaller());
        }
    }


    /**
     * Initialize handler.
     */
    private void initHandler() {
        text.focusedProperty().addListener((ob, o, n) -> {
            setBorder(new Border(new BorderStroke(
                n ? themeColor.foreground() : Color.TRANSPARENT,
                BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT)));
        });
    }

}
