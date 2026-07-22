/*
 * Copyright 2026- the original author or authors.
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
package com.mammb.code.jfx.multitab.internal;

import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DropMarker extends Rectangle {

    public DropMarker() {
        setFill(Color.TRANSPARENT);
        setStroke(Color.DARKORANGE);
        setStrokeWidth(1.5);
        setManaged(false);
        setVisible(false);
    }

    public void show(Bounds bounds) {
        show(bounds, null);
    }

    public void show(Bounds bounds, Side side) {
        setX(bounds.getMinX());
        setY(bounds.getMinY());
        setWidth(bounds.getWidth());
        setHeight(bounds.getHeight());
        switch (side) {
            case TOP    -> top();
            case RIGHT  -> right();
            case BOTTOM -> bottom();
            case LEFT   -> left();
            case null   -> {}
        }
        setVisible(true);
    }

    public void clear() {
        setVisible(false);
    }

    public void top() {
        setHeight(getHeight() / 2);
    }

    public void right() {
        setWidth(getWidth() / 2);
        setX(getX() + getWidth());
    }

    public void bottom() {
        setHeight(getHeight() / 2);
        setY(getY() + getHeight());
    }

    public void left() {
        setWidth(getWidth() / 2);
    }

}
