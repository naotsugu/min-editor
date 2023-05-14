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

import com.mammb.code.editor2.model.buffer.ContentAdapter;
import com.mammb.code.editor2.model.core.PointText;
import com.mammb.code.editor2.model.edit.EditQueue;
import com.mammb.code.editor2.model.text.RowSlice;

import java.util.ArrayList;
import java.util.List;

/**
 * TextBuffer.
 * @author Naotsugu Kobayashi
 */
public class EditBuffer implements com.mammb.code.editor2.model.buffer.TextBuffer, RowSlice {

    private final RowSlice slice;
    private final Content content;
    private final EditQueue editQueue = EditQueue.of();

    public EditBuffer(Content content, int maxRowSize) {
        this.content = content;
        this.slice = RowSlice.of(maxRowSize, new ContentAdapter(content));
    }

    @Override
    public List<PointText> texts() {
        if (editQueue.isEmpty()) {
            return slice.texts();
        }

        List<PointText> ret = new ArrayList<>();
        // TODO
        return ret;
    }
}
