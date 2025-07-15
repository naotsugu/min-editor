/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.fx;

import java.io.File;
import java.util.List;
import java.util.Map;
import javafx.scene.input.DataFormat;
import com.mammb.code.editor.core.Clipboard;

/**
 * The FxClipboard.
 * @author Naotsugu Kobayashi
 */
public final class FxClipboard implements Clipboard {

    /** The fx clipboard instance. */
    static final Clipboard instance = new FxClipboard();

    private FxClipboard() { }

    @Override
    public void setPlainText(String text) {
        var clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        clipboard.setContent(Map.of(DataFormat.PLAIN_TEXT, text));
    }

    @Override
    public String getString() {
        var clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        return clipboard.hasString() ? clipboard.getString() : "";
    }

    @Override
    public String getHtml() {
        var clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        return clipboard.hasHtml() ? clipboard.getHtml() : "";
    }

    @Override
    public List<File> getFiles() {
        var clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        return clipboard.hasFiles() ? clipboard.getFiles() : List.of();
    }

    @Override
    public boolean hasContents() {
        var clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        return clipboard.hasString();
    }

    @Override
    public boolean hasImage() {
        var clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        return clipboard.hasImage();
    }

    @Override
    public boolean hasFiles() {
        var clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        return clipboard.hasFiles();
    }

}
