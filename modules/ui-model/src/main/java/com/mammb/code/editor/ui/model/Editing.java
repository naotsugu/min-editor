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
package com.mammb.code.editor.ui.model;

import com.mammb.code.editor.ui.model.editing.LineBreakEditing;
import com.mammb.code.editor.ui.model.editing.PassThroughInput;
import com.mammb.code.editor.ui.model.editing.TabEditing;
import com.mammb.code.editor.ui.model.editing.UpperCaseEditing;

/**
 * Editing.
 * @author Naotsugu Kobayashi
 */
public interface Editing {

    /** The empty editing. */
    Editing empty = (model, query) -> false;

    /** Editing input. */
    record Input(String string) {};

    /**
     * Apply editing.
     * @param model the editor model
     * @param query the editor query
     */
    boolean apply(EditorModel model, EditorQuery query);

    /**
     * Create to upper case editing.
     * @return the Editing
     */
    static Editing upperCase() {
        return new UpperCaseEditing();
    }

    /**
     * Create a key typed steal editing.
     * @param ch the typed text
     * @return a key typed steal editing
     */
    static Editing keyTypedSteal(String ch) {
        return (m, q) ->
                LineBreakEditing.of(ch).apply(m, q) ||
                TabEditing.of(ch).apply(m, q) ||
                PassThroughInput.of(ch).apply(m, q)
            ;
    }

}
