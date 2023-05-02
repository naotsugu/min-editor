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
package com.mammb.code.editor.model;

import com.mammb.code.editor.lang.Strings;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * The edit queue.
 * @author Naotsugu Kobayashi
 */
public class EditQueue {

    /** The edit listener. */
    private final EditListener handler;

    /** The buffered edit. */
    private Edit buffer = Edit.empty;

    /** The undo queue. */
    private final Deque<Edit> undo = new ArrayDeque<>();

    /** The redo queue. */
    private final Deque<Edit> redo = new ArrayDeque<>();


    /**
     * Constructor.
     * @param handler the edit listener
     */
    public EditQueue(EditListener handler) {
        this.handler = handler;
    }


    /**
     * Push the edit.
     * @param edit the edit
     */
    public void push(Edit edit) {
        if (buffer.canMerge(edit)) {
            buffer = buffer.merge(edit);
            if (Strings.countLf(buffer.string()) > 0) {
                flush();
            }
        } else {
            flush();
            buffer = edit;
        }
    }


    /**
     * Get whether other edit can be merged into this edit.
     * @param other the merging edit
     * @return {@code true} if other edit can be merged into this edit.
     */
    public boolean canMerge(Edit other) {
        return buffer.canMerge(other);
    }


    /**
     * Peek undo.
     * @return the undone edit.
     */
    public Edit undoPeek() {
        Edit edit = undo.peek();
        return Objects.isNull(edit) ? Edit.empty : edit;
    }


    /**
     * Peek redo.
     * @return the redone edit.
     */
    public Edit redoPeek() {
        Edit edit = redo.peek();
        return Objects.isNull(edit) ? Edit.empty : edit;
    }


    /**
     * Undo.
     * @return the undone edit.
     */
    public Edit undo() {
        flush();
        if (undo.isEmpty()) return Edit.empty;
        Edit edit = undo.pop();
        handler.handle(edit);
        redo.push(edit.flip());
        return edit;
    }


    /**
     * Redo.
     * @return the redone edit.
     */
    public Edit redo() {
        flush();
        if (redo.isEmpty()) return Edit.empty;
        Edit edit = redo.pop();
        handler.handle(edit);
        undo.push(edit.flip());
        return edit;
    }


    /**
     * Get the pending code point count delta.
     * @return the pending code point count delta
     */
    public int pendingCodePointCountDelta() {
        return buffer.isDelete() ? -buffer.codePointCount()
             : buffer.isInsert() ? +buffer.codePointCount()
             : 0;
    }


    /**
     * Get the pending lf count delta.
     * @return the pending lf count delta
     */
    public int pendingLfCountDelta() {
        return buffer.isDelete() ? -Strings.countLf(buffer.string())
             : buffer.isInsert() ? +Strings.countLf(buffer.string())
             : 0;
    }


    /**
     * Clear edit queue.
     */
    public void clear() {
        flush();
        undo.clear();
        redo.clear();
    }


    /**
     * Get whether this edit is nil type.
     * @return {@code true}, if this edit is nil type
     */
    public boolean isBufferEmpty() {
        return buffer.isEmpty();
    }


    /**
     * Flush edit queue.
     */
    public void flush() {
        if (buffer.isEmpty()) {
            return;
        }
        handler.handle(buffer);
        undo.push(buffer.flip());
        redo.clear();
        buffer = Edit.empty;
    }

}
