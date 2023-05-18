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
package com.mammb.code.editor2.model.buffer.impl;

import com.mammb.code.editor2.model.core.PointText;
import com.mammb.code.editor2.model.buffer.EditBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * TextWrapBuffer.
 * @author Naotsugu Kobayashi
 */
public class WrapBuffer implements EditBuffer {

    /** The EditBuffer. */
    private final EditBuffer editBuffer;

    private int lineOffset = 0;

    /** textWrap?. */
    private boolean textWrap = true;

    /** The text rows. */
    private List<PointText> rows = new ArrayList<>();

    /** The text lines(text wrapped). */
    private List<PointText> lines = new ArrayList<>();


    public WrapBuffer(EditBuffer editBuffer, boolean textWrap) {
        this.editBuffer = editBuffer;
        this.textWrap = textWrap;
    }


    @Override
    public List<PointText> texts() {
        if (textWrap) {
            return null;
        } else {
            return rows;
        }
    }


    private void pull() {
        rows = editBuffer.texts();

    }


    public void textWrap(boolean enable) {
        textWrap = enable;
        lineOffset = 0;
    }

}
