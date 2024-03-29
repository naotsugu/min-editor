/*
 * Copyright 2023-2024 the original author or authors.
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

import com.mammb.code.editor.ui.app.control.UiFlatButton;
import com.mammb.code.editor.ui.app.control.UiIcon;
import com.mammb.code.editor.ui.app.control.UiPathField;
import com.mammb.code.editor.ui.app.control.UiSearchField;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.nio.file.Path;
import java.util.function.BiConsumer;
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
    private final UiPathField pathField;
    /** The search field. */
    private final UiSearchField searchField;
    /** The menu button. */
    private final Button menu;


    /**
     * Constructor.
     */
    public CommandBar() {

        super(4);
        setAlignment(Pos.CENTER_LEFT);

        forward = new UiFlatButton(UiIcon.arrowRightShort().larger());
        backward = new UiFlatButton(UiIcon.arrowLeftShort().larger());
        pathField = new UiPathField();
        searchField = new UiSearchField();
        menu = new UiFlatButton(UiIcon.list().larger());

        forward.setDisable(true);
        backward.setDisable(true);
        pathField.textProperty().set(Path.of(
            System.getProperty("user.home")).resolve("Untitled").toString());
        HBox.setHgrow(pathField, Priority.ALWAYS);
        pathField.setVisible(true);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.setVisible(false);

        getChildren().addAll(backward, forward, pathField, searchField, menu);
        initHandler();
    }


    /**
     * Initialize handler.
     */
    private void initHandler() {
        menu.setOnMouseClicked(e -> new AboutDialog(getScene().getWindow()).showAndWait());
        pathField.managedProperty().bind(pathField.visibleProperty());
        searchField.managedProperty().bind(searchField.visibleProperty());
        pathField.visibleProperty().bind(searchField.visibleProperty().not());
    }


    /**
     * Set visible search field.
     * @param text the search field text
     */
    void setVisibleSearchField(String text) {
        boolean visible = searchField.isVisible();
        if (!visible) {
            searchField.setVisible(true);
        }
        searchField.requestTextFieldFocus();
        if (text != null && !text.isEmpty()) {
            searchField.setSearchText(text);
        }
    }


    /**
     * Set the path text.
     * @param path the path
     */
    void setPathText(Path path) {
        pathField.setPathText(path);
    }


    /**
     * Set the path modified.
     * @param modified the path modified
     */
    void setPathModified(boolean modified) {
        pathField.setPromptAccentuate(modified);
    }


    /**
     * Get the backward disable property.
     * @return the backward disable property
     */
    BooleanProperty backwardDisableProperty() {
        return backward.disableProperty();
    }


    /**
     * Get the forward disable property.
     * @return the forward disable property
     */
    BooleanProperty forwardDisableProperty() {
        return forward.disableProperty();
    }


    /**
     * Set the text committed handler.
     * @param consumer the consumer
     */
    void setOnPathTextCommitted(BiConsumer<String, KeyEvent> consumer) {
        pathField.setOnTextCommitted(consumer);
    }


    /**
     * Set the search text committed handler.
     * @param consumer the consumer
     */
    void setOnSearchTextCommitted(BiConsumer<String, KeyEvent> consumer) {
        searchField.setOnTextCommitted(consumer);
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


    /**
     * Set the exit action.
     * @param runnable the exit action handler
     */
    void setOnExit(Runnable runnable) {
        addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.TAB || e.getCode() == KeyCode.ESCAPE) {
                e.consume();
                runnable.run();
            }
        });
    }

}
