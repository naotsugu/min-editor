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
package com.mammb.code.editor2.model.edit;

import com.mammb.code.editor2.model.core.PointText;

/**
 * Mono Edit.
 * @author Naotsugu Kobayashi
 */
public class MonoEdit implements Edit {

    /** The type of edit. */
    private final EditType type;

    /** The offset point of edit. */
    private final PointText pointText;

    /** The occurred on. */
    private final long occurredOn;


    /**
     * Constructor.
     * @param type the type of edit
     */
    MonoEdit(EditType type, PointText pointText, long occurredOn) {
        this.type = type;
        this.pointText = pointText;
        this.occurredOn = occurredOn;
    }


    @Override
    public EditType type() {
        return type;
    }


    @Override
    public long occurredOn() {
        return occurredOn;
    }

}
