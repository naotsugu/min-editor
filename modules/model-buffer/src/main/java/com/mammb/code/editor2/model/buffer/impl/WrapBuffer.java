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
import com.mammb.code.editor2.model.buffer.SliceBuffer;
import com.mammb.code.editor2.model.core.Translate;

import java.util.ArrayList;
import java.util.List;

/**
 * WrapBuffer.
 * @author Naotsugu Kobayashi
 */
public class WrapBuffer implements SliceBuffer {

    /** The EditBuffer. */
    private final SliceBuffer sliceBuffer;

    private int lineOffset = 0;

    /** The wrap width for the layout. */
    private float wrapWidth = 0;

    /** The text rows. */
    private List<PointText> rows = new ArrayList<>();

    /** The text lines(text wrapped). */
    private List<PointText> lines = new ArrayList<>();

    /** The wrap strategy. */
    private final Translate<List<PointText>, List<PointText>> wrapStrategy;


    public WrapBuffer(SliceBuffer sliceBuffer, float wrapWidth,
                Translate<List<PointText>, List<PointText>> wrapStrategy) {
        this.sliceBuffer = sliceBuffer;
        this.wrapWidth = wrapWidth;
        this.wrapStrategy = wrapStrategy;
    }


    @Override
    public List<PointText> texts() {
        if (wrapWidth > 0) {
            return null;
        } else {
            return rows;
        }
    }


    private void pull() {
        rows = sliceBuffer.texts();

    }


    public void setWrapWidth(float wrapWidth) {
        this.wrapWidth = wrapWidth;
        lineOffset = 0;
    }

}
