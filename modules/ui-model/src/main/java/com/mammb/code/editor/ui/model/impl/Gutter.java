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
package com.mammb.code.editor.ui.model.impl;

import com.mammb.code.editor.model.layout.TextRun;
import javafx.scene.canvas.GraphicsContext;

/**
 * Gutter.
 * @author Naotsugu Kobayashi
 */
public interface Gutter {

    /**
     * Draw the gutter.
     * @param gc the graphicsContext
     * @param run the textRun
     * @param top the position top
     * @param accent the accent
     */
    void draw(GraphicsContext gc, TextRun run, double top, boolean accent);

    /**
     * Get the width of gutter.
     * @return the width of gutter
     */
    double width();

    boolean checkWidthChanged();

}
