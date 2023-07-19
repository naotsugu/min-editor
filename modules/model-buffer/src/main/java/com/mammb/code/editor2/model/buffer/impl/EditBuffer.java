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

import com.mammb.code.editor2.model.buffer.Content;
import com.mammb.code.editor2.model.buffer.ContentMetrics;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.edit.*;
import com.mammb.code.editor2.model.slice.Slice;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;

import java.util.List;

/**
 * EditBuffer.
 * @author Naotsugu Kobayashi
 */
public class EditBuffer implements TextBuffer<Textual> {

    /** The pear slice. */
    private final Slice<Textual> slice;

    /** The content. */
    private final Content content;

    /** The edit queue. */
    private final EditQueue editQueue;


    /**
     * Constructor.
     * @param content the content
     * @param maxRowSize the row size of slice
     */
    public EditBuffer(Content content, int maxRowSize) {
        this.content = content;
        this.slice = Slice.of(maxRowSize, new RawAdapter(content));
        this.editQueue = EditQueue.of(editTo(content));
    }


    @Override
    public List<Textual> texts() {

        if (editQueue.isEmpty()) {
            return slice.texts();
        }

        Edit edit = editQueue.isEmpty()
                ? Edit.empty
                : editQueue.peek();

        return slice.texts().stream()
                .map(edit::applyTo)
                .toList();
    }

    @Override
    public int maxLineSize() {
        return slice.maxRowSize();
    }

    @Override
    public void setMaxLineSize(int maxSize) {
        slice.setMaxRowSize(maxSize);
    }

    @Override
    public void push(Edit edit) {
        editQueue.push(edit);
    }

    @Override
    public List<Textual> prev(int n) {
        editQueue.flush();
        return slice.prev(n);
    }

    @Override
    public List<Textual> next(int n) {
        editQueue.flush();
        return slice.next(n);
    }

    @Override
    public ContentMetrics metrics(ContentMetrics metrics) {
        content.traverseRow(bytes -> metrics.add(new String(bytes, content.charset())));
        return metrics;
    }

    private EditListener editTo(Content content) {
        return new EditToListener(new EditTo() {
            @Override
            public void insert(OffsetPoint point, CharSequence cs) {
                content.insert(point, cs);
                slice.refresh(point.row());
            }
            @Override
            public void delete(OffsetPoint point, int len) {
                content.delete(point, len);
                slice.refresh(point.row());
            }
        });
    }

}
