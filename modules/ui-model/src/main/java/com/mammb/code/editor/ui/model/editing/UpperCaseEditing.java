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

import com.mammb.code.editor.ui.model.Editing;
import com.mammb.code.editor.ui.model.EditorModel;
import com.mammb.code.editor.ui.model.EditorQuery;

/**
 * Upper case editing.
 * @author Naotsugu Kobayashi
 */
public class UpperCaseEditing implements Editing {

    @Override
    public void apply(EditorModel model, EditorQuery query) {
        String text = query.selectedText();
        if (text.isBlank()) {
            return;
        }
        String upper = text.toUpperCase();
        if (!text.equals(upper)) {
            model.input(upper);
        } else {
            String lower = text.toLowerCase();
            if (!text.equals(lower)) {
                model.input(lower);
            }
        }
    }

}
