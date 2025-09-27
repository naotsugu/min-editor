/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.ui.fx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * A custom text field for entering and managing integer values within a defined range.
 * It extends the JavaFX {@link TextField} and provides functionality to restrict user input
 * to numeric characters only and enforce minimum and maximum value constraints.
 * <p>
 * The {@code IntegerField} allows binding of an integer property to enable reactive updates
 * between the field's internal value and external property bindings.
 * <p>
 * Key Features:
 * - Only allows numeric input (0-9).
 * - Ensures the value is always within a specified range.
 * - Provides property listeners for value changes and updates to the field text.
 *
 * @author Naotsugu Kobayashi
 */
public class IntegerField extends TextField {

    private final IntegerProperty value;
    private final int minValue;
    private final int maxValue;

    /**
     * Creates an instance of the IntegerField class with default minimum value, maximum value,
     * and initial value. The default range is set between 0 (inclusive) and 100 (inclusive),
     * with an initial value of 0.
     * <p>
     * The IntegerField is a specialized TextField designed to only accept and manage
     * integer values within a specified range, ensuring data validity. When this constructor
     * is used, the instance is pre-configured with these default values.
     */
    public IntegerField() {
        this(0, 100, 0);
    }

    /**
     * Constructs an IntegerField instance with specified minimum value, maximum value,
     * and initial value. The IntegerField is a specialized TextField designed to manage
     * and restrict input to integer values within a specified range.
     * <p>
     * The provided minimum value must not be greater than the maximum value. The initial
     * value must fall within the range defined by the minimum and maximum values.
     *
     * @param minValue the minimum allowable value for the field
     * @param maxValue the maximum allowable value for the field
     * @param initialValue the initial value to be set in the field, which must be within the defined range
     * @throws IllegalArgumentException if the minimum value is greater than the maximum value
     * @throws IllegalArgumentException if the initial value is not within the defined range
     */
    public IntegerField(int minValue, int maxValue, int initialValue) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException("min value %d greater than max value %d".formatted(minValue, maxValue));
        }
        if (!((minValue <= initialValue) && (initialValue <= maxValue))) {
            throw new IllegalArgumentException("initialValue %d not between %d  and %d".formatted(initialValue, minValue, maxValue));
        }

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = new SimpleIntegerProperty(initialValue);
        setText(String.valueOf(initialValue));

        addEventFilter(KeyEvent.KEY_TYPED, this::keyEventFilter);
        textProperty().addListener(this::handleTextChanged);
        value.addListener(this::handleValueChanged);
    }

    private void keyEventFilter(KeyEvent keyEvent) {
        if (!"0123456789".contains(keyEvent.getCharacter())) {
            keyEvent.consume();
        }
    }

    private void handleTextChanged(ObservableValue<? extends String> ob, String ov, String nv) {
        if (nv == null || nv.isEmpty()) {
            value.setValue(0);
            return;
        }

        final int intValue = Integer.parseInt(nv);
        if (minValue > intValue || intValue > maxValue) {
            textProperty().setValue(ov);
        }

        value.set(Integer.parseInt(textProperty().get()));
    }

    private void handleValueChanged(ObservableValue<? extends Number> ob, Number ov, Number nv) {
        if (nv == null) {
            setText("");
            return;
        }

        if (nv.intValue() < minValue) {
            value.setValue(minValue);
            return;
        }

        if (nv.intValue() > maxValue) {
            value.setValue(maxValue);
            return;
        }

        if (nv.intValue() == 0 && (textProperty().get() == null || textProperty().get().isEmpty())) {
            // no action required, text property is already blank, we don't need to set it to 0.
        } else {
            setText(nv.toString());
        }
    }

    /**
     * Get the current value of the IntegerField.
     * @return the current value
     */
    public int  getValue() {
        return value.getValue();
    }

    /**
     * Set the value of the IntegerField.
     * @param newValue the new value to set
     */
    public void setValue(int newValue) {
        value.setValue(newValue);
    }

    /**
     * Get the property that represents the current value of the IntegerField.
     * @return the property representing the current value
     */
    public IntegerProperty valueProperty() {
        return value;
    }

}
