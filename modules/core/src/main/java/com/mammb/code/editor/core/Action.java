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
package com.mammb.code.editor.core;

/**
 * The Action.
 * @author Naotsugu Kobayashi
 */
public interface Action {

    /** The empty action. */
    Action EMPTY = Action.of(Type.EMPTY);

    /**
     * Get the action type.
     * @return the type
     */
    Type type();

    /**
     * Get the attribute.
     * @return the attribute
     */
    String attr();

    /**
     * Get occurred at.
     * @return occurred at
     */
    long occurredAt();

    /** The action event type.*/
    enum Type {
        TYPED, DELETE, BACK_SPACE,
        INDENT, UNINDENT,
        CARET_RIGHT, CARET_LEFT, CARET_UP, CARET_DOWN,
        SELECT_CARET_RIGHT, SELECT_CARET_LEFT, SELECT_CARET_UP, SELECT_CARET_DOWN,
        HOME, SELECT_HOME, END, SELECT_END,
        PAGE_UP, SELECT_PAGE_UP,
        PAGE_DOWN, SELECT_PAGE_DOWN,
        SELECT_ALL,
        COPY, PASTE, CUT,
        UNDO, REDO,
        OPEN, SAVE, SAVE_AS, NEW,
        WRAP,
        COMMAND_PALETTE, FIND,
        ESC, EMPTY,
        ;

        public boolean syncCaret() {
            return !(this == OPEN || this == SAVE || this ==  SAVE_AS ||
                    this ==  NEW || this == ESC || this == FIND ||
                    this == COMMAND_PALETTE || this ==  EMPTY);
        }
    }

    /**
     * Create a new action record.
     * @param type action type
     * @return a new action
     */
    static Action of(Type type) {
        return of(type, "");
    }

    /**
     * Create a new action record with attribute.
     * @param type action type
     * @param attr attribute
     * @return a new action
     */
    static Action of(Type type, String attr) {
        record ActionRecord(Type type, String attr, long occurredAt) implements Action { }
        return new ActionRecord(type, attr, System.currentTimeMillis());
    }
}
