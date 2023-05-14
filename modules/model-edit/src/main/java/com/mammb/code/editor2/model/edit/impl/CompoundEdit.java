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
package com.mammb.code.editor2.model.edit.impl;

import com.mammb.code.editor2.model.core.OffsetPoint;
import com.mammb.code.editor2.model.core.PointText;
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.edit.EditType;

/**
 * CompoundEdit.
 * @author Naotsugu Kobayashi
 */
public class CompoundEdit implements Edit {

    /** The offset point of edit. */
    private final PointText pointText;

    /** The occurred on. */
    private final long occurredOn;


    /**
     * Constructor,
     * @param pointText the offset point of edit
     * @param occurredOn the occurred on
     */
    public CompoundEdit(PointText pointText, long occurredOn) {
        this.pointText = pointText;
        this.occurredOn = occurredOn;
    }


    @Override
    public EditType type() {
        return null;
    }


    @Override
    public long occurredOn() {
        return occurredOn;
    }


    @Override
    public boolean canMerge(Edit other) {
        return false;
    }


    @Override
    public Edit merge(Edit other) {
        throw new UnsupportedOperationException();
    }


    @Override
    public OffsetPoint point() {
        return pointText.point();
    }


    @Override
    public String text() {
        return pointText.text();
    }

}
