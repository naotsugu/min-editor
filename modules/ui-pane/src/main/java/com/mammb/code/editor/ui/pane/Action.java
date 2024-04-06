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
package com.mammb.code.editor.ui.pane;

/**
 * The action event.
 * @author Naotsugu Kobayashi
 */
public interface Action {

    /** The action event type.*/
    enum Type {

        CARET_RIGHT, SELECT_CARET_RIGHT,
        CARET_LEFT, SELECT_CARET_LEFT,
        CARET_UP, SELECT_CARET_UP,
        CARET_DOWN, SELECT_CARET_DOWN,
        PAGE_UP, SELECT_PAGE_UP,
        PAGE_DOWN, SELECT_PAGE_DOWN,
        HOME, SELECT_HOME,
        END, SELECT_END,
        SELECT_ALL,
        SCROLL_UP, SCROLL_DOWN,

        TYPED, DELETE, BACK_SPACE, ESCAPE,
        COPY, PASTE, CUT,
        UNDO, REDO,
        UPPER, LOWER, UNIQUE, SORT, HEX, CALC,
        INDENT, UN_INDENT,
        OPEN, SAVE, SAVE_AS, NEW,
        REPEAT,
        WRAP, DEBUG, EMPTY,
        ;
    }


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


    /**
     * Get repeatability.
     * @return {@code true} if this type is repeatability
     */
    default boolean isRepeatable() {
        return switch (type()) {
            case Type.OPEN,
                 Type.SAVE,
                 Type.SAVE_AS,
                 Type.NEW,
                 Type.REPEAT,
                 Type.WRAP,
                 Type.DEBUG,
                 Type.EMPTY -> false;
            default -> true;
        };
    }


    /**
     * Create a new action event.
     * @param type the action event type
     */
    static Action of(Type type) {
        return new ActionRecord(type);
    }


    /**
     * Create a new action event.
     * @param type the action event type
     * @param attr the action event attribute
     */
    static Action of(Type type, String attr) {
        return new ActionRecord(type, attr);
    }

}
