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

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * The UiCommandBar.
 * @author Naotsugu Kobayashi
 */
public class UiCommandBar extends StackPane {

    /** The theme color. */
    private final UiColor uiColor;

    /** The prompt field. */
    private final UiAddressField field;
    /** The forward button. */
    private final Button forward;
    /** The back button. */
    private final Button backward;
    /** The menu button. */
    private final Button menu;


    /**
     * Constructor.
     * @param themeColor the theme color
     */
    public UiCommandBar(UiColor themeColor) {

        uiColor = themeColor;
        field = new UiAddressField(uiColor);
        forward = new UiButton(UiIcon.arrowRightShort(uiColor).larger(), uiColor);
        backward = new UiButton(UiIcon.arrowLeftShort(uiColor).larger(), uiColor);
        menu = new UiButton(UiIcon.list(uiColor).larger(), uiColor);

        field.textProperty().set(Path.of(System.getProperty("user.home")).resolve("Untitled").toString());
        forward.setDisable(true);
        backward.setDisable(true);
        menu.setOnMouseClicked(e -> new UiAboutDialog(uiColor).showAndWait());

        setBackground(uiColor.backgroundFill());

        var hbox = new HBox(4);
        hbox.getChildren().addAll(backward, forward, field, menu);
        hbox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);
        HBox.setMargin(field, new Insets(0, 0, 0, 8));
        getChildren().add(hbox);

        StackPane.setMargin(hbox, new Insets(4));
    }

    void setPathText(Path path) {
        field.textProperty().set((path == null) ? "" : path.toString());
    }

    void setPathModified(boolean modified) {
        field.accentuatePrompt(modified);
    }

    BooleanProperty backwardDisableProperty() {
        return backward.disableProperty();
    }

    BooleanProperty forwardDisableProperty() {
        return forward.disableProperty();
    }

    void onTextCommitted(Consumer<String> consumer) {
        field.textCommitted(consumer);
    }

    void onPathSelected(Consumer<Path> consumer) {
        field.onPathSelected(consumer);
    }

    void onBackwardClicked(Runnable runnable) {
        backward.setOnMouseClicked(e -> runnable.run());
    }

    void onForwardClicked(Runnable runnable) {
        forward.setOnMouseClicked(e -> runnable.run());
    }

}
