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
package com.mammb.code.editor.ui.swing;

import com.mammb.code.editor.core.Clipboard;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

/**
 * The SgClipboard.
 * @author Naotsugu Kobayashi
 */
public class SgClipboard implements Clipboard {

    /** The swing clipboard instance. */
    static final Clipboard instance = new SgClipboard();

    private SgClipboard() { }

    @Override
    public void setPlainText(String text) {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(text), null);
    }

    @Override
    public String getString() {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception ignored) { }
        }
        return "";
    }

    @Override
    public String getHtml() {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.allHtmlFlavor)) {
            try {
                return (String) contents.getTransferData(DataFlavor.allHtmlFlavor);
            } catch (Exception ignored) { }
        }
        return "";
    }

    @Override
    public List<File> getFiles() {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                @SuppressWarnings("unchecked")
                var list = (List<File>) contents.getTransferData(DataFlavor.javaFileListFlavor);
                return list;
            } catch (Exception ignored) { }
        }
        return List.of();
    }

    @Override
    public boolean hasContents() {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        return contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean hasImage() {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        return contents != null && contents.isDataFlavorSupported(DataFlavor.imageFlavor);
    }

    @Override
    public boolean hasFiles() {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        return contents != null && contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

}
