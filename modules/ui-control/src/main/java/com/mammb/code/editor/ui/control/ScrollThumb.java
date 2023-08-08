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
package com.mammb.code.editor.ui.control;

import javafx.scene.AccessibleRole;
import javafx.scene.shape.Rectangle;

/**
 * ScrollThumb.
 * @author Naotsugu Kobayashi
 */
public class ScrollThumb extends Rectangle {

    /**
     * Constructor.
     * @param width the width of thumb
     * @param height the height of thumb
     */
    public ScrollThumb(double width, double height) {

        setManaged(false);
        setWidth(width);
        setHeight(height);
        setY(0);
        setArcHeight(8);
        setArcWidth(8);
        setAccessibleRole(AccessibleRole.THUMB);

        initListener();

    }

    /**
     * Initialize listener.
     */
    private void initListener() {
    }


    /**
     * Create a thumb for row scroll bar.
     * @param width the width of thumb
     * @return a new thumb
     */
    public static ScrollThumb rowOf(double width) {
        var thumb = new ScrollThumb(width, 10);
        return thumb;
    }

    /**
     * Create a thumb for col scroll bar.
     * @param height the height of thumb
     * @return a new thumb
     */
    public static ScrollThumb colOf(double height) {
        var thumb = new ScrollThumb(10, height);
        return thumb;
    }
}
