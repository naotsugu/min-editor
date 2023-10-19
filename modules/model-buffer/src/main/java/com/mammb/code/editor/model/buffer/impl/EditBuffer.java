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

import com.mammb.code.editor.model.buffer.Content;
import com.mammb.code.editor.model.buffer.Metrics;
import com.mammb.code.editor.model.buffer.TextBuffer;
import com.mammb.code.editor.model.buffer.Traverse;
import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.edit.EditListener;
import com.mammb.code.editor.model.edit.EditQueue;
import com.mammb.code.editor.model.edit.EditTo;
import com.mammb.code.editor.model.edit.EditToListener;
import com.mammb.code.editor.model.slice.TextualSlice;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.model.until.Until;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EditBuffer.
 * @author Naotsugu Kobayashi
 */
public class EditBuffer implements TextBuffer<Textual> {

    /** The peer slice. */
    private final TextualSlice<Textual> slice;

    /** The content. */
    private final Content content;

    /** The edit queue. */
    private final EditQueue editQueue;

    /** The metrics. */
    private final MetricsImpl metrics;


    /**
     * Constructor.
     * @param path the path of content
     * @param maxRowSize the row size of slice
     * @param loadingTraverse the loading traverse
     */
    public EditBuffer(Path path, int maxRowSize, Traverse loadingTraverse) {
        this.metrics = new MetricsImpl(path);
        this.content = Content.of(path, bytes -> {
                metrics.accept(bytes);
                if (loadingTraverse != null) loadingTraverse.accept(bytes);
        });
        this.slice = TextualSlice.of(maxRowSize, new RawAdapter(content));
        this.editQueue = EditQueue.of(editTo(content));
        metrics.setModified(false);
        metrics.setCharset(content.charset());
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
    public int pageSize() {
        return slice.pageSize();
    }

    @Override
    public void setPageSize(int pageSize) {
        slice.setPageSize(pageSize);
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
    public boolean move(int rowDelta) {
        editQueue.flush();
        return slice.move(rowDelta);
    }


    @Override
    public void push(Edit edit) {
        editQueue.push(edit);
        metrics.apply(edit);
    }

    @Override
    public void flush() {
        editQueue.flush();
    }

    @Override
    public Edit undo() {
        Edit edit = editQueue.undo();
        metrics.setModified(editQueue.hasUndo());
        return edit;
    }

    @Override
    public Edit redo() {
        return editQueue.redo();
    }

    @Override
    public String subText(OffsetPoint point, int length) {
        editQueue.flush();
        byte[] bytes = content.bytes(point.cpOffset(), Until.charLen(length));
        return new String(bytes, content.charset());
    }

    @Override
    public void save() {
        editQueue.flush();
        content.save();
        metrics.setModified(false);
    }

    @Override
    public void saveAs(Path path) {
        editQueue.flush();
        content.saveAs(path);
        metrics.setPath(path);
        metrics.setModified(false);
    }

    @Override
    public boolean readOnly() {
        return false;
    }

    @Override
    public Metrics metrics() {
        return metrics;
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
