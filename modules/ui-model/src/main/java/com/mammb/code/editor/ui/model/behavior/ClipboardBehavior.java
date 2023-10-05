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
package com.mammb.code.editor.ui.model.behavior;

import com.mammb.code.editor.model.edit.Edit;
import com.mammb.code.editor.model.text.Textual;
import com.mammb.code.editor.ui.model.impl.Editor;
import javafx.scene.input.DataFormat;
import java.util.Map;

/**
 * ClipboardBehavior.
 * @author Naotsugu Kobayashi
 */
public class ClipboardBehavior {

    public static void pasteFromClipboard(Editor self) {
        self.input(get());
    }

    public static void copyToClipboard(Editor self) {
        copyToClipboard(self, false);
    }

    public static void cutToClipboard(Editor self) {
        copyToClipboard(self, true);
    }

    /**
     * Copy the selection text to the clipboard.
     * @param cut need cut?
     */
    private static void copyToClipboard(Editor self, boolean cut) {
        if (self.selection().length() > 0) {
            Textual text = self.buffer().subText(self.selection().min(), self.selection().length());
            put(text.text());
            if (cut && !self.buffer().readOnly()) {
                self.buffer().push(Edit.delete(text.point(), text.text()));
                self.selection().clear();
                self.caret().at(text.point().offset(), true);
                self.texts().markDirty();
                self.caret().markDirty();
            }
        }
    }


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
