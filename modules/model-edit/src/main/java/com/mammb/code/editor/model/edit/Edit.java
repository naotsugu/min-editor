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
package com.mammb.code.editor.model.edit;

import com.mammb.code.editor.model.edit.impl.FlushInsertEdit;
import com.mammb.code.editor.model.edit.impl.InsertEdit;
import com.mammb.code.editor.model.edit.impl.ReplaceEdit;
import com.mammb.code.editor.model.edit.impl.BsDeleteEdit;
import com.mammb.code.editor.model.edit.impl.BsInsertEdit;
import com.mammb.code.editor.model.edit.impl.CompoundEdit;
import com.mammb.code.editor.model.edit.impl.DeleteEdit;
import com.mammb.code.editor.model.edit.impl.EmptyEdit;
import com.mammb.code.editor.model.text.OffsetPoint;

/**
 * Edit.
 * @author Naotsugu Kobayashi
 */
public sealed interface Edit extends TextTranslate
        permits InsertEdit, DeleteEdit,
            BsInsertEdit, BsDeleteEdit,
        ReplaceEdit, CompoundEdit,
        FlushInsertEdit, EmptyEdit {

    /** The empty edit. */
    Edit empty = new EmptyEdit();

    /**
     * Get the offset point of edit.
     * @return the offset point of edit
     */
    OffsetPoint point();

    /**
     * Get the char length of edit.
     * @return the char length of edit
     */
    int length();

    /**
     * Get the occurredOn.
     * @return the occurredOn.
     */
    long occurredOn();

    /**
     * Flip the edit.
     * @return the flipped edit.
     */
    Edit flip();

    /**
     * Apply this edit.
     * @param editTo the edit to
     */
    void apply(EditTo editTo);

    /**
     * Get whether other edit can be merged into this edit.
     * @param other the merging edit
     * @return {@code true} if other edit can be merged into this edit.
     */
    default boolean canMerge(Edit other) {
        return other instanceof EmptyEdit;
    }

    /**
     * Merge other edit into this edit.
     * @param other the merging edit
     * @return the merged edit
     */
    default Edit merge(Edit other) {
        if (other instanceof EmptyEdit) {
            return this;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Get whether this edit is a across-row edit or not.
     * @return {@code true}, if this edit is a across-row edit
     */
    boolean acrossRows();

    /**
     * Create the insertion edit.
     * @param point the offset point text
     * @param text the insertion text
     * @return the insertion edit
     */
    static Edit insert(OffsetPoint point, String text) {
        return new InsertEdit(point, text, System.currentTimeMillis());
    }

    /**
     * Create the flush insertion edit.
     * @param point the offset point text
     * @param text the insertion text
     * @return the flush insertion edit
     */
    static Edit insertFlush(OffsetPoint point, String text) {
        return new FlushInsertEdit(point, text, System.currentTimeMillis());
    }

    /**
     * Create the deletion edit.
     * @param point the offset point text
     * @param text the deletion text
     * @return the deletion edit
     */
    static Edit delete(OffsetPoint point, String text) {
        return new DeleteEdit(point, text, System.currentTimeMillis());
    }

    /**
     * Create the backspace edit.
     * @param point the offset point text
     * @param text the deletion text
     * @return the backspace edit
     */
    static Edit backspace(OffsetPoint point, String text) {
        return new BsDeleteEdit(point, text, System.currentTimeMillis());
    }

    /**
     * Create the replacement edit.
     * @param point the offset point text
     * @param beforeText the before text string
     * @param afterTText the after text string
     * @return the replacement edit
     */
    static Edit replace(OffsetPoint point, String beforeText, String afterTText) {
        return new ReplaceEdit(point, beforeText, afterTText, System.currentTimeMillis());
    }

}