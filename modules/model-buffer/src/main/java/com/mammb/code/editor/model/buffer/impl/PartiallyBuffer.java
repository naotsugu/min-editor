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
package com.mammb.code.editor.model.buffer.impl;

import com.mammb.code.editor.model.buffer.Metrics;
import com.mammb.code.editor.model.buffer.TextBuffer;
import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * PartiallyBuffer.
 * @author Naotsugu Kobayashi
 */
public class PartiallyBuffer implements TextBuffer<Textual> {

    private final List<Textual> list = new ArrayList<>();

    private int rowIndex = 0;

    private int maxRowSize = 10;

    private Metrics metrics;


    public PartiallyBuffer(Path path, int maxRowSize) {
        this.maxRowSize = maxRowSize;
    }

    @Override
    public List<Textual> texts() {
        return list.subList(rowIndex, Math.min(rowIndex + maxRowSize, list.size()));
    }

    @Override
    public int maxLineSize() {
        return maxRowSize;
    }

    @Override
    public void setMaxLineSize(int maxSize) {
        maxRowSize = maxSize;
    }

    @Override
    public void push(Edit edit) {
        // do nothing
    }

    @Override
    public void flush() {
        // do nothing
    }

    @Override
    public Edit undo() {
        return Edit.empty;
    }

    @Override
    public Edit redo() {
        return Edit.empty;
    }

    @Override
    public List<Textual> prev(int n) {
        n = (rowIndex - n) >= 0 ? n : n - rowIndex;
        List<Textual> delta = list.subList(rowIndex - n, rowIndex);
        rowIndex -= n;
        return delta;
    }

    @Override
    public List<Textual> next(int n) {
        n = (rowIndex + n) < list.size() ? n : list.size() - rowIndex + n;
        List<Textual> delta = list.subList(
            Math.min(rowIndex + maxRowSize + 1, list.size()),
            Math.min(rowIndex + maxRowSize + 1 + n, list.size()));
        rowIndex += n;
        return delta;
    }

    @Override
    public Textual subText(OffsetPoint point, int length) {
        return null;
    }

    @Override
    public void save() {
        // do nothing
    }

    @Override
    public void saveAs(Path path) {
        // do nothing
    }

    @Override
    public boolean readOnly() {
        return true;
    }

    @Override
    public Metrics metrics() {
        return metrics;
    }

}
