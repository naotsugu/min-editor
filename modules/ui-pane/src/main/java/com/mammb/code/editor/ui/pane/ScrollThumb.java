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
package com.mammb.code.editor.ui.pane;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * ScrollThumb.
 * @author Naotsugu Kobayashi
 */
public class ScrollThumb extends Rectangle {

    /** The base color. */
    private final Color baseColor;


    /**
     * Constructor.
     * @param width the width of thumb
     * @param height the height of thumb
     * @param baseColor the base color
     */
    public ScrollThumb(double width, double height, Color baseColor) {
        this.baseColor = baseColor;
        setManaged(false);
        setWidth(width);
        setHeight(height);
        setY(0);
        setArcHeight(8);
        setArcWidth(8);
        setFill(baseColor.deriveColor(0, 1, 1, 0.3));

        initListener();

    }

    /**
     * Initialize listener.
     */
    private void initListener() {
        if (!System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            setOnMouseEntered(this::handleMouseEntered);
            setOnMouseExited(this::handleMouseExited);
        }
    }

    private void handleMouseEntered(MouseEvent event) {
        setFill(baseColor.deriveColor(0, 1, 1, 0.6));
    }

    private void handleMouseExited(MouseEvent event) {
        setFill(baseColor.deriveColor(0, 1, 1, 0.3));
    }

}
