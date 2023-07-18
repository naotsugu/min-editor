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
package com.mammb.code.editor2.ui.pane.impl;

import com.mammb.code.editor2.ui.pane.Selection;

/**
 * SelectionImpl.
 * @author Naotsugu Kobayashi
 */
public class SelectionImpl implements Selection {

    private int start;
    private int end;
    private boolean dragging;

    public SelectionImpl(int start) {
        this.start = start;
        this.end = start;
    }

    @Override
    public int start() {
        return start;
    }

    @Override
    public int end() {
        return end;
    }

    @Override
    public void to(int toOffset) {
        end = toOffset;
    }

    public boolean dragging() {
        return dragging;
    }
}
