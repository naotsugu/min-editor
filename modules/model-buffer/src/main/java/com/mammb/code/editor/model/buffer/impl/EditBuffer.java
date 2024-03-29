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

import com.mammb.code.editor.model.buffer.CharacterCodeException;
import com.mammb.code.editor.model.buffer.Metrics;
import com.mammb.code.editor.model.buffer.TextEdit;
import com.mammb.code.editor.model.content.Content;
import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.edit.EditListener;
import com.mammb.code.editor.model.edit.EditQueue;
import com.mammb.code.editor.model.edit.EditTo;
import com.mammb.code.editor.model.edit.EditToListener;
import com.mammb.code.editor.model.slice.RowSupplier;
import com.mammb.code.editor.model.slice.TextualSlice;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.model.until.Until;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * EditBuffer.
 * @author Naotsugu Kobayashi
 */
public class EditBuffer implements TextEdit {

    /** logger. */
    private static final System.Logger log = System.getLogger(EditBuffer.class.getName());

    /** The content. */
    private final Content content;

    /** The edit queue. */
    private final EditQueue editQueue;

    /** The metrics. */
    private final MetricsImpl metrics;

    /** The slice views. */
    private final List<TextualSlice<Textual>> views = new ArrayList<>();


    /**
     * Constructor.
     * @param path the path of content
     */
    public EditBuffer(Path path) {

        var metrics = new MetricsImpl(path);
        metrics.addInvalidListener((o, n) -> {
            log.log(System.Logger.Level.ERROR, n);
            throw new CharacterCodeException();
        });

        this.content = Content.of(path, metrics);
        this.editQueue = EditQueue.of(editTo(content, views));

        this.metrics = metrics;
        metrics.setModified(false);
        metrics.setCharset(content.charset());

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
        metrics.apply(edit);
        metrics.setModified(editQueue.hasUndo());
        return edit;
    }


    @Override
    public Edit redo() {
        Edit edit = editQueue.redo();
        metrics.apply(edit);
        return edit;
    }


    @Override
    public String subText(OffsetPoint point, int length) {
        editQueue.flush();
        byte[] bytes = content.bytes(point.cpOffset(), Until.charLen(length));
        return new String(bytes, StandardCharsets.UTF_8);
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


    @Override
    public SliceView createView(int maxRowSize) {
        var view = new SliceView(
            TextualSlice.of(maxRowSize, new RowAdapter(content)),
            editQueue);
        views.add(view);
        return view;
    }


    @Override
    public RowSupplier rowSupplier() {
        return new RowSupplier() {
            private final RowSupplier peer = new RowAdapter(content);
            @Override public String at(long cpOffset) {
                editQueue.flush();
                return peer.at(cpOffset);
            }
            @Override public String before(long cpOffset) {
                editQueue.flush();
                return peer.before(cpOffset);
            }
            @Override public long offset(long startCpOffset, Until<byte[]> until) {
                editQueue.flush();
                return peer.offset(startCpOffset, until);
            }
            @Override public long offsetBefore(long startCpOffset, Until<byte[]> until) {
                editQueue.flush();
                return peer.offsetBefore(startCpOffset, until);
            }
            @Override public long cpLength() {
                return peer.cpLength();
            }
        };
    }


    private static EditListener editTo(Content content, List<TextualSlice<Textual>> views) {
        return new EditToListener(new EditTo() {
            @Override
            public void insert(OffsetPoint point, String text) {
                content.insert(point, text);
                views.forEach(s -> s.refresh(point.row()));
            }
            @Override
            public void delete(OffsetPoint point, String text) {
                content.delete(point, text.length());
                views.forEach(s -> s.refresh(point.row()));
            }
        });
    }

}
