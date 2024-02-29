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
 * Tab editing.
 * @author Naotsugu Kobayashi
 */
public class TabEditing implements Editing {

    private final Input input;


    /**
     * Constructor.
     * @param input
     */
    private TabEditing(Input input) {
        this.input = input;
    }


    /**
     * Create a new editing.
     * @param text the input text
     * @return a new editing
     */
    public static Editing of(String text) {
        return "\t".equals(text)
            ? new TabEditing(new Input(text))
            : Editing.empty;
    }


    @Override
    public boolean apply(EditorModel model, Queryable query) {
        var text = query.apply(ModelQuery.caretBeforeText());
        for (int i = text.length() - 1; i >= 0; i--) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                break;
            }
            if (!Character.isSpaceChar(ch)) {
                return false;
            }
        }
        model.input("    ");
        return true;
    }

}
