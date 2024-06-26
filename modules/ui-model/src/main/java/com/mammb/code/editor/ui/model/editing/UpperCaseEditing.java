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
import com.mammb.code.editor.ui.model.ModelQuery;

/**
 * Upper case editing.
 * @author Naotsugu Kobayashi
 */
public class UpperCaseEditing implements Editing {

    @Override
    public boolean apply(EditorModel model) {
        String text = model.query(ModelQuery.selectedText);
        if (text.isBlank()) {
            return false;
        }
        String upper = text.toUpperCase();
        if (!text.equals(upper)) {
            model.selectionReplace(upper, false);
            return true;
        } else {
            String lower = text.toLowerCase();
            if (!text.equals(lower)) {
                model.selectionReplace(lower, false);
                return true;
            }
        }
        return false;
    }

}
