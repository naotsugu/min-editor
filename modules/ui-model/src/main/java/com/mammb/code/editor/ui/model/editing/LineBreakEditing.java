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
 * The LineBreakEditing.
 * @author Naotsugu Kobayashi
 */
public class LineBreakEditing implements Editing {

    private final Input input;

    private LineBreakEditing(Input input) {
        this.input = input;
    }

    public static Editing of(String string) {
        if ("\n".equals(string) || "\r\n".equals(string)) {
            return new LineBreakEditing(new Input(string));
        }
        return Editing.empty;
    }

    @Override
    public boolean apply(EditorModel model, EditorQuery query) {
        String row = query.currentRow();
        int spCount = 0;
        int tabCount = 0;
        for (int i = 0; i < row.length(); i++) {
            char ch = row.charAt(i);
            if (ch == ' ') {
                spCount++;
            } else if (ch == '\t') {
                tabCount++;
            } else {
                break;
            }
        }

        if (spCount == 0 && tabCount > 0) {
            model.input(input.text() + "\t".repeat(tabCount));
            return true;
        } else if (spCount > 0) {
            model.input(input.text() + " ".repeat(spCount + tabCount * 4));
            return true;
        }
        return false;
    }

}
