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
package com.mammb.code.editor.ui.model.editing;

import com.mammb.code.editor.ui.model.EditorModel;

/**
 * Editing.
 * @author Naotsugu Kobayashi
 */
public interface Editing {

    /** The empty editing. */
    Editing empty = model -> false;

    /** Editing input. */
    record Input(String text) { }

    /**
     * Apply editing.
     * @param model the editor model
     */
    boolean apply(EditorModel model);


    /**
     * Create upper case editing.
     * @return the Editing
     */
    static Editing upperCase() {
        return new UpperCaseEditing();
    }


    /**
     * Create hex case editing.
     * @return the Editing
     */
    static Editing hexCase() {
        return new HexCaseEditing();
    }


    /**
     * Create calc case editing.
     * @return the Editing
     */
    static Editing calcCase() {
        return new CalcCaseEditing();
    }


    /**
     * Create sort editing.
     * @return the Editing
     */
    static Editing sortCase() {
        return new SortEditing();
    }


    /**
     * Create unique editing.
     * @return the Editing
     */
    static Editing uniqueCase() {
        return new UniqueEditing();
    }


    /**
     * Create a key typed steal editing.
     * @param ch the typed text
     * @return a key typed steal editing
     */
    static Editing keyTypedSteal(String ch) {
        return model ->
                LineBreakEditing.of(ch).apply(model) ||
                UnifyTabEditing.of(ch).apply(model) ||
                PassThroughInput.of(ch).apply(model);
    }

}
