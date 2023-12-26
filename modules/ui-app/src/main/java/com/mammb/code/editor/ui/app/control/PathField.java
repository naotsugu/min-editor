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

import com.mammb.code.editor.ui.app.AddressPath;
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
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The PathField.
 * @author Naotsugu Kobayashi
 */
public class PathField extends StackPane {

    /** The timeline. */
    private Timeline timeline;

    /** The prompt text. */
    private final PromptText text;

    /** The path navi. */
    private final PathNavi pathNavi;


    /**
     * Constructor.
     */
    public PathField() {
        text = new PromptText();
        pathNavi = new PathNavi();
        getChildren().add(text);
        initHandler();
    }


    /**
     * Initialize handler.
     */
    private void initHandler() {

        text.disabledProperty().addListener(this::handleTextDisabled);
        text.addEventFilter(KeyEvent.KEY_PRESSED, this::handleTextKeyPressed);
        text.setOnMouseMoved(this::handleTextMouseMoved);
        text.setOnMouseExited(this::handleTextMouseExited);
        text.textProperty().addListener(this::handleTextChanged);

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

            var index = text.getCaretPosition();
            var bounds = text.localToScreen(text.getBoundsInLocal());
            var point = new Point2D(bounds.getMinX() + (index * 8), bounds.getMaxY());
            showPathNavi(index, point);
        }
    }


    private void showPathNavi(int index, Point2D point) {
        var path = AddressPath.of(Path.of(text.getText()));
        pathNavi.put(path.dirOn(index), path.listSibling(index), handlePathSelect());
        pathNavi.show(getScene().getWindow(), point.getX(), point.getY());
    }


    private Consumer<Path> handlePathSelect() {
        return path -> {};
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
        runOnTimeline(_ -> showPathNavi(index, new Point2D(
                point.getX(), text.localToScreen(text.getBoundsInLocal()).getMaxY()))
        );
    }


    private void runOnTimeline(EventHandler<ActionEvent> eventEventHandler) {
        timeline = new Timeline();
        timeline.setCycleCount(1);
        var keyFrame = new KeyFrame(Duration.millis(1000), eventEventHandler);
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
        var extension = Icon.extension(n);
        if (!Objects.equals(Icon.extension(o), extension)) {
            text.setPrompt(Icon.contentOf(extension));
        }
    }


    /**
     * Get the text property.
     * @return the text property
     */
    public final StringProperty textProperty() {
        return text.textProperty();
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
