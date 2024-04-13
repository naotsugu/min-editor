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
 * dec -> hex -> bin
 * @author Naotsugu Kobayashi
 */
public class HexCaseEditing implements Editing {

    @Override
    public boolean apply(EditorModel model) {

        String selected = model.query(ModelQuery.selectedText);
        if (selected.isBlank()) {
            return false;
        }

        String trimmed = selected.trim().toLowerCase().replace(" ", "").replace("_", "");

        if (isDecimalLike(trimmed)) {
            String hex = Integer.toHexString(Integer.parseInt(trimmed));
            if (isDecimalLike(hex) || isBinaryLike(hex)) {
                hex = "0x" + hex;
            }
            model.selectionReplace(hex, false);
            return true;
        }

        if (isHexLike(trimmed)) {
            String text = trimmed.startsWith("0x") ? trimmed.substring(2) : trimmed;
            if (text.isBlank()) {
                return false;
            } else {
                String bin = Integer.toBinaryString(Integer.parseInt(text, 16));
                bin = "0".repeat(bin.length() % 4) + bin;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < bin.length(); i++) {
                    if (i != 0 && (i % 4) == 0) sb.append(" ");
                    sb.append(bin.charAt(i));
                }
                bin = sb.charAt(0) == '1' ? "0b" + sb : sb.toString();
                model.selectionReplace(bin, false);
                return true;
            }
        }

        if (isBinaryLike(trimmed)) {
            String text = trimmed.startsWith("0b") ? trimmed.substring(2) : trimmed;
            if (text.isBlank()) {
                return false;
            } else {
                model.selectionReplace(String.valueOf(Integer.parseInt(text, 2)), false);
                return true;
            }
        }

        return false;
    }


    private boolean isDecimalLike(String text) {
        Objects.requireNonNull(text);
        text = text.trim();
        if (text.isEmpty()) {
            return false;
        }
        if (text.charAt(0) == '0') {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
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
        if (text.startsWith("0x")) {
            return true;
        }
        if (text.charAt(0) == '0') {
            return false;
        }

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || ch == ' ' || ch == '_') {
                continue;
            }
            return false;
        }
        return true;
    }


    private boolean isBinaryLike(String text) {
        Objects.requireNonNull(text);
        text = text.trim().toLowerCase();
        if (text.isEmpty()) {
            return false;
        }
        if (text.startsWith("0b")) {
            return true;
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '0' || ch == '1' || ch == ' ' || ch == '_') {
                continue;
            }
            return false;
        }
        return true;
    }

}
