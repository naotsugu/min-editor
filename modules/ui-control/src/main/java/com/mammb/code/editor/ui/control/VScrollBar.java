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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.AccessibleRole;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Vertical ScrollBar.
 * @author Naotsugu Kobayashi
 */
public class VScrollBar extends StackPane implements ScrollBar {

    /** The min value of scroll bar. */
    private final IntegerProperty min = new SimpleIntegerProperty(0);

    /** The max value of scroll bar. */
    private final IntegerProperty max = new SimpleIntegerProperty(100);

    /** The value of scroll bar. */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /** The thumb. */
    private final ScrollThumb thumb = ScrollThumb.rowOf(WIDTH);


    /**
     * Constructor.
     */
    public VScrollBar(Color baseColor) {

        final Color backGround = baseColor.deriveColor(0, 1, 1, 0.05);

        setWidth(WIDTH);
        setPrefWidth(WIDTH);
        setMaxWidth(WIDTH);
        setBackground(new Background(new BackgroundFill(backGround, null, null)));
        setAccessibleRole(AccessibleRole.SCROLL_BAR);

        getChildren().add(thumb);

        initListener();
    }

    /**
     * Initialize listener.
     */
    private void initListener() {
    }

}
