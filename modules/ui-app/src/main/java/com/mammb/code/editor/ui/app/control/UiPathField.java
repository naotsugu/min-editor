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
package com.mammb.code.editor.ui.app.control;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The PathField.
 * @author Naotsugu Kobayashi
 */
public class UiPathField extends StackPane {

    /** The prompt text. */
    private final UiPromptText text;

    /** The timeline. */
    private Timeline timeline;

    /** The path menu. */
    private UiPathMenu pathMenu;

    /** The address path on timeline frame. */
    private AddressPath addressPath;

    /** The path select consumer. */
    private Consumer<Path> pathSelectConsumer;


    /**
     * Constructor.
     */
    public UiPathField() {
        text = new UiPromptText();
        text.textField().setFocusTraversable(false);
        getChildren().add(text);
        initHandler();
    }


    /**
     * Initialize handler.
     */
    private void initHandler() {
        text.textField().disabledProperty().addListener(this::handleTextDisabled);
        text.textField().addEventFilter(KeyEvent.KEY_PRESSED, this::handleTextKeyPressed);
        text.textField().setOnMouseMoved(this::handleTextMouseMoved);
        text.textField().setOnMouseExited(this::handleTextMouseExited);
        text.textField().textProperty().addListener(this::handleTextChanged);
    }


    /**
     * Handle text disabled.
     * @param ob the observable value
     * @param o the old value
     * @param disabled the new value
     */
    private void handleTextDisabled(ObservableValue<? extends Boolean> ob, Boolean o, Boolean disabled) {
        if (disabled) stopTimeline();
    }


    /**
     * Handle key pressed.
     * @param e the key event
     */
    private void handleTextKeyPressed(KeyEvent e) {
        stopTimeline();
        if (e.getCode() == KeyCode.DOWN) {
            if (text.getText().isBlank()) return;

            e.consume();
            var index = text.getCaretPosition();
            var path = AddressPath.of(text.getText()).dirOn(index);
            addressPath = path;
            UiPathMenu menu = new UiPathMenu(path.path(), path.listItem(), handlePathSelect());
            Point2D p = text.getScreenPointAtIndex(path.stringLength());
            showPathMenu(menu, p);
        }
    }


    /**
     * Handle text mouse moved.
     * @param e the key event
     */
    private void handleTextMouseMoved(MouseEvent e) {

        if (!getScene().getWindow().isFocused() || text.getText().isBlank()) {
            stopTimeline();
            return;
        }

        var point = new Point2D(e.getScreenX(), e.getScreenY());
        var index = text.getOffsetAtPoint(point);
        if (index <= 0 || index >= text.getText().length()) {
            stopTimeline();
            return;
        }

        var path = AddressPath.of(text.getText()).dirOn(index);
        UiPathMenu pathNaviRef = pathMenu;
        if (path.equals(addressPath) ||
            pathNaviRef != null && pathNaviRef.getParent().equals(path.path())) {
            return;
        }
        addressPath = path;
        UiPathMenu menu = new UiPathMenu(path.path(), path.listItem(), handlePathSelect());
        Point2D p = text.getScreenPointAtIndex(path.stringLength());
        runOnTimeline(ae -> showPathMenu(menu, p));
    }


    /**
     * Show path menu.
     * @param menu the path menu
     */
    private void showPathMenu(UiPathMenu menu, Point2D point) {
        stopTimeline();
        if (pathMenu != null) {
            pathMenu.hide();
        }
        pathMenu = menu;
        addressPath = null;
        pathMenu.setOnHidden(e -> {  });
        pathMenu.show(getScene().getWindow(), point.getX(), point.getY());
        pathMenu.requestFocus();
    }


    /**
     * Handle path select.
     * @return the path select handler
     */
    private Consumer<Path> handlePathSelect() {
        return path -> {
            var point = pathMenu.getAnchor();
            clearPathNavi();
            Path raw = (path instanceof PathItem item) ? item.raw() : path;
            if (Files.isDirectory(raw)) {
                var p = AddressPath.of(raw);
                pathMenu = new UiPathMenu(p.path(), p.listItem(), handlePathSelect());
                pathMenu.show(getScene().getWindow(), point.getX(),
                    text.localToScreen(text.getBoundsInLocal()).getMaxY());
            } else if (Files.isRegularFile(raw) && pathSelectConsumer != null) {
                pathSelectConsumer.accept(raw);
            }
        };
    }


    /**
     * Run on timeline.
     * @param eventEventHandler the action handler event
     */
    private void runOnTimeline(EventHandler<ActionEvent> eventEventHandler) {
        stopTimeline();
        timeline = new Timeline();
        timeline.setCycleCount(1);
        var keyFrame = new KeyFrame(Duration.millis(1200), eventEventHandler);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }


    /**
     * Handle text mouse moved.
     * @param e the key event
     */
    private void handleTextMouseExited(MouseEvent e) {
        stopTimeline();
    }


    /**
     * Handle text changed.
     * @param ob the observable value
     * @param o the old value
     * @param n the new value
     */
    private void handleTextChanged(ObservableValue<? extends String> ob, String o, String n) {
        var extension = UiIcon.extension(n);
        if (o.isBlank() || !Objects.equals(UiIcon.extension(o), extension)) {
            text.setPrompt(UiIcon.contentOf(extension));
        }
    }


    /**
     * Set accentuate the prompt.
     * @param accentuate accentuate
     */
    public void setPromptAccentuate(boolean accentuate) {
        text.setPromptAccentuate(accentuate);
    }


    /**
     * Set the path text.
     * @param path the path
     */
    public void setPathText(Path path) {
        text.textField().textProperty().set((path == null) ? "" : path.toString());
        text.end();
    }


    /**
     * Get the text property.
     * @return the text property
     */
    public final StringProperty textProperty() {
        return text.textField().textProperty();
    }


    /**
     * Set the path select consumer.
     * @param consumer the path select consumer
     */
    public void setOnPathSelect(Consumer<Path> consumer) {
        pathSelectConsumer = consumer;
    }


    /**
     * Set the path text committed handler.
     * @param consumer the consumer
     */
    public void setOnTextCommitted(BiConsumer<String, KeyEvent> consumer) {
        text.setOnTextCommitted(consumer);
    }


    /**
     * Stop path selecting.
     */
    private void clearPathNavi() {
        stopTimeline();
        addressPath = null;
        if (pathMenu != null) {
            pathMenu.hide();
            pathMenu = null;
        }
    }


    /**
     * Stop timeline.
     */
    private void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

}
