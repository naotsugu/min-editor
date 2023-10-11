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
package com.mammb.code.editor.ui.model.helper;

import javafx.scene.input.DataFormat;

import java.util.Map;

/**
 * Clipboard utilities.
 * @author Naotsugu Kobayashi
 */
public class Clipboards {

    /**
     * Put the text to clipboard.
     * @param text the text
     */
    public static void put(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        javafx.scene.input.Clipboard.getSystemClipboard()
            .setContent(Map.of(DataFormat.PLAIN_TEXT, text));
    }


    /**
     * Get the text from clipboard.
     * @return the text (will not be null)
     */
    public static String get() {
        var clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        return clipboard.hasString() ? clipboard.getString() : "";
    }

}
