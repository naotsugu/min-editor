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
import com.mammb.code.editor2.model.core.Translate;
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.edit.EditQueue;
import com.mammb.code.editor2.model.text.RowSlice;
import com.mammb.code.editor2.model.buffer.Content;

import java.util.ArrayList;
import java.util.List;
import static java.util.function.Predicate.not;

/**
 * TextBuffer.
 * @author Naotsugu Kobayashi
 */
public class EditBuffer implements com.mammb.code.editor2.model.buffer.EditBuffer {

    /** The pear slice. */
    private final RowSlice slice;

    /** The content. */
    private final Content content;

    /** The PointText translate. */
    private final List<Translate<PointText, PointText>> translates = new ArrayList<>();

    /** The edit queue. */
    private final EditQueue editQueue = EditQueue.of();


    /**
     * Constructor.
     * @param content the content
     * @param maxRowSize the row size of slice
     */
    public EditBuffer(Content content, int maxRowSize) {
        this.content = content;
        this.slice = RowSlice.of(maxRowSize, new ContentAdapter(content));
    }


    @Override
    public List<PointText> texts() {

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


    private PointText applyTranslate(PointText text) {
        for (var translate : translates) {
            text = translate.applyTo(text);
        }
        return text;
    }

}
