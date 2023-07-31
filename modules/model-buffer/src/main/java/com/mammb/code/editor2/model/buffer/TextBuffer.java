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
package com.mammb.code.editor2.model.buffer;

import com.mammb.code.editor2.model.buffer.impl.EditBuffer;
import com.mammb.code.editor2.model.buffer.impl.PtContentMirror;
import com.mammb.code.editor2.model.edit.Edit;
import com.mammb.code.editor2.model.text.OffsetPoint;
import com.mammb.code.editor2.model.text.TextList;
import com.mammb.code.editor2.model.text.Textual;

import java.nio.file.Path;
import java.util.List;

/**
 * The edit buffer.
 * @author Naotsugu Kobayashi
 */
public interface TextBuffer<T extends Textual> extends TextList<T> {

    @Override
    List<T> texts();

    /**
     * Get the capacity of lines.
     * @return the capacity of lines
     */
    int maxLineSize();

    /**
     * Set the capacity of lines.
     * @param maxSize the capacity of lines
     */
    void setMaxLineSize(int maxSize);

    /**
     * Push the edit.
     * @param edit the edit
     */
    void push(Edit edit);

    /**
     * Flush.
     */
    void flush();

    /**
     * Undo.
     * @return the undone edit.
     */
    Edit undo();

    /**
     * Redo.
     * @return the redone edit.
     */
    Edit redo();

    /**
     * Scroll previous line.
     * @param n the number of line
     * @return the added lines
     */
    List<T> prev(int n);

    /**
     * Scroll next line.
     * @param n the number of line
     * @return the added lines
     */
    List<T> next(int n);

    /**
     * Get the sub text.
     * @param point the start point
     * @param length the char length to be gets
     * @return the sub text
     */
    Textual subText(OffsetPoint point, int length);

    /**
     * Save.
     */
    void save();

    /**
     * Save as.
     * @param path the path to be saved
     */
    void saveAs(Path path);

    /**
     * Get the metrics.
     * @return the metrics
     */
    Metrics metrics();

    /**
     * Create a new text buffer.
     * @param maxRowSize the max row size
     * @param path the path of content
     * @return a new text buffer
     */
    static TextBuffer<Textual> editBuffer(int maxRowSize, Path path) {
        return new EditBuffer(PtContentMirror.of(path), maxRowSize);
    }

}
