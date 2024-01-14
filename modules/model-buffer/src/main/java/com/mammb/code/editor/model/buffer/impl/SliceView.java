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
package com.mammb.code.editor.model.buffer.impl;

import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.edit.EditQueue;
import com.mammb.code.editor.model.slice.TextualSlice;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SliceView.
 * @author Naotsugu Kobayashi
 */
public class SliceView implements TextualSlice<Textual> {

    /** The slice. */
    private final TextualSlice<Textual> slice;

    /** The edit queue ref. */
    private final EditQueue editQueueRef;


    /**
     * Constructor.
     * @param slice the slice
     * @param editQueue the edit queue ref
     */
    public SliceView(
            TextualSlice<Textual> slice,
            EditQueue editQueue) {
        this.slice = slice;
        this.editQueueRef = editQueue;
    }


    @Override
    public List<Textual> texts() {

        Edit edit = editQueueRef.peek();
        if (edit == null) {
            return slice.texts();
        }

        return slice.texts().stream()
            .map(edit::applyTo)
            .collect(Collectors.toList());
    }


    @Override
    public int pageSize() {
        return slice.pageSize();
    }


    @Override
    public void setPageSize(int pageSize) {
        slice.setPageSize(pageSize);
    }


    @Override
    public List<Textual> prev(int n) {
        editQueueRef.flush();
        return slice.prev(n);
    }


    @Override
    public List<Textual> next(int n) {
        editQueueRef.flush();
        return slice.next(n);
    }


    @Override
    public boolean move(OffsetPoint base, int rowDelta) {
        editQueueRef.flush();
        return slice.move(base, rowDelta);
    }


    @Override
    public void refresh(int rowNumber) {
        slice.refresh(rowNumber);
    }

}
