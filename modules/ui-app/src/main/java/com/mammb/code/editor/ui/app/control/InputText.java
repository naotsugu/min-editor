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

import javafx.scene.control.TextField;

/**
 * The InputText.
 * @author Naotsugu Kobayashi
 */
public class InputText extends TextField {

    /** caret locked. */
    private volatile boolean caretLocked = false;


    /**
     * Constructor.
     */
    public InputText() {
        focusedProperty().addListener((ob, o, n) -> caretLocked = !n);
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

}
