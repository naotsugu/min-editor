/*
 * Copyright 2023-2025 the original author or authors.
 * <p>
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

import com.mammb.code.editor.core.EditorModel;
import javax.swing.*;
import java.awt.*;

public class EditorPane extends JPanel {

    /** The canvas. */
    private final Canvas canvas;
    /** The editor model. */
    private EditorModel model;

    public EditorPane() {
        this.canvas = new Canvas();
        this.canvas.setBackground(Color.WHITE);
    }
}
