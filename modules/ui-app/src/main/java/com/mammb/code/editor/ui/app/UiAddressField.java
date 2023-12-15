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
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import java.nio.file.Path;

import static javafx.scene.AccessibleAttribute.OFFSET_AT_POINT;

/**
 * The UiAddressField.
 * @author Naotsugu Kobayashi
 */
public class UiAddressField extends UiPromptField {

    private Timeline timeline;

    private int pathPositionIndex = -1;

    /**
     * Constructor.
     *
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
    }




    private void handleTextProperty(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
        setPrompt(UiIcon.contentOf(uiColor(), extension(newValue)));
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
        if (attr == null) return;
        int index = Math.clamp(attr, 0, pathText.length() - 1);

        if (index != pathPositionIndex) {
            stopTimeline();
        }

        trackPathPoint(index, pathText);

    }


    private void trackPathPoint(int index, String pathText) {
        if (timeline != null) return;
        pathPositionIndex = index;
        timeline = new Timeline();
        timeline.setCycleCount(1);

        var kf = new KeyFrame(Duration.millis(1000), e -> {
            var p = AddressPath.of(Path.of(pathText));
            //var popup = new UiFileNavPopup(uiColor(), p.listSibling(index));
            //popup.show(UiAddressField.this.getScene().getWindow());
            var popup = UiPopupMenu.of(uiColor(), p.listSibling(index));
            popup.show(UiAddressField.this.getScene().getWindow());
        });
        timeline.getKeyFrames().add(kf);
        timeline.play();
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
