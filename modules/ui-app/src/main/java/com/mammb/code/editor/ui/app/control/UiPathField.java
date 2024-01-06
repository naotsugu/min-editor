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

    /** The path navi. */
    private UiPathMenu pathNavi;

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
            showPathMenu(path);
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
        if (path.equals(addressPath) ||
            pathNavi != null && pathNavi.getParent().equals(path.path())) {
            return;
        }
        addressPath = path;
        runOnTimeline(ae -> showPathMenu(path));
    }


    /**
     * Show path menu.
     * @param path the address path
     */
    private void showPathMenu(AddressPath path) {
        if (timeline == null) {
            return;
        }
        stopTimeline();
        if (pathNavi != null) {
            pathNavi.hide();
        }
        pathNavi = new UiPathMenu(path.path(), path.listItem(), handlePathSelect());
        addressPath = null;
        pathNavi.setOnHidden(e -> {
        });
        Point2D point = text.getScreenPointAtIndex(path.stringLength());
        pathNavi.show(getScene().getWindow(), point.getX(), point.getY());
        pathNavi.requestFocus();
    }


    /**
     * Handle path select.
     * @return the path select handler
     */
    private Consumer<Path> handlePathSelect() {
        return path -> {
            var point = pathNavi.getAnchor();
            clearPathNavi();
            Path raw = (path instanceof PathItem item) ? item.raw() : path;
            if (Files.isDirectory(raw)) {
                var p = AddressPath.of(raw);
                pathNavi = new UiPathMenu(p.path(), p.listItem(), handlePathSelect());
                pathNavi.show(getScene().getWindow(), point.getX(),
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
    public void setOnTextCommitted(Consumer<String> consumer) {
        text.setOnTextCommitted(consumer);
    }


    /**
     * Stop path selecting.
     */
    private void clearPathNavi() {
        stopTimeline();
        addressPath = null;
        if (pathNavi != null) {
            pathNavi.hide();
            pathNavi = null;
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
