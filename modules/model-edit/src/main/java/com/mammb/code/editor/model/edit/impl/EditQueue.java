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
package com.mammb.code.editor.model.edit.impl;

import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.edit.EditListener;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

/**
 * EditQueueImpl.
 * @author Naotsugu Kobayashi
 */
public class EditQueue implements com.mammb.code.editor.model.edit.EditQueue {

    /** The edit deque. */
    private final Deque<Edit> deque = new ArrayDeque<>();

    /** The undo queue. */
    private final Deque<Edit> undo = new ArrayDeque<>();

    /** The redo queue. */
    private final Deque<Edit> redo = new ArrayDeque<>();

    /** The edit listeners. */
    private final List<EditListener> listeners = new ArrayList<>();


    /**
     * Constructor.
     */
    public EditQueue() {
    }


    /**
     * Constructor.
     * @param listener the edit listener
     */
    public EditQueue(EditListener listener) {
        listeners.add(listener);
    }


    /**
     * Push the edit.
     * @param edit the edit
     */
    public void push(Edit edit) {
        Edit last = deque.peekLast();
        if (last != null && last.canMerge(edit)) {
            deque.removeLast();
            edit = last.merge(edit);
        }
        if (!deque.isEmpty()) {
            flush();
        }
        deque.push(edit);
        if (edit.acrossRows()) {
            flush();
        }
    }


    @Override
    public Edit peek() {
        return deque.peek();
    }


    @Override
    public void peekEach(Consumer<Edit> action) {
        deque.forEach(action);
    }


    @Override
    public void clear() {
        flush();
        undo.clear();
        redo.clear();
    }


    @Override
    public int size() {
        return deque.size();
    }


    @Override
    public boolean isEmpty() {
        return deque.isEmpty();
    }

    @Override
    public boolean hasUndo() {
        return !undo.isEmpty();
    }


    @Override
    public void flush() {
        while (!deque.isEmpty()) {
            Edit edit = deque.pop();
            if (edit == Edit.empty) continue;
            listeners.forEach(l -> l.handle(edit));
            undo.push(edit.flip());
            redo.clear();
        }
    }


    @Override
    public Edit undo() {
        flush();
        if (undo.isEmpty()) return Edit.empty;
        Edit edit = undo.pop();
        listeners.forEach(l -> l.handle(edit));
        redo.push(edit.flip());
        return edit;
    }


    @Override
    public Edit redo() {
        flush();
        if (redo.isEmpty()) return Edit.empty;
        Edit edit = redo.pop();
        listeners.forEach(l -> l.handle(edit));
        undo.push(edit.flip());
        return edit;
    }

}
