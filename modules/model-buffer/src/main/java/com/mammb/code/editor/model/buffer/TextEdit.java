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
package com.mammb.code.editor.model.buffer;

import com.mammb.code.editor.model.buffer.impl.EditBuffer;
import com.mammb.code.editor.model.buffer.impl.TextualArray;
import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.text.OffsetPoint;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.model.text.TextualScroll;

import java.nio.file.Path;

/**
 * The text edit.
 * @author Naotsugu Kobayashi
 */
public interface TextEdit {

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
     * Save.
     */
    void save();

    /**
     * Save as.
     * @param path the path to be saved
     */
    void saveAs(Path path);

    /**
     * Get whether this buffer is read-only or not.
     * @return {@code true}, if this buffer is read-only
     */
    boolean readOnly();

    /**
     * Create a view.
     * @param maxRowSize the number of row
     * @return a view
     */
    TextualScroll<? extends Textual> createView(int maxRowSize);

    /**
     * Get the sub text.
     * @param point the start point
     * @param length the char length to be gets
     * @return the sub text
     */
    String subText(OffsetPoint point, int length);

    /**
     * Get the metrics.
     * @return the metrics
     */
    Metrics metrics();

    /**
     * Create a new text buffer.
     * @param path the path of content
     * @return a new text buffer
     */
    static TextEdit editBuffer(Path path) {
        return new EditBuffer(path, null);
    }

    /**
     * Create a new fixed text buffer.
     * @param path the path of content
     * @return a new fixed text buffer
     */
    static TextEdit fixed(Path path) {
        return new TextualArray(path);
    }

}
