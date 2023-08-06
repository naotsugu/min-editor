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
import javafx.beans.value.ObservableValue;
import javafx.scene.AccessibleRole;
import javafx.scene.input.MouseEvent;
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

    /** The visible amount. */
    private final IntegerProperty visibleAmount = new SimpleIntegerProperty(100);

    /** The thumb. */
    private final ScrollThumb thumb = ScrollThumb.rowOf(WIDTH);

    private final Color backGround;


    /**
     * Constructor.
     */
    public VScrollBar(Color baseColor) {

        backGround = baseColor.deriveColor(0, 1, 1, 0.1);

        setWidth(WIDTH);
        setPrefWidth(WIDTH);
        setMaxWidth(WIDTH);
        setAccessibleRole(AccessibleRole.SCROLL_BAR);

        getChildren().add(thumb);

        initListener();
    }


    /**
     * Initialize listener.
     */
    private void initListener() {

        min.addListener(this::handleMinValueChanged);
        max.addListener(this::handleMaxValueChanged);
        value.addListener(this::handleValueChanged);
        visibleAmount.addListener(this::handleVisibleAmountChanged);

        setOnMouseEntered(this::handleMouseEntered);
        setOnMouseExited(this::handleMouseExited);
        setOnMouseClicked(this::handleTruckClicked);
    }


    private void handleMinValueChanged(ObservableValue<? extends Number> observable,
            Number oldValue, Number newValue) {

    }

    private void handleMaxValueChanged(ObservableValue<? extends Number> observable,
            Number oldValue, Number newValue) {

    }

    private void handleValueChanged(ObservableValue<? extends Number> observable,
            Number oldValue, Number newValue) {

    }

    private void handleVisibleAmountChanged(ObservableValue<? extends Number> observable,
            Number oldValue, Number newValue) {

    }

    /**
     * The truck clicked handler.
     * @param event the MouseEvent
     */
    private void handleTruckClicked(MouseEvent event) {
        if (event.isSynthesized()) {
            event.consume();
            return;
        }
    }

    private void handleMouseEntered(MouseEvent event) {
        setBackground(new Background(new BackgroundFill(backGround, null, null)));
    }

    private void handleMouseExited(MouseEvent event) {
        setBackground(null);
    }

}
