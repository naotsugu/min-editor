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
import java.util.Objects;

/**
 * Hex case editing.
 * @author Naotsugu Kobayashi
 */
public class HexCaseEditing implements Editing {

    @Override
    public boolean apply(EditorModel model) {
        String text = model.query(ModelQuery.selectedText);
        if (text.isBlank()) {
            return false;
        }
        String trimmed = text.trim();
        if (trimmed.startsWith("0x")) {
            if (trimmed.length() > 2 &&
                isHexLike(trimmed.substring(2))) {
                model.selectionReplace(String.valueOf(Integer.parseInt(trimmed.substring(2), 16)));
                return true;
            } else {
                return false;
            }
        }
        if (isDecimalLike(trimmed)) {
            model.selectionReplace(STR."0x\{Integer.toHexString(Integer.parseInt(trimmed))}");
            return true;
        }
        if (isHexLike(trimmed)) {
            model.selectionReplace(String.valueOf(Integer.parseInt(trimmed, 16)));
            return true;
        }

        return false;
    }


    private boolean isDecimalLike(String text) {
        Objects.requireNonNull(text);
        text = text.trim();
        if (text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (i == 0 && ch == '0') {
                return false;
            }
            if ((ch >= '0' && ch <= '9') || ch == '_') {
                continue;
            }
            return false;
        }
        return true;
    }


    private boolean isHexLike(String text) {
        Objects.requireNonNull(text);
        text = text.trim().toLowerCase();
        if (text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || ch == ' ') {
                continue;
            }
            return false;
        }
        return true;
    }

    private boolean isBinaryLike(String text) {
        Objects.requireNonNull(text);
        text = text.trim();
        if (text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '0' || ch == '1' || ch == ' ') {
                continue;
            }
            return false;
        }
        return true;
    }

}
