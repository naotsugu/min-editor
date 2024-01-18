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

import com.mammb.code.editor.ui.model.editing.UpperCaseEditing;

/**
 * Editing.
 * @author Naotsugu Kobayashi
 */
public interface Editing {

    /**
     * Apply editing.
     * @param model
     * @param query
     */
    void apply(EditorModel model, EditorQuery query);

    /**
     * Create to upper case editing.
     * @return the Editing
     */
    static Editing upperCaseOf() {
        return new UpperCaseEditing();
    }

}
