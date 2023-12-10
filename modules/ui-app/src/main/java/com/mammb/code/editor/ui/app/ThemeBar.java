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
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 * The ThemeBar.
 * @author Naotsugu Kobayashi
 */
public class ThemeBar extends StackPane {

    /** The theme color. */
    private final ThemeColor themeColor;

    /** The prompt field. */
    private final ThemePromptField field;
    /** The forward button. */
    private final Button forward;
    /** The back button. */
    private final Button back;
    /** The menu button. */
    private final Button menu;

    /**
     * Constructor.
     * @param tc the theme color
     */
    public ThemeBar(ThemeColor tc) {

        themeColor = tc;
        field = new ThemePromptField(themeColor);
        forward = new ThemeButton(ThemeIcon.arrowRightShort(themeColor).larger(), themeColor);
        back = new ThemeButton(ThemeIcon.arrowLeftShort(themeColor).larger(), themeColor);
        menu = new ThemeButton(ThemeIcon.list(themeColor).larger(), themeColor);

        setBackground(themeColor.backgroundFill());

        var hbox = new HBox(4);
        hbox.getChildren().addAll(back, forward, field, menu);
        hbox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);
        HBox.setMargin(field, new Insets(0, 0, 0, 8));
        getChildren().add(hbox);

        StackPane.setMargin(hbox, new Insets(4));
    }

    StringProperty addressTextProperty() {
        return field.textProperty();
    }

}
