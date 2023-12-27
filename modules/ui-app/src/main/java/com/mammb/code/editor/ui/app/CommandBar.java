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

import com.mammb.code.editor.ui.app.control.FlatButton;
import com.mammb.code.editor.ui.app.control.Icon;
import com.mammb.code.editor.ui.app.control.PathField;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * The CommandBar.
 * @author Naotsugu Kobayashi
 */
public class CommandBar extends HBox {

    /** The forward button. */
    private final Button forward;
    /** The back button. */
    private final Button backward;
    /** The path field. */
    private final PathField pathField;
    /** The menu button. */
    private final Button menu;


    /**
     * Constructor.
     */
    public CommandBar() {

        super(4);
        setAlignment(Pos.CENTER_LEFT);

        forward = new FlatButton(Icon.arrowRightShort().larger());
        backward = new FlatButton(Icon.arrowLeftShort().larger());
        pathField = new PathField();
        menu = new FlatButton(Icon.list().larger());

        forward.setDisable(true);
        backward.setDisable(true);
        pathField.textProperty().set(Path.of(
            System.getProperty("user.home")).resolve("Untitled").toString());
        menu.setOnMouseClicked(e -> new AboutDialog().showAndWait());

        HBox.setHgrow(pathField, Priority.ALWAYS);
        getChildren().addAll(backward, forward, pathField, menu);

    }


    void setPathText(Path path) {
        pathField.textProperty().set((path == null) ? "" : path.toString());
    }


    void setPathModified(boolean modified) {
        //field.accentuatePrompt(modified);
    }


    BooleanProperty backwardDisableProperty() {
        return backward.disableProperty();
    }


    BooleanProperty forwardDisableProperty() {
        return forward.disableProperty();
    }


    /**
     * Set the text committed handler.
     * @param consumer the consumer
     */
    void setOnTextCommitted(Consumer<String> consumer) {
        pathField.setOnTextCommitted(consumer);
    }


    /**
     * Set the path select consumer.
     * @param consumer the path select consumer
     */
    void setOnPathSelected(Consumer<Path> consumer) {
        pathField.setOnPathSelect(consumer);
    }


    /**
     * Set the backward clicked handler.
     * @param runnable the backward clicked handler
     */
    void setOnBackwardClicked(Runnable runnable) {
        backward.setOnMouseClicked(e -> runnable.run());
    }


    /**
     * Set the forward clicked handler.
     * @param runnable the forward clicked handler
     */
    void setOnForwardClicked(Runnable runnable) {
        forward.setOnMouseClicked(e -> runnable.run());
    }

}
