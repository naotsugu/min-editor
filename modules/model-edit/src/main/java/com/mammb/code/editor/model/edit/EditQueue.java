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
package com.mammb.code.editor.model.edit;

import java.util.function.Consumer;

/**
 * The edit queue.
 * @author Naotsugu Kobayashi
 */
public interface EditQueue {

    /**
     * Push the edit.
     * @param edit the edit
     */
    void push(Edit edit);

    /**
     * Peek the edit.
     * @return the head of the queue, or null if this queue is empty
     */
    Edit peek();

    /**
     * Actions are applied to elements in this queue on a first-in, first-out basis.
     * No elements are deleted by this operation.
     * @param action the action
     */
    void peekEach(Consumer<Edit> action);

    /**
     * Clear edit queue.
     */
    void clear();

    /**
     * Get the edit queue size.
     * @return the edit queue size
     */
    int size();

    /**
     * Get whether this edit queue is empty.
     * @return {@code true} if this edit queue is empty.
     */
    boolean isEmpty();

    /**
     * Get whether this edit queue has undo.
     * @return {@code true} if this edit queue has undo.
     */
    boolean hasUndo();

    /**
     * Flush edit queue.
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
     * Create the edit queue.
     * @return the edit queue.
     */
    static EditQueue of() {
        return new com.mammb.code.editor.model.edit.impl.EditQueue();
    }

    /**
     * Create the edit queue.
     * @param listener the edit listener
     * @return the edit queue.
     */
    static EditQueue of(EditListener listener) {
        return new com.mammb.code.editor.model.edit.impl.EditQueue(listener);
    }

}
