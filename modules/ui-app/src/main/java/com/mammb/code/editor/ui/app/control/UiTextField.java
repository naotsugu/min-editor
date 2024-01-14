/*
 * Copyright 2023-2024 the original author or authors.
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

import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.text.HitInfo;

import static javafx.scene.AccessibleAttribute.OFFSET_AT_POINT;

/**
 * The InputText.
 * @author Naotsugu Kobayashi
 */
public class UiTextField extends TextField {

    /** caret locked. */
    private volatile boolean caretLocked = false;


    /**
     * Constructor.
     */
    public UiTextField() {
        focusedProperty().addListener((ob, o, n) -> {
            caretLocked = true;
        });
    }


    public void caretFlashLock() {
        caretLocked = true;
    }


    @Override
    public void selectRange(int anchor, int caretPosition) {
        if (caretLocked) {
            // prevents caret from moving to the beginning of the line when focus out
            caretLocked = false;
        } else {
            super.selectRange(anchor, caretPosition);
        }
    }


    /**
     * Get the text offset at point.
     * @param point the point 2D
     * @return the text offset at point, {@code -1} if out of range
     */
    public int getOffsetAtPoint(Point2D point) {
        var offset = (Integer) queryAccessibleAttribute(OFFSET_AT_POINT, point);
        return (offset == null || offset >= (getText().length() - 1)) ? -1 : offset;
    }


    /**
     * Get the visible text start index.
     * @return the visible text start index
     */
    public int getVisibleTextStartIndex() {
        TextFieldSkin textFieldSkin = (TextFieldSkin) getSkin();
        HitInfo hit = textFieldSkin.getIndex(getPadding().getLeft(), 0);
        return hit.getCharIndex();
    }

}
