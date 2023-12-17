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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import static javafx.scene.AccessibleAttribute.OFFSET_AT_POINT;

/**
 * The UiAddressField.
 * @author Naotsugu Kobayashi
 */
public class UiAddressField extends UiPromptField {

    /** The timeline. */
    private Timeline timeline;

    private PopupWindow popup;

    private int pathPositionIndex = -1;

    private Consumer<Path> pathSelectConsumer;


    /**
     * Constructor.
     * @param themeColor the theme color
     */
    public UiAddressField(UiColor themeColor) {
        super(themeColor);
        initHandler();
    }


    /**
     * Initialize handler.
     */
    private void initHandler() {
        text().textProperty().addListener(this::handleTextProperty);
        text().setOnMouseMoved(this::handleMouseMoved);
        text().setOnMouseExited(e -> stopTimeline());
        text().disabledProperty().addListener((ob, o, n) -> { if (n) stopTimeline(); });
        text().addEventFilter(KeyEvent.KEY_PRESSED, this::handleTextKeyPressed);
    }


    private void handleTextProperty(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
        setPrompt(UiIcon.contentOf(uiColor(), extension(newValue)));
    }


    /**
     * Key pressed handler.
     * @param e the key event
     */
    private void handleTextKeyPressed(KeyEvent e) {
        stopTimeline();
        if (e.getCode() == KeyCode.DOWN) {
            if (popup != null) {
                popup.hide();
            }
            var index = text().getCaretPosition();
            var addressPath = AddressPath.of(Path.of(text().getText()));
            var bounds = text().localToScreen(text().getBoundsInLocal());
            popup = UiPopupMenu.of(uiColor(), addressPath.listSibling(index), handlePathSelect());
            popup.setOnHidden(we -> {
                text().requestFocus();
                text().positionCaret(index);
            });
            popup.show(UiAddressField.this.getScene().getWindow(),
                bounds.getMinX() + (index * 8), bounds.getMaxY());
            requestFocus();
            e.consume();
        }
    }


    private void handleMouseMoved(MouseEvent e) {

        if (!getScene().getWindow().isFocused()) {
            return;
        }

        String pathText = text().getText();
        if (pathText == null || pathText.isBlank()) {
            stopTimeline();
            return;
        }

        var point = new Point2D(e.getScreenX(), e.getScreenY());
        var attr = (Integer) text().queryAccessibleAttribute(OFFSET_AT_POINT, point);
        if (attr == null || attr >= pathText.length() - 1) {
            stopTimeline();
            return;
        }

        int index = Math.clamp(attr, 0, pathText.length() - 1);
        if (index != pathPositionIndex) {
            stopTimeline();
        }

        trackPathPoint(index, pathText, point);

    }


    private void trackPathPoint(int index, String pathText, Point2D point) {

        if (timeline != null) return;
        pathPositionIndex = index;
        timeline = new Timeline();
        timeline.setCycleCount(1);

        var keyFrame = new KeyFrame(Duration.millis(1000), e -> {
            if (popup != null) {
                popup.hide();
            }
            var addressPath = AddressPath.of(Path.of(pathText));
            popup = UiPopupMenu.of(uiColor(), addressPath.listSibling(index), handlePathSelect());
            popup.setOnHidden(we -> {
                text().requestFocus();
                text().positionCaret(index);
            });
            popup.show(UiAddressField.this.getScene().getWindow(),
                point.getX(), text().localToScreen(text().getBoundsInLocal()).getMaxY());
            requestFocus();
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }


    private Consumer<Path> handlePathSelect() {
        return path -> {
            if (pathSelectConsumer == null) return;
            if (Files.isDirectory(path)) {
                var anchorX = popup.getAnchorX();
                popup.hide();
                popup = UiPopupMenu.of(uiColor(), AddressPath.of(path).listSibling(), handlePathSelect());
                popup.show(UiAddressField.this.getScene().getWindow(), anchorX,
                    text().localToScreen(text().getBoundsInLocal()).getMaxY());
                requestFocus();
            } else if (Files.isRegularFile(path)) {
                pathSelectConsumer.accept(path);
            }
        };
    }


    void onPathSelected(Consumer<Path> consumer) {
        pathSelectConsumer = consumer;
    }

    private void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
        pathPositionIndex = -1;
    }


    private static String extension(String string) {
        int index = string.lastIndexOf(".") + 1;
        return (index > 0 && index < string.length()) ? string.substring(index) : "";
    }

}
