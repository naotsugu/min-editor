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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 * The AddressBar.
 * @author Naotsugu Kobayashi
 */
public class AddressBar extends StackPane {

    /** The theme color. */
    private final ThemeColor themeColor;

    private final TextField addressText;
    private final Button forward;
    private final Button back;

    /**
     * Constructor.
     * @param themeColor the theme color
     */
    public AddressBar(ThemeColor themeColor) {

        this.themeColor = themeColor;
        this.addressText = new ThemeTextField(themeColor);
        this.forward = new ThemeButton(ThemeIcon.arrowRightShort(themeColor), themeColor);
        this.back = new ThemeButton(ThemeIcon.arrowLeftShort(themeColor), themeColor);

        setBackground(themeColor.backgroundFill());

        var hBox = new HBox(4);
        hBox.getChildren().addAll(back, forward, addressText);
        HBox.setHgrow(addressText, Priority.ALWAYS);
        getChildren().add(hBox);

        StackPane.setMargin(hBox, new Insets(4));
    }

    StringProperty addressTextProperty() {
        return addressText.textProperty();
    }

}
