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
package com.mammb.code.editor.ui.model.impl;

import com.mammb.code.editor.model.text.OffsetPoint;

/**
 * CaretSelection.
 * @author Naotsugu Kobayashi
 */
public class CaretSelection implements SelectionDrawTrait {

    /** The selection open offset. */
    private final OffsetPoint start;

    /** The selection close offset caret. */
    private final CaretLine caretLine;


    /**
     * Constructor.
     * @param caretLine the caret
     */
    public CaretSelection(CaretLine caretLine) {
        if (caretLine == null) {
            this.start = null;
            this.caretLine = null;
        } else {
            this.start = caretLine.offsetPoint();
            this.caretLine = caretLine;
        }
    }


    public boolean isInvalid() {
        return start == null || caretLine.getLine() == null;
    }


    @Override
    public OffsetPoint min() {
        if (isInvalid()) {
            return null;
        }
        return (start.offset() <= caretLine.getBar().offset())
            ? start
            : caretLine.offsetPoint();
    }


    @Override
    public OffsetPoint max() {
        if (isInvalid()) {
            return null;
        }
        return (start.offset() <= caretLine.getBar().offset())
            ? caretLine.offsetPoint()
            : start;
    }

}
