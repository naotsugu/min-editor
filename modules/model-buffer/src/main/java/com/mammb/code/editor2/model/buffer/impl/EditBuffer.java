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

import com.mammb.code.editor2.model.buffer.Scroll;
import com.mammb.code.editor2.model.buffer.TextBuffer;
import com.mammb.code.editor2.model.text.Textual;
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.edit.EditQueue;
import com.mammb.code.editor2.model.slice.Slice;
import com.mammb.code.editor2.model.buffer.Content;
import java.util.List;
import static java.util.function.Predicate.not;

/**
 * EditBuffer.
 * @author Naotsugu Kobayashi
 */
public class EditBuffer implements TextBuffer<Textual> {

    /** The pear slice. */
    private final Slice<Textual> slice;

    /** The content. */
    private final Content content;

    /** The scroll. */
    private final Scroll scroll;

    /** The edit queue. */
    private final EditQueue editQueue = EditQueue.of();


    /**
     * Constructor.
     * @param content the content
     * @param maxRowSize the row size of slice
     */
    public EditBuffer(Content content, int maxRowSize) {
        this.content = content;
        this.slice = Slice.of(maxRowSize, new RawAdapter(content));
        scroll = ScrollSlice.of(slice);
    }


    @Override
    public List<Textual> texts() {

        if (editQueue.isEmpty()) {
            return slice.texts();
        }

        if (editQueue.size() > 1 || editQueue.stream().anyMatch(not(Edit::isSingleEdit))) {
            // if the edit queue contains multiline deleted edits,
            // the retrieved text may be insufficient.
            editQueue.flush();
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
    public Scroll scroll() {
        return scroll;
    }

}
