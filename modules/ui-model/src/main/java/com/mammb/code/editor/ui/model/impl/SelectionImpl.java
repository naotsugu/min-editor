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
import com.mammb.code.editor.ui.model.Selection;

/**
 * SelectionImpl.
 * @author Naotsugu Kobayashi
 */
public class SelectionImpl implements Selection {

    /** The selection open offset. */
    private OffsetPoint start;

    /** The selection close offset. */
    private OffsetPoint end;

    /** The selection dragging. */
    private boolean dragging = false;

    @Override
    public void start(OffsetPoint offset) {
        start = end = offset;
        dragging = false;
    }

    @Override
    public void to(OffsetPoint toOffset) {
        end = toOffset;
    }

    @Override
    public void startDragging(OffsetPoint offset) {
        start = end = offset;
        dragging = true;
    }

    @Override
    public void endDragging() {
        dragging = false;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void clear() {
        start = end = null;
        dragging = false;
    }

    @Override
    public OffsetPoint startOffset() {
        return start;
    }

    @Override
    public OffsetPoint endOffset() {
        return end;
    }

    @Override
    public boolean started() {
        return start != null;
    }

}
