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
package com.mammb.code.editor2.model.edit;

import com.mammb.code.editor2.model.core.PointText;
import com.mammb.code.editor2.model.edit.impl.EmptyEdit;
import com.mammb.code.editor2.model.edit.impl.MonoEdit;

/**
 * Edit.
 * @author Naotsugu Kobayashi
 */
public interface Edit extends PointText {

    /** The empty edit. */
    Edit empty = new EmptyEdit();


    /**
     * Get the edit type.
     * @return the edit type.
     */
    EditType type();


    /**
     * Get the occurredOn.
     * @return the occurredOn.
     */
    long occurredOn();


    /**
     * Get whether this edit is insert type.
     * @return {@code true}, if this edit is insert type
     */
    default boolean isInsert() { return type().isInsert(); }


    /**
     * Get whether this edit is delete type.
     * @return {@code true}, if this edit is delete type
     */
    default boolean isDelete() { return type().isDelete(); }


    /**
     * Get whether this edit is nil type.
     * @return {@code true}, if this edit is nil type
     */
    default boolean isEmpty() { return type().isNil(); }


    /**
     * Create the insert edit.
     * @param pointText the point text
     * @return the Edit
     */
    static Edit insert(PointText pointText) {
        return new MonoEdit(EditType.INSERT, pointText, System.currentTimeMillis());
    }


    /**
     * Create the deleted edit.
     * @param pointText the point text
     * @return the Edit
     */
    static Edit delete(PointText pointText) {
        return new MonoEdit(EditType.DELETE, pointText, System.currentTimeMillis());
    }


    /**
     * Get whether other edit can be merged into this edit.
     * @param other the merging edit
     * @return {@code true} if other edit can be merged into this edit.
     */
    boolean canMerge(Edit other);


    /**
     * Merge other edit into this edit.
     * @param other the merging edit
     * @return the merged edit
     */
    Edit merge(Edit other);

}
