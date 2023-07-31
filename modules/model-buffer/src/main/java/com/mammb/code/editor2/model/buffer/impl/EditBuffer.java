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
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.edit.EditListener;
import com.mammb.code.editor2.model.edit.EditQueue;
import com.mammb.code.editor2.model.edit.EditTo;
import com.mammb.code.editor2.model.edit.EditToListener;
import com.mammb.code.editor2.model.slice.Slice;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.Textual;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

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

        Edit edit = editQueue.peek();
        return slice.texts().stream()
            .map(edit::applyTo)
            .collect(Collectors.toList());
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
    public void flush() {
        editQueue.flush();
    }

    @Override
    public Edit undo() {
        return editQueue.undo();
    }

    @Override
    public Edit redo() {
        return editQueue.redo();
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
    public Textual subText(OffsetPoint point, int length) {
        editQueue.flush();
        byte[] bytes = content.bytes(point.cpOffset(), Until.charLength(length));
        return Textual.of(point, new String(bytes, content.charset()));
    }

    @Override
    public ContentMetrics metrics(ContentMetrics metrics) {
        editQueue.flush();
        content.traverseRow(bytes -> metrics.add(new String(bytes, content.charset())));
        return metrics;
    }

    @Override
    public void save() {
        editQueue.flush();
        content.save();
    }

    @Override
    public void saveAs(Path path) {
        editQueue.flush();
        content.saveAs(path);
    }

    private EditListener editTo(Content content) {
        return new EditToListener(new EditTo() {
            @Override
            public void insert(OffsetPoint point, String text) {
                content.insert(point, text);
                slice.refresh(point.row());
            }
            @Override
            public void delete(OffsetPoint point, String text) {
                content.delete(point, text.length());
                slice.refresh(point.row());
            }
        });
    }

}
